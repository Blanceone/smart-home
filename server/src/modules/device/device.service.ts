import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../../common/prisma/prisma.service';
import { SearchDeviceDto } from './dto/device.dto';
import { Prisma } from '@prisma/client';
import { TaobaoService } from '../../common/services/taobao.service';

@Injectable()
export class DeviceService {
  constructor(
    private prisma: PrismaService,
    private taobaoService: TaobaoService,
  ) {}

  async getDeviceDetail(deviceId: string) {
    const device = await this.prisma.device.findUnique({
      where: { id: deviceId },
    });

    if (!device) {
      throw new NotFoundException('设备不存在');
    }

    return {
      id: device.id,
      name: device.name,
      brand: device.brand,
      category: device.category,
      price: device.price,
      originalPrice: device.originalPrice,
      description: device.description,
      features: device.features,
      specifications: device.specifications,
      applicableScenes: device.applicableScenes,
      imageUrl: device.imageUrl,
      images: device.images,
      taobaoUrl: device.taobaoUrl,
      priceUpdatedAt: device.priceUpdatedAt,
    };
  }

  async getDevicePurchaseUrl(deviceId: string) {
    const device = await this.prisma.device.findUnique({
      where: { id: deviceId },
    });

    if (!device) {
      throw new NotFoundException('设备不存在');
    }

    if (this.taobaoService.isConfigured() && device.taobaoItemId) {
      try {
        const purchaseInfo = await this.taobaoService.generatePurchaseUrl(
          device.taobaoItemId,
          device.name,
        );
        
        await this.prisma.device.update({
          where: { id: deviceId },
          data: {
            price: new Prisma.Decimal(purchaseInfo.price),
            priceUpdatedAt: new Date(),
          },
        });

        return {
          purchaseUrl: purchaseInfo.purchaseUrl,
          price: purchaseInfo.price,
          originalPrice: purchaseInfo.originalPrice,
          coupon: purchaseInfo.coupon,
          expiresAt: purchaseInfo.expiresAt,
        };
      } catch (error) {
        console.error('Failed to get Taobao price:', error.message);
      }
    }

    return {
      purchaseUrl: device.taobaoUrl || `https://s.click.taobao.com/item/${device.taobaoItemId}`,
      price: device.price,
      coupon: null,
      expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000),
    };
  }

  async searchDevices(dto: SearchDeviceDto) {
    const page = dto.page || 1;
    const pageSize = Math.min(dto.pageSize || 20, 100);

    const where: Prisma.DeviceWhereInput = {
      status: 1,
    };

    if (dto.keyword) {
      where.name = { contains: dto.keyword, mode: 'insensitive' };
    }

    if (dto.category) {
      where.category = dto.category;
    }

    if (dto.brand) {
      where.brand = dto.brand;
    }

    if (dto.minPrice !== undefined || dto.maxPrice !== undefined) {
      where.price = {};
      if (dto.minPrice !== undefined) {
        where.price.gte = new Prisma.Decimal(dto.minPrice);
      }
      if (dto.maxPrice !== undefined) {
        where.price.lte = new Prisma.Decimal(dto.maxPrice);
      }
    }

    const [devices, total] = await Promise.all([
      this.prisma.device.findMany({
        where,
        skip: (page - 1) * pageSize,
        take: pageSize,
        orderBy: { createdAt: 'desc' },
        select: {
          id: true,
          name: true,
          brand: true,
          category: true,
          price: true,
          imageUrl: true,
        },
      }),
      this.prisma.device.count({ where }),
    ]);

    return {
      list: devices,
      pagination: {
        page,
        pageSize,
        total,
        totalPages: Math.ceil(total / pageSize),
      },
    };
  }
}
