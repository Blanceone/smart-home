import { Controller, Get, Query, Param } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { DeviceService } from './device.service';
import { SearchDeviceDto } from './dto/device.dto';

@ApiTags('设备模块')
@Controller('devices')
@ApiBearerAuth()
export class DeviceController {
  constructor(private readonly deviceService: DeviceService) {}

  @Get()
  @ApiOperation({ summary: '搜索设备' })
  async searchDevices(@Query() dto: SearchDeviceDto) {
    return this.deviceService.searchDevices(dto);
  }

  @Get(':deviceId')
  @ApiOperation({ summary: '获取设备详情' })
  async getDeviceDetail(@Param('deviceId') deviceId: string) {
    return this.deviceService.getDeviceDetail(deviceId);
  }

  @Get(':deviceId/purchase-url')
  @ApiOperation({ summary: '获取设备购买链接' })
  async getDevicePurchaseUrl(@Param('deviceId') deviceId: string) {
    return this.deviceService.getDevicePurchaseUrl(deviceId);
  }
}
