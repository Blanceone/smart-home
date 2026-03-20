import { Controller, Get, Patch, Body, Req } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { UserService } from './user.service';
import { UpdateUserDto } from './dto/user.dto';
import { CurrentUser } from '../../common/decorators/current-user.decorator';

@ApiTags('用户模块')
@Controller('users')
@ApiBearerAuth()
export class UserController {
  constructor(private readonly userService: UserService) {}

  @Get('me')
  @ApiOperation({ summary: '获取当前用户信息' })
  async getCurrentUser(@CurrentUser('userId') userId: string) {
    return this.userService.getCurrentUser(userId);
  }

  @Patch('me')
  @ApiOperation({ summary: '更新当前用户信息' })
  async updateCurrentUser(@CurrentUser('userId') userId: string, @Body() dto: UpdateUserDto) {
    return this.userService.updateCurrentUser(userId, dto);
  }

  @Get('me/schemes')
  @ApiOperation({ summary: '获取我的方案列表' })
  async getUserSchemes(@CurrentUser('userId') userId: string) {
    return this.userService.getUserSchemes(userId);
  }
}
