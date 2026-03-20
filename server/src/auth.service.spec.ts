import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './modules/auth/auth.service';
import { PrismaService } from './common/prisma/prisma.service';

describe('AuthService', () => {
  let service: AuthService;
  let prisma: PrismaService;

  const mockPrismaService = {
    user: {
      findUnique: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
    },
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
      ],
    }).compile();

    service = module.get<AuthService>(AuthService);
    prisma = module.get<PrismaService>(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('register', () => {
    it('should register a new user successfully', async () => {
      const dto = {
        deviceId: 'a1b2c3d4e5f6',
        nickname: 'TestUser',
        avatar: undefined,
      };

      mockPrismaService.user.findUnique.mockResolvedValue(null);
      mockPrismaService.user.create.mockResolvedValue({
        id: 'user-123',
        deviceId: dto.deviceId,
        nickname: dto.nickname,
        avatar: null,
        status: 1,
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      const result = await service.register(dto);

      expect(result.isNewUser).toBe(true);
      expect(result.deviceId).toBe(dto.deviceId);
      expect(mockPrismaService.user.create).toHaveBeenCalled();
    });

    it('should return existing user if already registered', async () => {
      const dto = {
        deviceId: 'a1b2c3d4e5f6',
        nickname: 'TestUser',
        avatar: undefined,
      };

      mockPrismaService.user.findUnique.mockResolvedValue({
        id: 'user-123',
        deviceId: dto.deviceId,
        nickname: 'ExistingUser',
        avatar: null,
        status: 1,
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      const result = await service.register(dto);

      expect(result.isNewUser).toBe(false);
      expect(result.nickname).toBe('ExistingUser');
      expect(mockPrismaService.user.create).not.toHaveBeenCalled();
    });

    it('should generate default nickname if not provided', async () => {
      const dto = {
        deviceId: 'a1b2c3d4e5f6',
        nickname: undefined,
        avatar: undefined,
      };

      mockPrismaService.user.findUnique.mockResolvedValue(null);
      mockPrismaService.user.create.mockResolvedValue({
        id: 'user-123',
        deviceId: dto.deviceId,
        nickname: '用户123456',
        avatar: null,
        status: 1,
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      const result = await service.register(dto);

      expect(result.isNewUser).toBe(true);
      expect(mockPrismaService.user.create).toHaveBeenCalled();
    });
  });

  describe('getCurrentUser', () => {
    it('should return user by userId', async () => {
      const userId = 'user-123';
      const mockUser = {
        id: userId,
        deviceId: 'a1b2c3d4e5f6',
        nickname: 'TestUser',
        avatar: null,
        status: 1,
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);

      const result = await service.getCurrentUser(userId);

      expect(result.id).toBe(userId);
      expect(result.nickname).toBe('TestUser');
    });

    it('should throw error if user not found', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(null);

      await expect(service.getCurrentUser('non-existent')).rejects.toThrow('用户不存在');
    });
  });
});
