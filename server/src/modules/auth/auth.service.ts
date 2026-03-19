import { Injectable } from '@nestjs/common';
import { PrismaService } from '../../common/prisma/prisma.service';
import { RegisterDto } from './dto/auth.dto';

@Injectable()
export class AuthService {
  constructor(private prisma: PrismaService) {}

  async register(registerDto: RegisterDto) {
    const { deviceId, nickname, avatar } = registerDto;

    let user = await this.prisma.user.findUnique({
      where: { deviceId },
    });

    const isNewUser = !user;

    if (!user) {
      const defaultNickname = nickname || `用户${Date.now().toString().slice(-6)}`;
      user = await this.prisma.user.create({
        data: {
          deviceId,
          nickname: defaultNickname,
          avatar: avatar || null,
        },
      });
    }

    return {
      id: user.id,
      deviceId: user.deviceId,
      nickname: user.nickname,
      avatar: user.avatar,
      isNewUser,
      createdAt: user.createdAt.toISOString(),
    };
  }

  async getCurrentUser(userId: string) {
    const user = await this.prisma.user.findUnique({
      where: { id: userId },
    });

    if (!user) {
      throw new Error('用户不存在');
    }

    return {
      id: user.id,
      deviceId: user.deviceId,
      nickname: user.nickname,
      avatar: user.avatar,
      createdAt: user.createdAt.toISOString(),
      updatedAt: user.updatedAt.toISOString(),
    };
  }
}
