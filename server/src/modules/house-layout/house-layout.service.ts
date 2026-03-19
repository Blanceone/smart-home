import { Injectable } from '@nestjs/common';
import { PrismaService } from '../../common/prisma/prisma.service';
import { SaveHouseLayoutDto } from './dto/house-layout.dto';
import { Prisma } from '@prisma/client';

@Injectable()
export class HouseLayoutService {
  constructor(private prisma: PrismaService) {}

  async saveHouseLayout(userId: string, dto: SaveHouseLayoutDto) {
    const existingLayout = await this.prisma.houseLayout.findUnique({
      where: { userId },
    });

    if (existingLayout) {
      await this.prisma.room.deleteMany({
        where: { layoutId: existingLayout.id },
      });
    }

    const layout = await this.prisma.houseLayout.upsert({
      where: { userId },
      create: {
        userId,
        houseType: dto.houseType,
        totalArea: new Prisma.Decimal(dto.totalArea),
        rooms: {
          create: dto.rooms.map((room, index) => ({
            name: room.name,
            area: new Prisma.Decimal(room.area),
            specialNeeds: room.specialNeeds,
            sortOrder: index,
          })),
        },
      },
      update: {
        houseType: dto.houseType,
        totalArea: new Prisma.Decimal(dto.totalArea),
        updatedAt: new Date(),
        rooms: {
          create: dto.rooms.map((room, index) => ({
            name: room.name,
            area: new Prisma.Decimal(room.area),
            specialNeeds: room.specialNeeds,
            sortOrder: index,
          })),
        },
      },
      include: {
        rooms: {
          orderBy: { sortOrder: 'asc' },
        },
      },
    });

    return {
      id: layout.id,
      userId: layout.userId,
      houseType: layout.houseType,
      totalArea: layout.totalArea,
      roomCount: layout.rooms.length,
      createdAt: layout.createdAt,
    };
  }

  async getHouseLayout(userId: string) {
    const layout = await this.prisma.houseLayout.findUnique({
      where: { userId },
      include: {
        rooms: {
          orderBy: { sortOrder: 'asc' },
        },
      },
    });

    if (!layout) {
      return null;
    }

    return {
      id: layout.id,
      houseType: layout.houseType,
      totalArea: layout.totalArea,
      rooms: layout.rooms.map((room) => ({
        id: room.id,
        name: room.name,
        area: room.area,
        specialNeeds: room.specialNeeds,
      })),
      createdAt: layout.createdAt,
      updatedAt: layout.updatedAt,
    };
  }
}
