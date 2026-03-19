import { Controller, Get, Post, Body } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { HouseLayoutService } from './house-layout.service';
import { SaveHouseLayoutDto } from './dto/house-layout.dto';
import { CurrentUser } from '../../common/decorators/current-user.decorator';

@ApiTags('户型模块')
@Controller('users/me')
@ApiBearerAuth()
export class HouseLayoutController {
  constructor(private readonly houseLayoutService: HouseLayoutService) {}

  @Post('house-layout')
  @ApiOperation({ summary: '保存户型信息' })
  async saveHouseLayout(
    @CurrentUser('userId') userId: string,
    @Body() dto: SaveHouseLayoutDto,
  ) {
    return this.houseLayoutService.saveHouseLayout(userId, dto);
  }

  @Get('house-layout')
  @ApiOperation({ summary: '获取户型信息' })
  async getHouseLayout(@CurrentUser('userId') userId: string) {
    return this.houseLayoutService.getHouseLayout(userId);
  }
}
