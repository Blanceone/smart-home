import { Module } from '@nestjs/common';
import { DeviceController } from './device.controller';
import { DeviceService } from './device.service';
import { TaobaoService } from '../../common/services/taobao.service';

@Module({
  controllers: [DeviceController],
  providers: [DeviceService, TaobaoService],
  exports: [DeviceService],
})
export class DeviceModule {}
