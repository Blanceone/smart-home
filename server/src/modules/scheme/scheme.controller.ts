import { Controller, Get, Post, Delete, Body, Param } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { SchemeService } from './scheme.service';
import { GenerateSchemeDto } from './dto/scheme.dto';
import { CurrentUser } from '../../common/decorators/current-user.decorator';

@ApiTags('方案模块')
@Controller('schemes')
@ApiBearerAuth()
export class SchemeController {
  constructor(private readonly schemeService: SchemeService) {}

  @Post('generate')
  @ApiOperation({ summary: '生成方案' })
  async generateScheme(@CurrentUser('userId') userId: string, @Body() dto: GenerateSchemeDto) {
    return this.schemeService.generateScheme(userId, dto);
  }

  @Get(':schemeId')
  @ApiOperation({ summary: '获取方案详情' })
  async getSchemeDetail(
    @Param('schemeId') schemeId: string,
    @CurrentUser('userId') userId: string,
  ) {
    return this.schemeService.getSchemeDetail(schemeId, userId);
  }

  @Post(':schemeId/save')
  @ApiOperation({ summary: '保存方案' })
  async saveScheme(@Param('schemeId') schemeId: string, @CurrentUser('userId') userId: string) {
    return this.schemeService.saveScheme(schemeId, userId);
  }

  @Delete(':schemeId')
  @ApiOperation({ summary: '删除方案' })
  async deleteScheme(@Param('schemeId') schemeId: string, @CurrentUser('userId') userId: string) {
    return this.schemeService.deleteScheme(schemeId, userId);
  }

  @Get(':schemeId/export')
  @ApiOperation({ summary: '导出方案PDF' })
  async exportSchemePdf(
    @Param('schemeId') schemeId: string,
    @CurrentUser('userId') userId: string,
  ) {
    return this.schemeService.exportSchemePdf(schemeId, userId);
  }
}
