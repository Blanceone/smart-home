import { Module } from '@nestjs/common';
import { HouseLayoutController } from './house-layout.controller';
import { HouseLayoutService } from './house-layout.service';

@Module({
  controllers: [HouseLayoutController],
  providers: [HouseLayoutService],
  exports: [HouseLayoutService],
})
export class HouseLayoutModule {}
