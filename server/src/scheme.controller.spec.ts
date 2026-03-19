import { Test, TestingModule } from '@nestjs/testing';
import { SchemeController } from './modules/scheme/scheme.controller';
import { SchemeService } from './modules/scheme/scheme.service';

describe('SchemeController', () => {
  let controller: SchemeController;
  let service: SchemeService;

  const mockSchemeService = {
    generateScheme: jest.fn(),
    getSchemeDetail: jest.fn(),
    saveScheme: jest.fn(),
    deleteScheme: jest.fn(),
    exportSchemePdf: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [SchemeController],
      providers: [
        {
          provide: SchemeService,
          useValue: mockSchemeService,
        },
      ],
    }).compile();

    controller = module.get<SchemeController>(SchemeController);
    service = module.get<SchemeService>(SchemeService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('generateScheme', () => {
    it('should generate a scheme', async () => {
      const userId = 'user-123';
      const dto = { budget: 10000 };
      const expectedResult = {
        id: 'scheme-123',
        name: '智能家居方案 #1',
        budget: 10000,
        totalPrice: 8500,
        status: 'completed',
        devices: [],
        createdAt: new Date(),
      };

      mockSchemeService.generateScheme.mockResolvedValue(expectedResult);

      const result = await controller.generateScheme(userId, dto);

      expect(result).toEqual(expectedResult);
      expect(service.generateScheme).toHaveBeenCalledWith(userId, dto);
    });
  });

  describe('getSchemeDetail', () => {
    it('should return scheme detail', async () => {
      const schemeId = 'scheme-123';
      const userId = 'user-123';
      const expectedResult = {
        id: schemeId,
        name: '智能家居方案 #1',
        budget: 10000,
        totalPrice: 8500,
        devices: [],
        createdAt: new Date(),
      };

      mockSchemeService.getSchemeDetail.mockResolvedValue(expectedResult);

      const result = await controller.getSchemeDetail(schemeId, userId);

      expect(result).toEqual(expectedResult);
      expect(service.getSchemeDetail).toHaveBeenCalledWith(schemeId, userId);
    });
  });

  describe('saveScheme', () => {
    it('should save a scheme', async () => {
      const schemeId = 'scheme-123';
      const userId = 'user-123';
      const expectedResult = {
        id: schemeId,
        savedAt: new Date(),
      };

      mockSchemeService.saveScheme.mockResolvedValue(expectedResult);

      const result = await controller.saveScheme(schemeId, userId);

      expect(result).toEqual(expectedResult);
      expect(service.saveScheme).toHaveBeenCalledWith(schemeId, userId);
    });
  });

  describe('deleteScheme', () => {
    it('should delete a scheme', async () => {
      const schemeId = 'scheme-123';
      const userId = 'user-123';

      mockSchemeService.deleteScheme.mockResolvedValue(null);

      const result = await controller.deleteScheme(schemeId, userId);

      expect(result).toBeNull();
      expect(service.deleteScheme).toHaveBeenCalledWith(schemeId, userId);
    });
  });

  describe('exportSchemePdf', () => {
    it('should export scheme as PDF', async () => {
      const schemeId = 'scheme-123';
      const userId = 'user-123';
      const expectedResult = {
        pdfUrl: 'https://example.com/pdf/scheme-123.pdf',
        fileName: '智能家居方案_20260315.pdf',
        expiresAt: new Date(),
      };

      mockSchemeService.exportSchemePdf.mockResolvedValue(expectedResult);

      const result = await controller.exportSchemePdf(schemeId, userId);

      expect(result).toEqual(expectedResult);
      expect(service.exportSchemePdf).toHaveBeenCalledWith(schemeId, userId);
    });
  });
});
