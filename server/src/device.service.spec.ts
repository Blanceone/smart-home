import { Test, TestingModule } from '@nestjs/testing';
import { DeviceService } from './modules/device/device.service';
import { PrismaService } from './common/prisma/prisma.service';
import { TaobaoService } from './common/services/taobao.service';
import { NotFoundException } from '@nestjs/common';

describe('DeviceService', () => {
  let service: DeviceService;
  let prisma: PrismaService;

  const mockPrismaService = {
    device: {
      findUnique: jest.fn(),
      findMany: jest.fn(),
      count: jest.fn(),
      update: jest.fn(),
    },
  };

  const mockTaobaoService = {
    isConfigured: jest.fn().mockReturnValue(false),
    generatePurchaseUrl: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        DeviceService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
        {
          provide: TaobaoService,
          useValue: mockTaobaoService,
        },
      ],
    }).compile();

    service = module.get<DeviceService>(DeviceService);
    prisma = module.get<PrismaService>(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getDeviceDetail', () => {
    it('should return device detail successfully', async () => {
      const deviceId = 'device-123';
      const mockDevice = {
        id: deviceId,
        name: 'Smart Light',
        brand: 'Xiaomi',
        category: 'Lighting',
        price: 199,
        originalPrice: 249,
        description: 'A smart LED light',
        features: ['WiFi', 'Voice Control'],
        specifications: { power: '10W', voltage: '220V' },
        applicableScenes: ['Living Room', 'Bedroom'],
        imageUrl: 'https://example.com/image.jpg',
        images: [],
        taobaoUrl: 'https://item.taobao.com/123',
        priceUpdatedAt: new Date(),
      };

      mockPrismaService.device.findUnique.mockResolvedValue(mockDevice);

      const result = await service.getDeviceDetail(deviceId);

      expect(result.id).toBe(deviceId);
      expect(result.name).toBe('Smart Light');
      expect(result.brand).toBe('Xiaomi');
    });

    it('should throw NotFoundException if device not found', async () => {
      mockPrismaService.device.findUnique.mockResolvedValue(null);

      await expect(service.getDeviceDetail('non-existent')).rejects.toThrow(
        NotFoundException,
      );
    });
  });

  describe('searchDevices', () => {
    it('should return paginated device list', async () => {
      const dto = {
        page: 1,
        pageSize: 10,
        keyword: 'light',
      };

      const mockDevices = [
        { id: 'device-1', name: 'Smart Light 1', brand: 'Xiaomi', category: 'Lighting', price: 199, imageUrl: null },
        { id: 'device-2', name: 'Smart Light 2', brand: 'Yeelight', category: 'Lighting', price: 299, imageUrl: null },
      ];

      mockPrismaService.device.findMany.mockResolvedValue(mockDevices);
      mockPrismaService.device.count.mockResolvedValue(2);

      const result = await service.searchDevices(dto);

      expect(result.list).toHaveLength(2);
      expect(result.pagination.total).toBe(2);
      expect(result.pagination.page).toBe(1);
    });

    it('should return empty list when no devices match', async () => {
      const dto = {
        page: 1,
        pageSize: 10,
        keyword: 'nonexistent',
      };

      mockPrismaService.device.findMany.mockResolvedValue([]);
      mockPrismaService.device.count.mockResolvedValue(0);

      const result = await service.searchDevices(dto);

      expect(result.list).toHaveLength(0);
      expect(result.pagination.total).toBe(0);
    });

    it('should handle pagination correctly', async () => {
      const dto = {
        page: 2,
        pageSize: 5,
      };

      const mockDevices = Array.from({ length: 5 }, (_, i) => ({
        id: `device-${i + 6}`,
        name: `Smart Device ${i + 6}`,
        brand: 'Xiaomi',
        category: 'Smart',
        price: 100 + i * 10,
        imageUrl: null,
      }));

      mockPrismaService.device.findMany.mockResolvedValue(mockDevices);
      mockPrismaService.device.count.mockResolvedValue(20);

      const result = await service.searchDevices(dto);

      expect(result.list).toHaveLength(5);
      expect(result.pagination.page).toBe(2);
      expect(result.pagination.total).toBe(20);
      expect(result.pagination.pageSize).toBe(5);
    });

    it('should filter by category', async () => {
      const dto = {
        category: 'Lighting',
        page: 1,
        pageSize: 20,
      };

      mockPrismaService.device.findMany.mockResolvedValue([]);
      mockPrismaService.device.count.mockResolvedValue(0);

      await service.searchDevices(dto);

      expect(mockPrismaService.device.findMany).toHaveBeenCalledWith(
        expect.objectContaining({
          where: expect.objectContaining({
            category: 'Lighting',
          }),
        }),
      );
    });

    it('should limit pageSize to 100', async () => {
      const dto = {
        page: 1,
        pageSize: 200,
      };

      mockPrismaService.device.findMany.mockResolvedValue([]);
      mockPrismaService.device.count.mockResolvedValue(0);

      await service.searchDevices(dto);

      expect(mockPrismaService.device.findMany).toHaveBeenCalledWith(
        expect.objectContaining({
          take: 100,
        }),
      );
    });
  });

  describe('getDevicePurchaseUrl', () => {
    it('should return purchase URL with device info', async () => {
      const deviceId = 'device-123';

      mockPrismaService.device.findUnique.mockResolvedValue({
        id: deviceId,
        name: 'Smart Light',
        taobaoItemId: 'item-123',
        taobaoUrl: 'https://item.taobao.com/item-123',
        price: 199,
      });

      const result = await service.getDevicePurchaseUrl(deviceId);

      expect(result.purchaseUrl).toBeDefined();
      expect(result.price).toBe(199);
    });

    it('should throw NotFoundException if device not found', async () => {
      mockPrismaService.device.findUnique.mockResolvedValue(null);

      await expect(service.getDevicePurchaseUrl('non-existent')).rejects.toThrow(
        NotFoundException,
      );
    });
  });
});
