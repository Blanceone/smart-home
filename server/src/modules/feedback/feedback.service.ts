import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../../common/prisma/prisma.service';
import { SchemeRatingDto, SuggestionDto, DataCorrectionDto } from './dto/feedback.dto';

@Injectable()
export class FeedbackService {
  constructor(private prisma: PrismaService) {}

  async submitSchemeRating(userId: string, dto: SchemeRatingDto) {
    const scheme = await this.prisma.scheme.findFirst({
      where: { id: dto.schemeId, userId },
    });

    if (!scheme) {
      throw new NotFoundException('方案不存在');
    }

    const feedback = await this.prisma.feedback.create({
      data: {
        userId,
        type: 'scheme_rating',
        content: dto.content || '',
        relatedId: dto.schemeId,
        rating: dto.rating,
      },
    });

    return {
      id: feedback.id,
      createdAt: feedback.createdAt,
    };
  }

  async submitSuggestion(userId: string, dto: SuggestionDto) {
    const feedback = await this.prisma.feedback.create({
      data: {
        userId,
        type: 'suggestion',
        content: dto.content,
        contact: dto.contact,
      },
    });

    return {
      id: feedback.id,
      createdAt: feedback.createdAt,
    };
  }

  async submitDataCorrection(userId: string, dto: DataCorrectionDto) {
    const device = await this.prisma.device.findUnique({
      where: { id: dto.deviceId },
    });

    if (!device) {
      throw new NotFoundException('设备不存在');
    }

    const feedback = await this.prisma.feedback.create({
      data: {
        userId,
        type: 'data_correction',
        content: `${dto.errorType}: ${dto.correctInfo}`,
        relatedId: dto.deviceId,
      },
    });

    return {
      id: feedback.id,
      createdAt: feedback.createdAt,
    };
  }
}
