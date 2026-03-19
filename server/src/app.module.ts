import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { APP_GUARD, APP_INTERCEPTOR } from '@nestjs/core';
import { PrismaModule } from './common/prisma/prisma.module';
import { RedisModule } from './common/redis/redis.module';
import { AuthModule } from './modules/auth/auth.module';
import { UserModule } from './modules/user/user.module';
import { UserInfoModule } from './modules/user-info/user-info.module';
import { HouseLayoutModule } from './modules/house-layout/house-layout.module';
import { SchemeModule } from './modules/scheme/scheme.module';
import { DeviceModule } from './modules/device/device.module';
import { FeedbackModule } from './modules/feedback/feedback.module';
import { CommonModule } from './modules/common/common.module';
import { DeviceAuthGuard } from './common/guards/jwt-auth.guard';
import { TransformInterceptor } from './common/interceptors/transform.interceptor';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '.env',
    }),
    PrismaModule,
    RedisModule,
    AuthModule,
    UserModule,
    UserInfoModule,
    HouseLayoutModule,
    SchemeModule,
    DeviceModule,
    FeedbackModule,
    CommonModule,
  ],
  providers: [
    {
      provide: APP_GUARD,
      useClass: DeviceAuthGuard,
    },
    {
      provide: APP_INTERCEPTOR,
      useClass: TransformInterceptor,
    },
  ],
})
export class AppModule {}
