import { Test, TestingModule } from '@nestjs/testing';
import { UserInfoService } from './modules/user-info/user-info.service';
import { PrismaService } from './common/prisma/prisma.service';

describe('UserInfoService', () => {
  let service: UserInfoService;
  let prisma: PrismaService;

  const mockPrismaService = {
    userInfo: {
      upsert: jest.fn(),
      findUnique: jest.fn(),
    },
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UserInfoService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
      ],
    }).compile();

    service = module.get<UserInfoService>(UserInfoService);
    prisma = module.get<PrismaService>(PrismaService);
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
        lifestyle: {
          sleepPattern: '晚睡晚起',
          homeActivities: ['休闲娱乐', '健身运动'],
          entertainmentHabits: ['看电影追剧', '听音乐'],
        },
        deviceExperience: {
          knowledgeLevel: '用过一些',
          usedDevices: ['智能音箱', '智能灯具'],
        },
        aestheticPreference: {
          decorStyle: '现代简约',
          colorPreferences: ['白色系', '原木色'],
        },
        brandPreference: {
          preferredBrands: ['小米/米家', '华为'],
        },
      };

      mockPrismaService.userInfo.upsert.mockResolvedValue({
        id: 'info-123',
        userId,
        updatedAt: new Date(),
      });

      const result = await service.saveUserInfo(userId, dto);

      expect(result.id).toBe('info-123');
      expect(result.userId).toBe(userId);
      expect(mockPrismaService.userInfo.upsert).toHaveBeenCalled();
    });

    it('should save minimal user info', async () => {
      const userId = 'user-123';
      const dto = {
        basicInfo: {
          age: '18-25',
          occupation: '学生',
          familyMembers: ['独居'],
          city: '上海',
        },
      };

      mockPrismaService.userInfo.upsert.mockResolvedValue({
        id: 'info-123',
        userId,
        updatedAt: new Date(),
      });

      const result = await service.saveUserInfo(userId, dto);

      expect(result.id).toBe('info-123');
    });

    it('should update existing user info', async () => {
      const userId = 'user-123';
      const dto = {
        basicInfo: {
          age: '31-35',
          occupation: '自由职业',
          familyMembers: ['夫妻+孩子'],
          city: '深圳',
        },
      };

      mockPrismaService.userInfo.upsert.mockResolvedValue({
        id: 'existing-info-123',
        userId,
        updatedAt: new Date(),
      });

      const result = await service.saveUserInfo(userId, dto);

      expect(result.id).toBe('existing-info-123');
    });

    it('should handle all optional fields', async () => {
      const userId = 'user-123';
      const dto = {
        basicInfo: {
          age: '35+',
          occupation: '其他',
          familyMembers: ['与父母同住'],
          city: '广州',
        },
        lifestyle: {
          sleepPattern: '早睡早起',
          homeActivities: ['工作学习'],
          entertainmentHabits: ['阅读'],
        },
        deviceExperience: {
          knowledgeLevel: '非常熟悉',
          usedDevices: ['智能音箱', '智能灯具', '智能门锁', '扫地机器人'],
        },
        aestheticPreference: {
          decorStyle: '北欧风',
          colorPreferences: ['灰色系'],
        },
        brandPreference: {
          preferredBrands: ['Apple HomeKit'],
        },
      };

      mockPrismaService.userInfo.upsert.mockResolvedValue({
        id: 'info-123',
        userId,
        updatedAt: new Date(),
      });

      const result = await service.saveUserInfo(userId, dto);

      expect(result.id).toBe('info-123');
    });
  });

  describe('getUserInfo', () => {
    it('should return user info', async () => {
      const userId = 'user-123';

      mockPrismaService.userInfo.findUnique.mockResolvedValue({
        id: 'info-123',
        userId,
        age: '26-30',
        occupation: '上班族',
        familyMembers: ['情侣'],
        city: '北京',
        sleepPattern: '晚睡晚起',
        homeActivities: ['休闲娱乐'],
        entertainmentHabits: ['看电影追剧'],
        deviceKnowledgeLevel: '用过一些',
        usedDevices: ['智能音箱'],
        decorStyle: '现代简约',
        colorPreferences: ['白色系'],
        preferredBrands: ['小米/米家'],
        isCompleted: true,
        updatedAt: new Date(),
      });

      const result = await service.getUserInfo(userId);

      expect(result).not.toBeNull();
      expect(result!.basicInfo.age).toBe('26-30');
      expect(result!.basicInfo.city).toBe('北京');
      expect(result!.isCompleted).toBe(true);
    });

    it('should return null if user info not found', async () => {
      mockPrismaService.userInfo.findUnique.mockResolvedValue(null);

      const result = await service.getUserInfo('non-existent');

      expect(result).toBeNull();
    });

    it('should return structured response with all sections', async () => {
      const userId = 'user-123';

      mockPrismaService.userInfo.findUnique.mockResolvedValue({
        id: 'info-123',
        userId,
        age: '26-30',
        occupation: '上班族',
        familyMembers: ['情侣'],
        city: '北京',
        sleepPattern: '晚睡晚起',
        homeActivities: ['休闲娱乐'],
        entertainmentHabits: ['看电影追剧'],
        deviceKnowledgeLevel: '用过一些',
        usedDevices: ['智能音箱'],
        decorStyle: '现代简约',
        colorPreferences: ['白色系'],
        preferredBrands: ['小米/米家'],
        isCompleted: true,
        updatedAt: new Date(),
      });

      const result = await service.getUserInfo(userId);

      expect(result).toHaveProperty('basicInfo');
      expect(result).toHaveProperty('lifestyle');
      expect(result).toHaveProperty('deviceExperience');
      expect(result).toHaveProperty('aestheticPreference');
      expect(result).toHaveProperty('brandPreference');
    });
  });
});
