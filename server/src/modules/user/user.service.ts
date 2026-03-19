import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../../common/prisma/prisma.service';
import { UpdateUserDto } from './dto/user.dto';

@Injectable()
export class UserService {
  constructor(private prisma: PrismaService) {}

  async getCurrentUser(userId: string) {
    const user = await this.prisma.user.findUnique({
      where: { id: userId },
      select: {
        id: true,
        deviceId: true,
        nickname: true,
        avatar: true,
        createdAt: true,
        updatedAt: true,
      },
    });

    if (!user) {
      throw new NotFoundException('用户不存在');
    }

    return user;
  }

  async updateCurrentUser(userId: string, dto: UpdateUserDto) {
    const user = await this.prisma.user.update({
      where: { id: userId },
      data: {
        ...dto,
        updatedAt: new Date(),
      },
      select: {
        id: true,
        nickname: true,
        avatar: true,
        updatedAt: true,
      },
    });

    return user;
  }

  async getUserSchemes(userId: string) {
    const schemes = await this.prisma.scheme.findMany({
      where: {
        userId,
        isSaved: true,
      },
      orderBy: {
        createdAt: 'desc',
      },
      take: 3,
      select: {
        id: true,
        name: true,
        budget: true,
        totalPrice: true,
        createdAt: true,
        _count: {
          select: { schemeDevices: true },
        },
      },
    });

    return {
      list: schemes.map((scheme) => ({
        id: scheme.id,
        name: scheme.name,
        budget: scheme.budget,
        totalPrice: scheme.totalPrice,
        deviceCount: scheme._count.schemeDevices,
        createdAt: scheme.createdAt,
      })),
      total: schemes.length,
      maxAllowed: 3,
    };
  }
}
