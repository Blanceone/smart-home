import { Test, TestingModule } from '@nestjs/testing';
import { DeviceController } from './modules/device/device.controller';
import { DeviceService } from './modules/device/device.service';

describe('DeviceController', () => {
  let controller: DeviceController;
  let service: DeviceService;

  const mockDeviceService = {
    getDeviceDetail: jest.fn(),
    getDevicePurchaseUrl: jest.fn(),
    searchDevices: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [DeviceController],
      providers: [
        {
          provide: DeviceService,
          useValue: mockDeviceService,
        },
      ],
    }).compile();

    controller = module.get<DeviceController>(DeviceController);
    service = module.get<DeviceService>(DeviceService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getDeviceDetail', () => {
    it('should return device detail', async () => {
      const deviceId = 'device-123';
      const expectedResult = {
        id: deviceId,
        name: 'Smart Light',
        brand: 'Xiaomi',
        category: 'Lighting',
        price: 199,
        description: 'A smart LED light',
      };

      mockDeviceService.getDeviceDetail.mockResolvedValue(expectedResult);

      const result = await controller.getDeviceDetail(deviceId);

      expect(result).toEqual(expectedResult);
      expect(service.getDeviceDetail).toHaveBeenCalledWith(deviceId);
    });
  });

  describe('getDevicePurchaseUrl', () => {
    it('should return purchase URL', async () => {
      const deviceId = 'device-123';
      const expectedResult = {
        purchaseUrl: 'https://item.taobao.com/123',
        price: 199,
        coupon: null,
        expiresAt: new Date(),
      };

      mockDeviceService.getDevicePurchaseUrl.mockResolvedValue(expectedResult);

      const result = await controller.getDevicePurchaseUrl(deviceId);

      expect(result).toEqual(expectedResult);
      expect(service.getDevicePurchaseUrl).toHaveBeenCalledWith(deviceId);
    });
  });

  describe('searchDevices', () => {
    it('should return paginated device list', async () => {
      const dto = { page: 1, pageSize: 10 };
      const expectedResult = {
        list: [
          { id: 'device-1', name: 'Smart Light', brand: 'Xiaomi', price: 199 },
        ],
        pagination: { page: 1, pageSize: 10, total: 1, totalPages: 1 },
      };

      mockDeviceService.searchDevices.mockResolvedValue(expectedResult);

      const result = await controller.searchDevices(dto);

      expect(result).toEqual(expectedResult);
      expect(service.searchDevices).toHaveBeenCalledWith(dto);
    });

    it('should filter by keyword', async () => {
      const dto = { keyword: 'light', page: 1, pageSize: 20 };
      const expectedResult = {
        list: [],
        pagination: { page: 1, pageSize: 20, total: 0, totalPages: 0 },
      };

      mockDeviceService.searchDevices.mockResolvedValue(expectedResult);

      const result = await controller.searchDevices(dto);

      expect(service.searchDevices).toHaveBeenCalledWith(dto);
    });
  });
});
