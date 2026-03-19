import { Injectable, NotFoundException, BadRequestException, ServiceUnavailableException } from '@nestjs/common';
import { PrismaService } from '../../common/prisma/prisma.service';
import { GenerateSchemeDto } from './dto/scheme.dto';
import { ConfigService } from '@nestjs/config';
import { Prisma } from '@prisma/client';
import axios from 'axios';
import * as PDFDocument from 'pdfkit';
import * as fs from 'fs';
import * as path from 'path';

interface DecorationGuide {
  summary: string;
  rooms: Array<{
    name: string;
    layout: string;
    devices: string[];
    installationPoints: string[];
    notes: string;
  }>;
  professionalAdvice: string;
}

interface DeviceRecommendation {
  name: string;
  brand: string;
  category: string;
  price: number;
  quantity: number;
  description: string;
  recommendReason: string;
  roomName?: string;
}

interface AIResponse {
  decorationGuide: DecorationGuide;
  devices: DeviceRecommendation[];
  totalPrice: number;
}

@Injectable()
export class SchemeService {
  constructor(
    private prisma: PrismaService,
    private configService: ConfigService,
  ) {}

  async generateScheme(userId: string, dto: GenerateSchemeDto) {
    const userInfo = await this.prisma.userInfo.findUnique({
      where: { userId },
    });

    const houseLayout = await this.prisma.houseLayout.findUnique({
      where: { userId },
      include: {
        rooms: {
          orderBy: { sortOrder: 'asc' },
        },
      },
    });

    if (!userInfo || !houseLayout) {
      throw new BadRequestException('请先完善用户信息和户型信息');
    }

    let aiResponse: AIResponse;
    
    try {
      aiResponse = await this.callDeepSeekAPI(userInfo, houseLayout, dto.budget);
    } catch (error) {
      console.error('DeepSeek API failed, using default scheme:', error.message);
      aiResponse = this.getDefaultScheme(dto.budget, houseLayout);
    }

    const scheme = await this.prisma.scheme.create({
      data: {
        userId,
        name: `智能家居方案 #${Date.now().toString().slice(-6)}`,
        budget: new Prisma.Decimal(dto.budget),
        totalPrice: new Prisma.Decimal(aiResponse.totalPrice),
        decorationGuide: aiResponse.decorationGuide as any,
        status: 'completed',
      },
    });

    if (aiResponse.devices && aiResponse.devices.length > 0) {
      const existingDevices = await this.prisma.device.findMany({
        where: {
          name: { in: aiResponse.devices.map(d => d.name) },
          brand: { in: aiResponse.devices.map(d => d.brand) },
        },
      });

      const existingDeviceMap = new Map(
        existingDevices.map(d => [`${d.name}-${d.brand}`, d])
      );

      const devicesToCreate = aiResponse.devices
        .filter(d => !existingDeviceMap.has(`${d.name}-${d.brand}`))
        .map(d => ({
          name: d.name,
          brand: d.brand,
          category: d.category,
          price: new Prisma.Decimal(d.price),
          description: d.description,
        }));

      if (devicesToCreate.length > 0) {
        const createdDevices = await this.prisma.device.createMany({
          data: devicesToCreate,
          skipDuplicates: true,
        });

        const newDevices = await this.prisma.device.findMany({
          where: {
            name: { in: devicesToCreate.map(d => d.name) },
            brand: { in: devicesToCreate.map(d => d.brand) },
          },
        });

        newDevices.forEach(d => {
          existingDeviceMap.set(`${d.name}-${d.brand}`, d);
        });
      }

      const schemeDevicesData = aiResponse.devices.map((deviceRec, index) => {
        const device = existingDeviceMap.get(`${deviceRec.name}-${deviceRec.brand}`);
        if (!device) {
          return null;
        }
        return {
          schemeId: scheme.id,
          deviceId: device.id,
          roomName: deviceRec.roomName,
          quantity: deviceRec.quantity,
          price: device.price,
          recommendReason: deviceRec.recommendReason,
          sortOrder: index,
        };
      }).filter((sd): sd is NonNullable<typeof sd> => sd !== null);

      if (schemeDevicesData.length > 0) {
        await this.prisma.schemeDevice.createMany({
          data: schemeDevicesData,
        });
      }
    }

    return this.getSchemeDetail(scheme.id, userId);
  }

