import { Test, TestingModule } from '@nestjs/testing';
import { UserController } from './modules/user/user.controller';
import { UserService } from './modules/user/user.service';

describe('UserController', () => {
  let controller: UserController;
  let service: UserService;

  const mockUserService = {
    getCurrentUser: jest.fn(),
    updateCurrentUser: jest.fn(),
    getUserSchemes: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UserController],
      providers: [
        {
          provide: UserService,
          useValue: mockUserService,
        },
      ],
    }).compile();

    controller = module.get<UserController>(UserController);
    service = module.get<UserService>(UserService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getCurrentUser', () => {
    it('should return current user info', async () => {
      const userId = 'user-123';
      const expectedResult = {
        id: userId,
        deviceId: 'a1b2c3d4e5f6',
        nickname: 'TestUser',
        avatar: null,
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      mockUserService.getCurrentUser.mockResolvedValue(expectedResult);

      const result = await controller.getCurrentUser(userId);

      expect(result).toEqual(expectedResult);
      expect(service.getCurrentUser).toHaveBeenCalledWith(userId);
    });
  });

  describe('updateCurrentUser', () => {
    it('should update user nickname', async () => {
      const userId = 'user-123';
      const dto = { nickname: 'NewNickname' };
      const expectedResult = {
        id: userId,
        nickname: 'NewNickname',
        avatar: null,
        updatedAt: new Date(),
      };

      mockUserService.updateCurrentUser.mockResolvedValue(expectedResult);

      const result = await controller.updateCurrentUser(userId, dto);

      expect(result).toEqual(expectedResult);
      expect(service.updateCurrentUser).toHaveBeenCalledWith(userId, dto);
    });

    it('should update user avatar', async () => {
      const userId = 'user-123';
      const dto = { avatar: 'https://example.com/avatar.jpg' };
      const expectedResult = {
        id: userId,
        nickname: 'TestUser',
        avatar: dto.avatar,
        updatedAt: new Date(),
      };

      mockUserService.updateCurrentUser.mockResolvedValue(expectedResult);

      const result = await controller.updateCurrentUser(userId, dto);

      expect(result).toEqual(expectedResult);
    });
  });

  describe('getUserSchemes', () => {
    it('should return user schemes list', async () => {
      const userId = 'user-123';
      const expectedResult = {
        list: [
          {
            id: 'scheme-1',
            name: 'Scheme 1',
            budget: 10000,
            totalPrice: 9500,
            deviceCount: 5,
            createdAt: new Date(),
          },
        ],
        total: 1,
        maxAllowed: 3,
      };

      mockUserService.getUserSchemes.mockResolvedValue(expectedResult);

      const result = await controller.getUserSchemes(userId);

      expect(result).toEqual(expectedResult);
      expect(service.getUserSchemes).toHaveBeenCalledWith(userId);
    });

    it('should return empty list if no schemes', async () => {
      const userId = 'user-123';
      const expectedResult = {
        list: [],
        total: 0,
        maxAllowed: 3,
      };

      mockUserService.getUserSchemes.mockResolvedValue(expectedResult);

      const result = await controller.getUserSchemes(userId);

      expect(result.list).toHaveLength(0);
    });
  });
});
