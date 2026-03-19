import { Test, TestingModule } from '@nestjs/testing';
import { HouseLayoutService } from './modules/house-layout/house-layout.service';
import { PrismaService } from './common/prisma/prisma.service';
import { Prisma } from '@prisma/client';

describe('HouseLayoutService', () => {
  let service: HouseLayoutService;
  let prisma: PrismaService;

  const mockPrismaService = {
    houseLayout: {
      findUnique: jest.fn(),
      upsert: jest.fn(),
    },
    room: {
      deleteMany: jest.fn(),
    },
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        HouseLayoutService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
      ],
    }).compile();

    service = module.get<HouseLayoutService>(HouseLayoutService);
    prisma = module.get<PrismaService>(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('saveHouseLayout', () => {
    it('should save house layout successfully', async () => {
      const userId = 'user-123';
      const dto = {
        houseType: '两居室',
        totalArea: 85,
        rooms: [
          { name: '客厅', area: 25, specialNeeds: '需要智能灯光控制' },
          { name: '主卧', area: 18, specialNeeds: '' },
          { name: '厨房', area: 8, specialNeeds: '需要智能烟感' },
        ],
      };

      mockPrismaService.houseLayout.findUnique.mockResolvedValue(null);
      mockPrismaService.houseLayout.upsert.mockResolvedValue({
        id: 'layout-123',
        userId,
        houseType: dto.houseType,
        totalArea: new Prisma.Decimal(dto.totalArea),
        rooms: dto.rooms.map((r, i) => ({ ...r, id: `room-${i}`, sortOrder: i })),
        createdAt: new Date(),
      });

      const result = await service.saveHouseLayout(userId, dto);

      expect(result.houseType).toBe(dto.houseType);
      expect(result.roomCount).toBe(3);
    });

    it('should update existing house layout', async () => {
      const userId = 'user-123';
      const dto = {
        houseType: '三居室',
        totalArea: 120,
        rooms: [
          { name: '客厅', area: 35, specialNeeds: '' },
          { name: '主卧', area: 20, specialNeeds: '' },
          { name: '次卧', area: 15, specialNeeds: '' },
          { name: '书房', area: 12, specialNeeds: '需要智能照明' },
        ],
      };

      mockPrismaService.houseLayout.findUnique.mockResolvedValue({
        id: 'existing-layout-123',
        userId,
      });
      mockPrismaService.room.deleteMany.mockResolvedValue({ count: 3 });
      mockPrismaService.houseLayout.upsert.mockResolvedValue({
        id: 'existing-layout-123',
        userId,
        houseType: dto.houseType,
        totalArea: new Prisma.Decimal(dto.totalArea),
        rooms: dto.rooms.map((r, i) => ({ ...r, id: `room-${i}`, sortOrder: i })),
        createdAt: new Date(),
      });

      const result = await service.saveHouseLayout(userId, dto);

      expect(result.roomCount).toBe(4);
      expect(mockPrismaService.room.deleteMany).toHaveBeenCalled();
    });

    it('should save single room layout', async () => {
      const userId = 'user-123';
      const dto = {
        houseType: '一居室',
        totalArea: 40,
        rooms: [{ name: '客厅', area: 40, specialNeeds: '' }],
      };

      mockPrismaService.houseLayout.findUnique.mockResolvedValue(null);
      mockPrismaService.houseLayout.upsert.mockResolvedValue({
        id: 'layout-123',
        userId,
        houseType: dto.houseType,
        totalArea: new Prisma.Decimal(dto.totalArea),
        rooms: [{ id: 'room-0', ...dto.rooms[0], sortOrder: 0 }],
        createdAt: new Date(),
      });

      const result = await service.saveHouseLayout(userId, dto);

      expect(result.roomCount).toBe(1);
    });

    it('should handle all room types', async () => {
      const userId = 'user-123';
      const dto = {
        houseType: '复式/别墅',
        totalArea: 200,
        rooms: [
          { name: '客厅', area: 50, specialNeeds: '' },
          { name: '主卧', area: 30, specialNeeds: '' },
          { name: '次卧', area: 20, specialNeeds: '' },
          { name: '书房', area: 15, specialNeeds: '' },
          { name: '厨房', area: 15, specialNeeds: '' },
          { name: '卫生间', area: 10, specialNeeds: '' },
          { name: '阳台', area: 10, specialNeeds: '' },
        ],
      };

      mockPrismaService.houseLayout.findUnique.mockResolvedValue(null);
      mockPrismaService.houseLayout.upsert.mockResolvedValue({
        id: 'layout-123',
        userId,
        houseType: dto.houseType,
        totalArea: new Prisma.Decimal(dto.totalArea),
        rooms: dto.rooms.map((r, i) => ({ ...r, id: `room-${i}`, sortOrder: i })),
        createdAt: new Date(),
      });

      const result = await service.saveHouseLayout(userId, dto);

      expect(result.roomCount).toBe(7);
    });
  });

  describe('getHouseLayout', () => {
    it('should return house layout with rooms', async () => {
      const userId = 'user-123';

      mockPrismaService.houseLayout.findUnique.mockResolvedValue({
        id: 'layout-123',
        userId,
        houseType: '两居室',
        totalArea: new Prisma.Decimal(85),
        rooms: [
          { id: 'room-1', name: '客厅', area: new Prisma.Decimal(25), specialNeeds: '智能灯光', sortOrder: 0 },
          { id: 'room-2', name: '主卧', area: new Prisma.Decimal(18), specialNeeds: null, sortOrder: 1 },
        ],
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      const result = await service.getHouseLayout(userId);

      expect(result).not.toBeNull();
      expect(result!.houseType).toBe('两居室');
      expect(result!.rooms).toHaveLength(2);
    });

    it('should return null if layout not found', async () => {
      mockPrismaService.houseLayout.findUnique.mockResolvedValue(null);

      const result = await service.getHouseLayout('non-existent');

      expect(result).toBeNull();
    });

    it('should order rooms by sortOrder', async () => {
      const userId = 'user-123';

      mockPrismaService.houseLayout.findUnique.mockResolvedValue({
        id: 'layout-123',
        userId,
        houseType: '三居室',
        totalArea: new Prisma.Decimal(100),
        rooms: [
          { id: 'room-1', name: '客厅', area: new Prisma.Decimal(30), specialNeeds: null, sortOrder: 0 },
          { id: 'room-2', name: '主卧', area: new Prisma.Decimal(20), specialNeeds: null, sortOrder: 1 },
          { id: 'room-3', name: '次卧', area: new Prisma.Decimal(15), specialNeeds: null, sortOrder: 2 },
        ],
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      const result = await service.getHouseLayout(userId);

      expect(result!.rooms[0].name).toBe('客厅');
      expect(result!.rooms[1].name).toBe('主卧');
      expect(result!.rooms[2].name).toBe('次卧');
    });
  });
});