  async getSchemeDetail(schemeId: string, userId: string) {
    const scheme = await this.prisma.scheme.findFirst({
      where: {
        id: schemeId,
        userId,
      },
      include: {
        schemeDevices: {
          include: {
            device: true,
          },
          orderBy: { sortOrder: 'asc' },
        },
      },
    });

    if (!scheme) {
      throw new NotFoundException('方案不存在');
    }

    return {
      id: scheme.id,
      name: scheme.name,
      budget: scheme.budget,
      totalPrice: scheme.totalPrice,
      status: scheme.status,
      decorationGuide: scheme.decorationGuide,
      devices: scheme.schemeDevices.map((sd) => ({
        id: sd.device.id,
        name: sd.device.name,
        brand: sd.device.brand,
        category: sd.device.category,
        price: sd.price,
        quantity: sd.quantity,
        description: sd.device.description,
        recommendReason: sd.recommendReason,
        imageUrl: sd.device.imageUrl,
        taobaoUrl: sd.device.taobaoUrl,
      })),
      createdAt: scheme.createdAt,
    };
  }

  async saveScheme(schemeId: string, userId: string) {
    const scheme = await this.prisma.scheme.findFirst({
      where: { id: schemeId, userId },
    });

    if (!scheme) {
      throw new NotFoundException('方案不存在');
    }

    const savedCount = await this.prisma.scheme.count({
      where: { userId, isSaved: true },
    });

    if (savedCount >= 3 && !scheme.isSaved) {
      throw new BadRequestException('最多只能保存3个方案');
    }

    const updated = await this.prisma.scheme.update({
      where: { id: schemeId },
      data: {
        isSaved: true,
        savedAt: new Date(),
        status: 'saved',
      },
    });

    return {
      id: updated.id,
      savedAt: updated.savedAt,
    };
  }

  async deleteScheme(schemeId: string, userId: string) {
    const scheme = await this.prisma.scheme.findFirst({
      where: { id: schemeId, userId },
    });

    if (!scheme) {
      throw new NotFoundException('方案不存在');
    }

    await this.prisma.scheme.delete({
      where: { id: schemeId },
    });

    return null;
  }

