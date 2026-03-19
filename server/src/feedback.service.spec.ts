import { Test, TestingModule } from '@nestjs/testing';
import { FeedbackService } from './modules/feedback/feedback.service';
import { PrismaService } from './common/prisma/prisma.service';
import { NotFoundException } from '@nestjs/common';

describe('FeedbackService', () => {
  let service: FeedbackService;
  let prisma: PrismaService;

  const mockPrismaService = {
    scheme: {
      findFirst: jest.fn(),
    },
    device: {
      findUnique: jest.fn(),
    },
    feedback: {
      create: jest.fn(),
    },
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        FeedbackService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
      ],
    }).compile();

    service = module.get<FeedbackService>(FeedbackService);
    prisma = module.get<PrismaService>(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('submitSchemeRating', () => {
    it('should submit scheme rating successfully', async () => {
      const userId = 'user-123';
      const dto = {
        schemeId: 'scheme-123',
        rating: 5,
        content: 'Great scheme!',
      };

      mockPrismaService.scheme.findFirst.mockResolvedValue({
        id: dto.schemeId,
        userId,
      });

      mockPrismaService.feedback.create.mockResolvedValue({
        id: 'feedback-123',
        userId,
        type: 'scheme_rating',
        content: dto.content,
        relatedId: dto.schemeId,
        rating: dto.rating,
        createdAt: new Date(),
      });

      const result = await service.submitSchemeRating(userId, dto);

      expect(result.id).toBe('feedback-123');
      expect(mockPrismaService.feedback.create).toHaveBeenCalled();
    });

    it('should throw NotFoundException if scheme not found', async () => {
      mockPrismaService.scheme.findFirst.mockResolvedValue(null);

      await expect(
        service.submitSchemeRating('user-123', {
          schemeId: 'non-existent',
          rating: 5,
        }),
      ).rejects.toThrow(NotFoundException);
    });

    it('should accept rating without content', async () => {
      const userId = 'user-123';
      const dto = {
        schemeId: 'scheme-123',
        rating: 4,
      };

      mockPrismaService.scheme.findFirst.mockResolvedValue({
        id: dto.schemeId,
        userId,
      });

      mockPrismaService.feedback.create.mockResolvedValue({
        id: 'feedback-123',
        createdAt: new Date(),
      });

      const result = await service.submitSchemeRating(userId, dto);

      expect(result.id).toBe('feedback-123');
    });
  });

  describe('submitSuggestion', () => {
    it('should submit suggestion successfully', async () => {
      const userId = 'user-123';
      const dto = {
        type: 'suggestion',
        content: 'Please add more device options',
        contact: 'user@example.com',
      };

      mockPrismaService.feedback.create.mockResolvedValue({
        id: 'feedback-123',
        userId,
        type: 'suggestion',
        content: dto.content,
        contact: dto.contact,
        createdAt: new Date(),
      });

      const result = await service.submitSuggestion(userId, dto);

      expect(result.id).toBe('feedback-123');
      expect(mockPrismaService.feedback.create).toHaveBeenCalledWith({
        data: {
          userId,
          type: 'suggestion',
          content: dto.content,
          contact: dto.contact,
        },
      });
    });
  });

  describe('submitDataCorrection', () => {
    it('should submit data correction successfully', async () => {
      const userId = 'user-123';
      const dto = {
        deviceId: 'device-123',
        errorType: 'price_error',
        correctInfo: 'The price should be 299 yuan',
      };

      mockPrismaService.device.findUnique.mockResolvedValue({
        id: dto.deviceId,
        name: 'Smart Light',
      });

      mockPrismaService.feedback.create.mockResolvedValue({
        id: 'feedback-123',
        userId,
        type: 'data_correction',
        content: `${dto.errorType}: ${dto.correctInfo}`,
        relatedId: dto.deviceId,
        createdAt: new Date(),
      });

      const result = await service.submitDataCorrection(userId, dto);

      expect(result.id).toBe('feedback-123');
    });

    it('should throw NotFoundException if device not found', async () => {
      mockPrismaService.device.findUnique.mockResolvedValue(null);

      await expect(
        service.submitDataCorrection('user-123', {
          deviceId: 'non-existent',
          errorType: 'price_error',
          correctInfo: 'test',
        }),
      ).rejects.toThrow(NotFoundException);
    });
  });
});
