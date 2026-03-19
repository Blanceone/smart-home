import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { PrismaService } from '../src/common/prisma/prisma.service';

describe('Auth API (e2e)', () => {
  let app: INestApplication;
  let prisma: PrismaService;
  let testDeviceId: string;
  let testUserId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));
    prisma = app.get(PrismaService);
    await app.init();

    testDeviceId = 'a1b2c3d4e5f67890';
  });

  afterAll(async () => {
    if (testUserId) {
      await prisma.feedback.deleteMany({ where: { userId: testUserId } });
      await prisma.schemeDevice.deleteMany({
        where: { scheme: { userId: testUserId } },
      });
      await prisma.scheme.deleteMany({ where: { userId: testUserId } });
      await prisma.houseLayout.deleteMany({ where: { userId: testUserId } });
      await prisma.userInfo.deleteMany({ where: { userId: testUserId } });
      await prisma.user.deleteMany({ where: { id: testUserId } });
    }
    await app.close();
  });

  describe('POST /users/register', () => {
    it('should register a new user successfully', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/register')
        .send({
          deviceId: testDeviceId,
          nickname: 'TestUser',
        })
        .expect(201);

      expect(response.body).toHaveProperty('id');
      expect(response.body).toHaveProperty('deviceId', testDeviceId);
      expect(response.body).toHaveProperty('isNewUser', true);
      testUserId = response.body.id;
    });

    it('should return existing user on duplicate registration', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/register')
        .send({
          deviceId: testDeviceId,
          nickname: 'DifferentName',
        })
        .expect(201);

      expect(response.body).toHaveProperty('isNewUser', false);
      expect(response.body).toHaveProperty('id', testUserId);
    });

    it('should fail with invalid deviceId format', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/register')
        .send({
          deviceId: 'invalid-device-id',
          nickname: 'TestUser',
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail with missing deviceId', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/register')
        .send({
          nickname: 'TestUser',
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });
  });

  describe('GET /users/me', () => {
    it('should return current user info', async () => {
      const response = await request(app.getHttpServer())
        .get('/users/me')
        .set('X-Device-ID', testDeviceId)
        .expect(200);

      expect(response.body).toHaveProperty('id', testUserId);
      expect(response.body).toHaveProperty('deviceId', testDeviceId);
      expect(response.body).toHaveProperty('nickname');
    });

    it('should fail without device ID', async () => {
      const response = await request(app.getHttpServer())
        .get('/users/me')
        .expect(401);

      expect(response.body.message).toContain('缺少设备ID');
    });

    it('should fail with invalid device ID format', async () => {
      const response = await request(app.getHttpServer())
        .get('/users/me')
        .set('X-Device-ID', 'invalid')
        .expect(401);

      expect(response.body.message).toContain('无效的设备ID格式');
    });

    it('should fail with non-existent device ID', async () => {
      const response = await request(app.getHttpServer())
        .get('/users/me')
        .set('X-Device-ID', '0000000000000000')
        .expect(401);

      expect(response.body.message).toContain('用户不存在');
    });
  });

  describe('PATCH /users/me', () => {
    it('should update user nickname', async () => {
      const response = await request(app.getHttpServer())
        .patch('/users/me')
        .set('X-Device-ID', testDeviceId)
        .send({ nickname: 'UpdatedName' })
        .expect(200);

      expect(response.body).toHaveProperty('nickname', 'UpdatedName');
    });

    it('should update user avatar', async () => {
      const response = await request(app.getHttpServer())
        .patch('/users/me')
        .set('X-Device-ID', testDeviceId)
        .send({ avatar: 'https://example.com/avatar.jpg' })
        .expect(200);

      expect(response.body).toHaveProperty('avatar', 'https://example.com/avatar.jpg');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .patch('/users/me')
        .send({ nickname: 'NewName' })
        .expect(401);
    });
  });
});
