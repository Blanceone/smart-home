import { Controller, Get, Post, Body } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { UserInfoService } from './user-info.service';
import { SaveUserInfoDto } from './dto/user-info.dto';
import { CurrentUser } from '../../common/decorators/current-user.decorator';

@ApiTags('用户信息采集模块')
@Controller('users/me')
@ApiBearerAuth()
export class UserInfoController {
  constructor(private readonly userInfoService: UserInfoService) {}

  @Post('info')
  @ApiOperation({ summary: '保存用户信息' })
  async saveUserInfo(
    @CurrentUser('userId') userId: string,
    @Body() dto: SaveUserInfoDto,
  ) {
    return this.userInfoService.saveUserInfo(userId, dto);
  }

  @Get('info')
  @ApiOperation({ summary: '获取用户信息' })
  async getUserInfo(@CurrentUser('userId') userId: string) {
    return this.userInfoService.getUserInfo(userId);
  }
}
