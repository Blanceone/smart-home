import { Test, TestingModule } from '@nestjs/testing';
import { UserService } from './modules/user/user.service';
import { PrismaService } from './common/prisma/prisma.service';
import { NotFoundException } from '@nestjs/common';

describe('UserService', () => {
  let service: UserService;
  let prisma: PrismaService;

  const mockPrismaService = {
    user: {
      findUnique: jest.fn(),
      update: jest.fn(),
    },
    scheme: {
      findMany: jest.fn(),
    },
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UserService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
      ],
    }).compile();

    service = module.get<UserService>(UserService);
    prisma = module.get<PrismaService>(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getCurrentUser', () => {
    it('should return user by userId', async () => {
      const userId = 'user-123';
      const mockUser = {
        id: userId,
        deviceId: 'a1b2c3d4e5f6',
        nickname: 'TestUser',
        avatar: null,
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);

      const result = await service.getCurrentUser(userId);

      expect(result.id).toBe(userId);
      expect(result.nickname).toBe('TestUser');
    });

    it('should throw NotFoundException if user not found', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(null);

      await expect(service.getCurrentUser('non-existent')).rejects.toThrow(NotFoundException);
    });
  });

  describe('updateCurrentUser', () => {
    it('should update user nickname successfully', async () => {
      const userId = 'user-123';
      const dto = { nickname: 'NewNickname' };

      mockPrismaService.user.update.mockResolvedValue({
        id: userId,
        nickname: 'NewNickname',
        avatar: null,
        updatedAt: new Date(),
      });

      const result = await service.updateCurrentUser(userId, dto);

      expect(result.nickname).toBe('NewNickname');
    });

    it('should update user avatar successfully', async () => {
      const userId = 'user-123';
      const dto = { avatar: 'https://example.com/avatar.jpg' };

      mockPrismaService.user.update.mockResolvedValue({
        id: userId,
        nickname: 'TestUser',
        avatar: dto.avatar,
        updatedAt: new Date(),
      });

      const result = await service.updateCurrentUser(userId, dto);

      expect(result.avatar).toBe(dto.avatar);
    });

    it('should update both nickname and avatar', async () => {
      const userId = 'user-123';
      const dto = {
        nickname: 'NewName',
        avatar: 'https://example.com/new-avatar.jpg',
      };

      mockPrismaService.user.update.mockResolvedValue({
        id: userId,
        nickname: dto.nickname,
        avatar: dto.avatar,
        updatedAt: new Date(),
      });

      const result = await service.updateCurrentUser(userId, dto);

      expect(result.nickname).toBe(dto.nickname);
      expect(result.avatar).toBe(dto.avatar);
    });
  });

  describe('getUserSchemes', () => {
    it('should return user schemes list', async () => {
      const userId = 'user-123';

      mockPrismaService.scheme.findMany.mockResolvedValue([
        {
          id: 'scheme-1',
          name: 'Scheme 1',
          budget: 10000,
          totalPrice: 9500,
          createdAt: new Date(),
          _count: { schemeDevices: 5 },
        },
        {
          id: 'scheme-2',
          name: 'Scheme 2',
          budget: 15000,
          totalPrice: 12000,
          createdAt: new Date(),
          _count: { schemeDevices: 8 },
        },
      ]);

      const result = await service.getUserSchemes(userId);

      expect(result.list).toHaveLength(2);
      expect(result.total).toBe(2);
      expect(result.maxAllowed).toBe(3);
    });

    it('should return empty list if no schemes', async () => {
      mockPrismaService.scheme.findMany.mockResolvedValue([]);

      const result = await service.getUserSchemes('user-123');

      expect(result.list).toHaveLength(0);
      expect(result.total).toBe(0);
    });

    it('should only return saved schemes', async () => {
      const userId = 'user-123';

      await service.getUserSchemes(userId);

      expect(mockPrismaService.scheme.findMany).toHaveBeenCalledWith(
        expect.objectContaining({
          where: expect.objectContaining({
            userId,
            isSaved: true,
          }),
        }),
      );
    });

    it('should limit results to 3 schemes', async () => {
      const userId = 'user-123';

      await service.getUserSchemes(userId);

      expect(mockPrismaService.scheme.findMany).toHaveBeenCalledWith(
        expect.objectContaining({
          take: 3,
        }),
      );
    });

    it('should order schemes by createdAt descending', async () => {
      const userId = 'user-123';

      await service.getUserSchemes(userId);

      expect(mockPrismaService.scheme.findMany).toHaveBeenCalledWith(
        expect.objectContaining({
          orderBy: { createdAt: 'desc' },
        }),
      );
    });
  });
});
