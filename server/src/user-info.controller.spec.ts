import { Test, TestingModule } from '@nestjs/testing';
import { UserInfoController } from './modules/user-info/user-info.controller';
import { UserInfoService } from './modules/user-info/user-info.service';

describe('UserInfoController', () => {
  let controller: UserInfoController;
  let service: UserInfoService;

  const mockUserInfoService = {
    saveUserInfo: jest.fn(),
    getUserInfo: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UserInfoController],
      providers: [
        {
          provide: UserInfoService,
          useValue: mockUserInfoService,
        },
      ],
    }).compile();

    controller = module.get<UserInfoController>(UserInfoController);
    service = module.get<UserInfoService>(UserInfoService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('saveUserInfo', () => {
    it('should save user info successfully', async () => {
      const userId = 'user-123';
      const dto = {
        basicInfo: {
          age: '26-30',
          occupation: '上班族',
          familyMembers: ['情侣'],
          city: '北京',
        },
      };

      mockUserInfoService.saveUserInfo.mockResolvedValue({
        id: 'info-123',
        userId,
        ...dto,
      });

      const result = await controller.saveUserInfo(userId, dto);

      expect(result.id).toBe('info-123');
      expect(result.userId).toBe(userId);
      expect(mockUserInfoService.saveUserInfo).toHaveBeenCalledWith(userId, dto);
    });

    it('should save complete user info with all sections', async () => {
      const userId = 'user-456';
      const dto = {
        basicInfo: {
          age: '31-35',
          occupation: '自由职业',
          familyMembers: ['夫妻+孩子'],
          city: '上海',
        },
        lifestyle: {
          sleepPattern: '早睡早起',
          homeActivities: ['工作学习'],
          entertainmentHabits: ['阅读'],
        },
        deviceExperience: {
          knowledgeLevel: '非常熟悉',
          usedDevices: ['智能音箱', '智能门锁'],
        },
        aestheticPreference: {
          decorStyle: '北欧风',
          colorPreferences: ['灰色系', '白色系'],
        },
        brandPreference: {
          preferredBrands: ['小米/米家', '华为'],
        },
      };

      mockUserInfoService.saveUserInfo.mockResolvedValue({
        id: 'info-456',
        userId,
        ...dto,
      });

      const result = await controller.saveUserInfo(userId, dto);

      expect(result.id).toBe('info-456');
      expect(mockUserInfoService.saveUserInfo).toHaveBeenCalled();
    });
  });

  describe('getUserInfo', () => {
    it('should return user info', async () => {
      const userId = 'user-123';
      const mockUserInfo = {
        basicInfo: {
          age: '26-30',
          occupation: '上班族',
          familyMembers: ['情侣'],
          city: '北京',
        },
        lifestyle: {
          sleepPattern: '晚睡晚起',
          homeActivities: ['休闲娱乐'],
          entertainmentHabits: ['看电影追剧'],
        },
        deviceExperience: {
          knowledgeLevel: '用过一些',
          usedDevices: ['智能音箱'],
        },
        aestheticPreference: {
          decorStyle: '现代简约',
          colorPreferences: ['白色系'],
        },
        brandPreference: {
          preferredBrands: ['小米/米家'],
        },
        isCompleted: true,
        updatedAt: new Date(),
      };

      mockUserInfoService.getUserInfo.mockResolvedValue(mockUserInfo);

      const result = await controller.getUserInfo(userId);

      expect(result).not.toBeNull();
      expect(result!.basicInfo.age).toBe('26-30');
      expect(mockUserInfoService.getUserInfo).toHaveBeenCalledWith(userId);
    });

    it('should return null if user info not found', async () => {
      mockUserInfoService.getUserInfo.mockResolvedValue(null);

      const result = await controller.getUserInfo('non-existent-user');

      expect(result).toBeNull();
    });
  });
});
