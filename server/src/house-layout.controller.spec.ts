import { Test, TestingModule } from '@nestjs/testing';
import { HouseLayoutController } from './modules/house-layout/house-layout.controller';
import { HouseLayoutService } from './modules/house-layout/house-layout.service';

describe('HouseLayoutController', () => {
  let controller: HouseLayoutController;
  let service: HouseLayoutService;

  const mockHouseLayoutService = {
    saveHouseLayout: jest.fn(),
    getHouseLayout: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [HouseLayoutController],
      providers: [
        {
          provide: HouseLayoutService,
          useValue: mockHouseLayoutService,
        },
      ],
    }).compile();

    controller = module.get<HouseLayoutController>(HouseLayoutController);
    service = module.get<HouseLayoutService>(HouseLayoutService);
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
          { name: '客厅', area: 25, specialNeeds: '智能灯光' },
          { name: '主卧', area: 18, specialNeeds: '' },
          { name: '厨房', area: 8, specialNeeds: '智能烟感' },
        ],
      };

      mockHouseLayoutService.saveHouseLayout.mockResolvedValue({
        id: 'layout-123',
        userId,
        houseType: '两居室',
        totalArea: 85,
        roomCount: 3,
      });

      const result = await controller.saveHouseLayout(userId, dto);

      expect(result.id).toBe('layout-123');
      expect(result.roomCount).toBe(3);
      expect(mockHouseLayoutService.saveHouseLayout).toHaveBeenCalledWith(userId, dto);
    });

    it('should save single room layout', async () => {
      const userId = 'user-456';
      const dto = {
        houseType: '一居室',
        totalArea: 40,
        rooms: [{ name: '客厅', area: 40, specialNeeds: '' }],
      };

      mockHouseLayoutService.saveHouseLayout.mockResolvedValue({
        id: 'layout-456',
        userId,
        houseType: '一居室',
        totalArea: 40,
        roomCount: 1,
      });

      const result = await controller.saveHouseLayout(userId, dto);

      expect(result.roomCount).toBe(1);
    });

    it('should save large house layout', async () => {
      const userId = 'user-789';
      const dto = {
        houseType: '别墅',
        totalArea: 300,
        rooms: [
          { name: '客厅', area: 50, specialNeeds: '' },
          { name: '主卧', area: 30, specialNeeds: '' },
          { name: '次卧', area: 25, specialNeeds: '' },
          { name: '书房', area: 20, specialNeeds: '' },
          { name: '厨房', area: 20, specialNeeds: '' },
          { name: '卫生间', area: 15, specialNeeds: '' },
          { name: '阳台', area: 15, specialNeeds: '' },
          { name: '车库', area: 40, specialNeeds: '' },
        ],
      };

      mockHouseLayoutService.saveHouseLayout.mockResolvedValue({
        id: 'layout-789',
        userId,
        houseType: '别墅',
        totalArea: 300,
        roomCount: 8,
      });

      const result = await controller.saveHouseLayout(userId, dto);

      expect(result.roomCount).toBe(8);
    });
  });

  describe('getHouseLayout', () => {
    it('should return house layout with rooms', async () => {
      const userId = 'user-123';
      const mockLayout = {
        id: 'layout-123',
        userId,
        houseType: '两居室',
        totalArea: 85,
        rooms: [
          { id: 'room-1', name: '客厅', area: 25, specialNeeds: '智能灯光', sortOrder: 0 },
          { id: 'room-2', name: '主卧', area: 18, specialNeeds: null, sortOrder: 1 },
        ],
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      mockHouseLayoutService.getHouseLayout.mockResolvedValue(mockLayout);

      const result = await controller.getHouseLayout(userId);

      expect(result).not.toBeNull();
      expect(result!.id).toBe('layout-123');
      expect(result!.rooms).toHaveLength(2);
      expect(mockHouseLayoutService.getHouseLayout).toHaveBeenCalledWith(userId);
    });

    it('should return null if house layout not found', async () => {
      mockHouseLayoutService.getHouseLayout.mockResolvedValue(null);

      const result = await controller.getHouseLayout('non-existent-user');

      expect(result).toBeNull();
    });
  });
});
