import { Controller, Post, Body } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { FeedbackService } from './feedback.service';
import { SchemeRatingDto, SuggestionDto, DataCorrectionDto } from './dto/feedback.dto';
import { CurrentUser } from '../../common/decorators/current-user.decorator';

@ApiTags('反馈模块')
@Controller('feedback')
@ApiBearerAuth()
export class FeedbackController {
  constructor(private readonly feedbackService: FeedbackService) {}

  @Post('scheme-rating')
  @ApiOperation({ summary: '提交方案评价' })
  async submitSchemeRating(@CurrentUser('userId') userId: string, @Body() dto: SchemeRatingDto) {
    return this.feedbackService.submitSchemeRating(userId, dto);
  }

  @Post('suggestion')
  @ApiOperation({ summary: '提交意见反馈' })
  async submitSuggestion(@CurrentUser('userId') userId: string, @Body() dto: SuggestionDto) {
    return this.feedbackService.submitSuggestion(userId, dto);
  }

  @Post('data-correction')
  @ApiOperation({ summary: '提交数据纠错' })
  async submitDataCorrection(
    @CurrentUser('userId') userId: string,
    @Body() dto: DataCorrectionDto,
  ) {
    return this.feedbackService.submitDataCorrection(userId, dto);
  }
}