  async exportSchemePdf(schemeId: string, userId: string) {
    const scheme = await this.prisma.scheme.findFirst({
      where: { id: schemeId, userId },
      include: {
        schemeDevices: {
          include: {
            device: true,
          },
          orderBy: { sortOrder: 'asc' },
        },
      },
    });

    if (!scheme) {
      throw new NotFoundException('方案不存在');
    }

    const uploadDir = process.env.UPLOAD_DIR || path.join(process.cwd(), 'uploads', 'pdf');
    if (!fs.existsSync(uploadDir)) {
      fs.mkdirSync(uploadDir, { recursive: true });
    }

    const dateStr = new Date().toISOString().slice(0, 10).replace(/-/g, '');
    const fileName = `智能家居方案_${dateStr}.pdf`;
    const filePath = path.join(uploadDir, `${schemeId}.pdf`);

    return new Promise((resolve, reject) => {
      const doc = new PDFDocument({ margin: 50 });
      const writeStream = fs.createWriteStream(filePath);
      
      doc.pipe(writeStream);

      doc.fontSize(24).text('智能家居方案', { align: 'center' });
      doc.moveDown();
      doc.fontSize(14).text(`方案名称: ${scheme.name}`, { align: 'left' });
      doc.text(`预算: ¥${scheme.budget}`, { align: 'left' });
      doc.text(`总价: ¥${scheme.totalPrice}`, { align: 'left' });
      doc.text(`生成时间: ${scheme.createdAt.toLocaleDateString()}`, { align: 'left' });
      doc.moveDown(2);

      const decorationGuide = scheme.decorationGuide as any;
      if (decorationGuide) {
        doc.fontSize(18).text('装修指南', { underline: true });
        doc.moveDown();
        
        if (decorationGuide.summary) {
          doc.fontSize(12).text(decorationGuide.summary);
          doc.moveDown();
        }

        if (decorationGuide.rooms && decorationGuide.rooms.length > 0) {
          doc.fontSize(14).text('房间详情:');
          doc.moveDown(0.5);
          
          for (const room of decorationGuide.rooms) {
            doc.fontSize(12).text(`• ${room.name}:`);
            doc.text(`  布局: ${room.layout}`);
            if (room.devices && room.devices.length > 0) {
              doc.text(`  设备: ${room.devices.join(', ')}`);
            }
            if (room.installationPoints && room.installationPoints.length > 0) {
              doc.text(`  安装要点: ${room.installationPoints.join(', ')}`);
            }
            if (room.notes) {
              doc.text(`  备注: ${room.notes}`);
            }
            doc.moveDown(0.5);
          }
        }

        if (decorationGuide.professionalAdvice) {
          doc.moveDown();
          doc.fontSize(14).text('专业建议:', { underline: true });
          doc.fontSize(12).text(decorationGuide.professionalAdvice);
        }
      }

      if (scheme.schemeDevices && scheme.schemeDevices.length > 0) {
        doc.moveDown(2);
        doc.fontSize(18).text('设备清单', { underline: true });
        doc.moveDown();
        
        let totalPrice = 0;
        scheme.schemeDevices.forEach((sd, index) => {
          const device = sd.device;
          const itemTotal = Number(device.price) * sd.quantity;
          totalPrice += itemTotal;
          
          doc.fontSize(12).text(`${index + 1}. ${device.name} (${device.brand})`);
          doc.text(`   类别: ${device.category}`);
          doc.text(`   价格: ¥${device.price} × ${sd.quantity} = ¥${itemTotal}`);
          doc.text(`   说明: ${device.description || '无'}`);
          doc.moveDown(0.5);
        });
        
        doc.moveDown();
        doc.fontSize(14).text(`设备总价: ¥${totalPrice}`);
      }

      doc.end();

      writeStream.on('finish', () => {
        const baseUrl = process.env.APP_URL || 'https://api.smarthome.com';
        resolve({
          pdfUrl: `${baseUrl}/v1/uploads/pdf/${schemeId}.pdf`,
          fileName: fileName,
          expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000),
        });
      });

      writeStream.on('error', (error) => {
        reject(error);
      });
    });
  }

  private async callDeepSeekAPI(
    userInfo: any,
    houseLayout: any,
    budget: number,
  ): Promise<AIResponse> {
    const apiKey = this.configService.get<string>('DEEPSEEK_API_KEY');
    const baseUrl = this.configService.get<string>(
      'DEEPSEEK_BASE_URL',
      'https://api.deepseek.com',
    );

    if (!apiKey) {
      throw new ServiceUnavailableException('AI服务暂不可用，请稍后再试');
    }

    const prompt = this.buildPrompt(userInfo, houseLayout, budget);

    const response = await axios.post(
      `${baseUrl}/v1/chat/completions`,
      {
        model: 'deepseek-chat',
        messages: [
          {
            role: 'system',
            content:
              '你是一位专业的智能家居方案设计师。请根据用户信息生成个性化的智能家居方案，返回JSON格式的结果。',
          },
          {
            role: 'user',
            content: prompt,
          },
        ],
        response_format: { type: 'json_object' },
      },
      {
        headers: {
          Authorization: `Bearer ${apiKey}`,
          'Content-Type': 'application/json',
        },
        timeout: 30000,
      },
    );

    const content = response.data.choices[0].message.content;
    const result = JSON.parse(content);
    
    if (!result.devices || !result.decorationGuide) {
      throw new ServiceUnavailableException('AI响应格式错误，请稍后再试');
    }
    
    return result;
  }

  private buildPrompt(userInfo: any, houseLayout: any, budget: number): string {
    return `
请为以下用户生成智能家居方案，预算${budget}元：

用户信息：
- 年龄段：${userInfo.age}
- 职业：${userInfo.occupation}
- 家庭成员：${JSON.stringify(userInfo.familyMembers)}
- 城市：${userInfo.city}
- 作息：${userInfo.sleepPattern}
- 在家活动：${JSON.stringify(userInfo.homeActivities)}
- 娱乐习惯：${JSON.stringify(userInfo.entertainmentHabits)}
- 智能设备了解程度：${userInfo.deviceKnowledgeLevel}
- 使用过的设备：${JSON.stringify(userInfo.usedDevices)}
- 装修风格偏好：${userInfo.decorStyle}
- 颜色偏好：${JSON.stringify(userInfo.colorPreferences)}
- 品牌偏好：${JSON.stringify(userInfo.preferredBrands)}

户型信息：
- 房屋类型：${houseLayout.houseType}
- 建筑面积：${houseLayout.totalArea}平方米
- 房间：${JSON.stringify(houseLayout.rooms)}

请返回JSON格式：
{
  "decorationGuide": {
    "summary": "整体布局建议",
    "rooms": [{"name": "房间名", "layout": "布局建议", "devices": ["设备列表"], "installationPoints": ["安装要点"], "notes": "注意事项"}],
    "professionalAdvice": "专业建议"
  },
  "devices": [{"name": "设备名", "brand": "品牌", "category": "分类", "price": 价格, "quantity": 数量, "description": "描述", "recommendReason": "推荐理由", "roomName": "所属房间"}],
  "totalPrice": 总价
}
`;
  }

  private getDefaultScheme(
    budget: number,
    houseLayout: any,
  ): AIResponse {
    const rooms = houseLayout.rooms || [{ name: '客厅', area: 30 }];
    const devices: DeviceRecommendation[] = [];

    for (const room of rooms) {
      if (room.name === '客厅') {
        devices.push(
          {
            name: '智能吸顶灯',
            brand: '小米/米家',
            category: '照明',
            price: 299,
            quantity: 1,
            description: '支持亮度调节、色温调节',
            recommendReason: '性价比高，支持米家生态',
            roomName: '客厅',
          },
          {
            name: '智能音箱',
            brand: '小米/米家',
            category: '语音控制',
            price: 199,
            quantity: 1,
            description: '语音控制中枢',
            recommendReason: '入门级智能音箱，支持语音控制',
            roomName: '客厅',
          },
        );
      } else if (room.name.includes('卧')) {
        devices.push({
          name: '智能吸顶灯',
          brand: '小米/米家',
          category: '照明',
          price: 199,
          quantity: 1,
          description: '卧室智能灯',
          recommendReason: '适合卧室使用',
          roomName: room.name,
        });
      }
    }

    const totalPrice = devices.reduce((sum, d) => sum + d.price * d.quantity, 0);

    return {
      decorationGuide: {
        summary: '根据您的户型和预算，为您推荐以下智能家居方案。',
        rooms: rooms.map((room: any) => ({
          name: room.name,
          layout: `建议在${room.name}安装智能设备`,
          devices: devices
            .filter((d) => d.roomName === room.name)
            .map((d) => d.name),
          installationPoints: ['请预留电源插座'],
          notes: '注意设备安装位置',
        })),
        professionalAdvice: '建议优先考虑设备兼容性，选择同一生态品牌。',
      },
      devices,
      totalPrice: Math.min(totalPrice, budget),
    };
  }
}
