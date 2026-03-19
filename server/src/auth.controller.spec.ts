import { Test, TestingModule } from '@nestjs/testing';
import { AuthController } from './modules/auth/auth.controller';
import { AuthService } from './modules/auth/auth.service';
import { PrismaService } from './common/prisma/prisma.service';

describe('AuthController', () => {
  let controller: AuthController;
  let service: AuthService;

  const mockAuthService = {
    register: jest.fn(),
    getCurrentUser: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [AuthController],
      providers: [
        {
          provide: AuthService,
          useValue: mockAuthService,
        },
      ],
    }).compile();

    controller = module.get<AuthController>(AuthController);
    service = module.get<AuthService>(AuthService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('register', () => {
    it('should register a new user', async () => {
      const dto = {
        deviceId: 'a1b2c3d4e5f6',
        nickname: 'TestUser',
      };

      const expectedResult = {
        id: 'user-123',
        deviceId: dto.deviceId,
        nickname: dto.nickname,
        avatar: null,
        isNewUser: true,
        createdAt: '2026-03-15T10:00:00Z',
      };

      mockAuthService.register.mockResolvedValue(expectedResult);

      const result = await controller.register(dto);

      expect(result).toEqual(expectedResult);
      expect(service.register).toHaveBeenCalledWith(dto);
    });

    it('should register user without nickname', async () => {
      const dto = {
        deviceId: 'a1b2c3d4e5f6',
      };

      const expectedResult = {
        id: 'user-123',
        deviceId: dto.deviceId,
        nickname: '用户123456',
        avatar: null,
        isNewUser: true,
        createdAt: '2026-03-15T10:00:00Z',
      };

      mockAuthService.register.mockResolvedValue(expectedResult);

      const result = await controller.register(dto);

      expect(result).toEqual(expectedResult);
    });
  });

  describe('getCurrentUser', () => {
    it('should return current user info', async () => {
      const userId = 'user-123';
      const expectedResult = {
        id: userId,
        deviceId: 'a1b2c3d4e5f6',
        nickname: 'TestUser',
        avatar: null,
        createdAt: '2026-03-15T10:00:00Z',
        updatedAt: '2026-03-15T10:00:00Z',
      };

      mockAuthService.getCurrentUser.mockResolvedValue(expectedResult);

      const result = await controller.getCurrentUser(userId);

      expect(result).toEqual(expectedResult);
      expect(service.getCurrentUser).toHaveBeenCalledWith(userId);
    });
  });
});
