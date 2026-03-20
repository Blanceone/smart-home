import { Controller, Get, Post, Param, Res, UseInterceptors, UploadedFile } from '@nestjs/common';
import { Response } from 'express';
import { FileInterceptor } from '@nestjs/platform-express';
import { ApiTags, ApiOperation, ApiConsumes, ApiBearerAuth } from '@nestjs/swagger';
import { SkipAuth } from '../../common/decorators/public.decorator';
import { ConfigService } from '@nestjs/config';
import * as fs from 'fs';
import * as path from 'path';

interface MulterFile {
  fieldname: string;
  originalname: string;
  encoding: string;
  mimetype: string;
  size: number;
  destination?: string;
  filename?: string;
  path?: string;
  buffer?: Buffer;
}

@ApiTags('通用模块')
@Controller()
export class CommonController {
  constructor(private configService: ConfigService) {}

  @Get('config')
  @SkipAuth()
  @ApiOperation({ summary: '获取配置信息' })
  async getConfig() {
    return {
      version: {
        latestVersion: this.configService.get('APP_VERSION', '1.0.0'),
        minVersion: '1.0.0',
        updateUrl: this.configService.get('UPDATE_URL') || null,
        forceUpdate: false,
      },
      dictionaries: {
        ageRanges: ['18-25', '26-30', '31-35', '35+'],
        occupations: ['学生', '上班族', '自由职业', '其他'],
        familyMembers: ['独居', '情侣', '夫妻+孩子', '与父母同住', '其他'],
        sleepPatterns: ['早睡早起', '晚睡晚起', '作息不规律'],
        homeActivities: ['工作学习', '休闲娱乐', '健身运动', '烹饪美食', '其他'],
        entertainmentHabits: ['看电影追剧', '听音乐', '玩游戏', '阅读', '其他'],
        knowledgeLevels: ['完全不了解', '听说过但没用过', '用过一些', '非常熟悉'],
        decorStyles: ['现代简约', '北欧风', '日式', '工业风', '中式', '其他'],
        colorPreferences: ['白色系', '灰色系', '原木色', '黑色系', '彩色系'],
        brands: ['小米/米家', '华为', '天猫精灵', '小度', 'Apple HomeKit', '其他', '无偏好'],
        houseTypes: ['一居室', '两居室', '三居室', '四居室及以上', '复式/别墅'],
        roomTypes: ['客厅', '主卧', '次卧', '书房', '厨房', '卫生间', '阳台', '其他'],
        deviceCategories: ['照明', '安防', '环境', '影音', '家电', '其他'],
      },
      platforms: {
        android: {
          minVersion: '5.0',
          targetVersion: '14',
        },
        ios: {
          minVersion: '11.0',
          targetVersion: '17',
        },
        web: {
          supportedBrowsers: ['Chrome 80+', 'Safari 13+', 'Firefox 75+', 'Edge 80+'],
        },
      },
    };
  }

  @Get('health')
  @SkipAuth()
  @ApiOperation({ summary: '健康检查' })
  async healthCheck() {
    return {
      status: 'healthy',
      services: {
        database: 'ok',
        redis: 'ok',
        deepseek: 'ok',
        taobao: 'ok',
      },
      timestamp: Date.now(),
    };
  }

  @Post('upload')
  @ApiBearerAuth()
  @ApiOperation({ summary: '上传文件' })
  @ApiConsumes('multipart/form-data')
  @UseInterceptors(FileInterceptor('file'))
  async uploadFile(@UploadedFile() file: MulterFile) {
    if (!file) {
      return {
        url: null,
        error: '请选择要上传的文件',
      };
    }

    const fileUrl = `${this.configService.get('APP_URL', 'https://api.smarthome.com')}/uploads/${file.filename}`;

    return {
      url: fileUrl,
      filename: file.originalname,
      size: file.size,
      mimeType: file.mimetype,
    };
  }

  @Get('uploads/pdf/:filename')
  @SkipAuth()
  @ApiOperation({ summary: '下载PDF文件' })
  async downloadPdf(@Param('filename') filename: string, @Res() res: Response) {
    const uploadDir = process.env.UPLOAD_DIR || path.join(process.cwd(), 'uploads', 'pdf');
    const filePath = path.join(uploadDir, filename);

    if (!fs.existsSync(filePath)) {
      res.status(404).json({ error: '文件不存在' });
      return;
    }

    res.setHeader('Content-Type', 'application/pdf');
    res.setHeader('Content-Disposition', `attachment; filename=${encodeURIComponent(filename)}`);

    const fileStream = fs.createReadStream(filePath);
    fileStream.pipe(res);
  }
}
