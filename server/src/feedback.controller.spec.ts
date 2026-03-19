import { Test, TestingModule } from '@nestjs/testing';
import { FeedbackController } from './modules/feedback/feedback.controller';
import { FeedbackService } from './modules/feedback/feedback.service';

describe('FeedbackController', () => {
  let controller: FeedbackController;
  let service: FeedbackService;

  const mockFeedbackService = {
    submitSchemeRating: jest.fn(),
    submitSuggestion: jest.fn(),
    submitDataCorrection: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [FeedbackController],
      providers: [
        {
          provide: FeedbackService,
          useValue: mockFeedbackService,
        },
      ],
    }).compile();

    controller = module.get<FeedbackController>(FeedbackController);
    service = module.get<FeedbackService>(FeedbackService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('submitSchemeRating', () => {
    it('should submit scheme rating successfully', async () => {
      const userId = 'user-123';
      const dto = {
        schemeId: 'scheme-456',
        rating: 5,
        content: '方案非常符合我的需求',
      };

      mockFeedbackService.submitSchemeRating.mockResolvedValue({
        id: 'feedback-123',
        createdAt: new Date(),
      });

      const result = await controller.submitSchemeRating(userId, dto);

      expect(result.id).toBe('feedback-123');
      expect(mockFeedbackService.submitSchemeRating).toHaveBeenCalledWith(userId, dto);
    });

    it('should submit rating without content', async () => {
      const userId = 'user-123';
      const dto = {
        schemeId: 'scheme-456',
        rating: 4,
      };

      mockFeedbackService.submitSchemeRating.mockResolvedValue({
        id: 'feedback-123',
        createdAt: new Date(),
      });

      const result = await controller.submitSchemeRating(userId, dto);

      expect(result.id).toBe('feedback-123');
    });
  });

  describe('submitSuggestion', () => {
    it('should submit suggestion successfully', async () => {
      const userId = 'user-123';
      const dto = {
        type: '功能建议',
        content: '希望增加更多的智能设备支持',
        contact: 'test@example.com',
      };

      mockFeedbackService.submitSuggestion.mockResolvedValue({
        id: 'feedback-456',
        createdAt: new Date(),
      });

      const result = await controller.submitSuggestion(userId, dto);

      expect(result.id).toBe('feedback-456');
      expect(mockFeedbackService.submitSuggestion).toHaveBeenCalledWith(userId, dto);
    });
  });

  describe('submitDataCorrection', () => {
    it('should submit data correction successfully', async () => {
      const userId = 'user-123';
      const dto = {
        deviceId: 'device-789',
        errorType: '价格错误',
        correctInfo: '正确设备名称 - ¥299',
      };

      mockFeedbackService.submitDataCorrection.mockResolvedValue({
        id: 'feedback-789',
        createdAt: new Date(),
      });

      const result = await controller.submitDataCorrection(userId, dto);

      expect(result.id).toBe('feedback-789');
      expect(mockFeedbackService.submitDataCorrection).toHaveBeenCalledWith(userId, dto);
    });
  });
});
