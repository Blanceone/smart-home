import { Test, TestingModule } from '@nestjs/testing';
import { SchemeService } from './modules/scheme/scheme.service';
import { PrismaService } from './common/prisma/prisma.service';
import { ConfigService } from '@nestjs/config';
import { NotFoundException, BadRequestException } from '@nestjs/common';

describe('SchemeService', () => {
  let service: SchemeService;
  let prisma: PrismaService;

  const mockPrismaService = {
    userInfo: {
      findUnique: jest.fn(),
    },
    houseLayout: {
      findUnique: jest.fn(),
    },
    scheme: {
      create: jest.fn(),
      findFirst: jest.fn(),
      findMany: jest.fn(),
      count: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
    },
    device: {
      findFirst: jest.fn(),
      create: jest.fn(),
    },
    schemeDevice: {
      create: jest.fn(),
    },
  };

  const mockConfigService = {
    get: jest.fn().mockReturnValue('test-api-key'),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        SchemeService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
        {
          provide: ConfigService,
          useValue: mockConfigService,
        },
      ],
    }).compile();

    service = module.get<SchemeService>(SchemeService);
    prisma = module.get<PrismaService>(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getSchemeDetail', () => {
    it('should return scheme detail successfully', async () => {
      const schemeId = 'scheme-123';
      const userId = 'user-123';

      mockPrismaService.scheme.findFirst.mockResolvedValue({
        id: schemeId,
        userId,
        name: 'Test Scheme',
        budget: 10000,
        totalPrice: 9500,
        status: 'completed',
        decorationGuide: { summary: 'Test summary' },
        schemeDevices: [],
        createdAt: new Date(),
      });

      const result = await service.getSchemeDetail(schemeId, userId);

      expect(result.id).toBe(schemeId);
      expect(result.name).toBe('Test Scheme');
    });

    it('should throw NotFoundException if scheme not found', async () => {
      mockPrismaService.scheme.findFirst.mockResolvedValue(null);

      await expect(
        service.getSchemeDetail('non-existent', 'user-123'),
      ).rejects.toThrow(NotFoundException);
    });

    it('should throw NotFoundException if scheme belongs to another user', async () => {
      mockPrismaService.scheme.findFirst.mockResolvedValue(null);

      await expect(
        service.getSchemeDetail('scheme-123', 'different-user'),
      ).rejects.toThrow(NotFoundException);
    });
  });

  describe('saveScheme', () => {
    it('should save scheme successfully', async () => {
      const schemeId = 'scheme-123';
      const userId = 'user-123';

      mockPrismaService.scheme.findFirst.mockResolvedValue({
        id: schemeId,
        userId,
        isSaved: false,
      });

      mockPrismaService.scheme.count.mockResolvedValue(2);

      mockPrismaService.scheme.update.mockResolvedValue({
        id: schemeId,
        isSaved: true,
        savedAt: new Date(),
      });

      const result = await service.saveScheme(schemeId, userId);

      expect(result.id).toBe(schemeId);
      expect(mockPrismaService.scheme.update).toHaveBeenCalled();
    });

    it('should throw NotFoundException if scheme not found', async () => {
      mockPrismaService.scheme.findFirst.mockResolvedValue(null);

      await expect(service.saveScheme('non-existent', 'user-123')).rejects.toThrow(
        NotFoundException,
      );
    });

    it('should throw BadRequestException if saved schemes exceed limit', async () => {
      const schemeId = 'scheme-123';
      const userId = 'user-123';

      mockPrismaService.scheme.findFirst.mockResolvedValue({
        id: schemeId,
        userId,
        isSaved: false,
      });

      mockPrismaService.scheme.count.mockResolvedValue(3);

      await expect(service.saveScheme(schemeId, userId)).rejects.toThrow(
        BadRequestException,
      );
    });
  });

  describe('deleteScheme', () => {
    it('should delete scheme successfully', async () => {
      const schemeId = 'scheme-123';
      const userId = 'user-123';

      mockPrismaService.scheme.findFirst.mockResolvedValue({
        id: schemeId,
        userId,
      });

      mockPrismaService.scheme.delete.mockResolvedValue({ id: schemeId });

      await service.deleteScheme(schemeId, userId);

      expect(mockPrismaService.scheme.delete).toHaveBeenCalledWith({
        where: { id: schemeId },
      });
    });

    it('should throw NotFoundException if scheme not found', async () => {
      mockPrismaService.scheme.findFirst.mockResolvedValue(null);

      await expect(service.deleteScheme('non-existent', 'user-123')).rejects.toThrow(
        NotFoundException,
      );
    });
  });
});
