import {
  Injectable,
  CanActivate,
  ExecutionContext,
  UnauthorizedException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { Reflector } from '@nestjs/core';
import { IS_PUBLIC_KEY } from '../decorators/public.decorator';

const DEVICE_ID_PATTERN = /^[a-fA-F0-9]{8,16}$/;

const requestCounts = new Map<string, { count: number; resetTime: number }>();
const RATE_LIMIT_WINDOW = 60 * 1000;
const RATE_LIMIT_MAX_REQUESTS = 100;

function cleanOldEntries() {
  const now = Date.now();
  for (const [key, value] of requestCounts.entries()) {
    if (now > value.resetTime) {
      requestCounts.delete(key);
    }
  }
}

function checkRateLimit(deviceId: string): boolean {
  cleanOldEntries();
  
  const now = Date.now();
  const record = requestCounts.get(deviceId);
  
  if (!record || now > record.resetTime) {
    requestCounts.set(deviceId, { count: 1, resetTime: now + RATE_LIMIT_WINDOW });
    return true;
  }
  
  if (record.count >= RATE_LIMIT_MAX_REQUESTS) {
    return false;
  }
  
  record.count++;
  return true;
}

@Injectable()
export class DeviceAuthGuard implements CanActivate {
  constructor(
    private prisma: PrismaService,
    private reflector: Reflector,
  ) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const isPublic = this.reflector.getAllAndOverride<boolean>(IS_PUBLIC_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    if (isPublic) {
      return true;
    }

    const request = context.switchToHttp().getRequest();
    const deviceId = request.headers['x-device-id'];

    if (!deviceId) {
      throw new UnauthorizedException('缺少设备ID');
    }

    if (!DEVICE_ID_PATTERN.test(deviceId)) {
      throw new UnauthorizedException('无效的设备ID格式');
    }

    if (!checkRateLimit(deviceId)) {
      throw new UnauthorizedException('请求过于频繁，请稍后再试');
    }

    const user = await this.prisma.user.findUnique({
      where: { deviceId },
    });

    if (!user) {
      throw new UnauthorizedException('用户不存在');
    }

    request.user = { userId: user.id, deviceId: user.deviceId };
    return true;
  }
}
