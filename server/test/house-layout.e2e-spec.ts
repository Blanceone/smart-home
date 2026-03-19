import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { PrismaService } from '../src/common/prisma/prisma.service';

describe('House Layout API (e2e)', () => {
  let app: INestApplication;
  let prisma: PrismaService;
  let testDeviceId: string;
  let testUserId: string;
  let testLayoutId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));
    prisma = app.get(PrismaService);
    await app.init();

    testDeviceId = 'f6g7h8i9j0k1l2m3';
    
    const user = await prisma.user.create({
      data: {
        deviceId: testDeviceId,
        nickname: 'HouseLayoutTestUser',
      },
    });
    testUserId = user.id;
  });

  afterAll(async () => {
    await prisma.room.deleteMany({ where: { houseLayout: { userId: testUserId } } });
    await prisma.houseLayout.deleteMany({ where: { userId: testUserId } });
    await prisma.user.deleteMany({ where: { id: testUserId } });
    await app.close();
  });

  describe('POST /users/me/house-layout', () => {
    it('should save house layout successfully', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/me/house-layout')
        .set('X-Device-ID', testDeviceId)
        .send({
          houseType: '两居室',
          totalArea: 85,
          rooms: [
            {
              name: '客厅',
              area: 25,
              specialNeeds: '需要智能灯光控制',
            },
            {
              name: '主卧',
              area: 18,
              specialNeeds: '',
            },
            {
              name: '厨房',
              area: 8,
              specialNeeds: '需要智能烟感',
            },
          ],
        })
        .expect(201);

      expect(response.body).toHaveProperty('id');
      expect(response.body).toHaveProperty('userId', testUserId);
      expect(response.body).toHaveProperty('houseType', '两居室');
      expect(response.body).toHaveProperty('totalArea', 85);
      testLayoutId = response.body.id;
    });

    it('should fail with missing required fields', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/me/house-layout')
        .set('X-Device-ID', testDeviceId)
        .send({
          houseType: '两居室',
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail with invalid house type', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/me/house-layout')
        .set('X-Device-ID', testDeviceId)
        .send({
          houseType: 'invalid-type',
          totalArea: 85,
          rooms: [{ name: '客厅', area: 25 }],
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail with negative area', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/me/house-layout')
        .set('X-Device-ID', testDeviceId)
        .send({
          houseType: '两居室',
          totalArea: -85,
          rooms: [{ name: '客厅', area: 25 }],
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail with empty rooms array', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/me/house-layout')
        .set('X-Device-ID', testDeviceId)
        .send({
          houseType: '两居室',
          totalArea: 85,
          rooms: [],
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .post('/users/me/house-layout')
        .send({
          houseType: '两居室',
          totalArea: 85,
          rooms: [{ name: '客厅', area: 25 }],
        })
        .expect(401);
    });
  });

  describe('GET /users/me/house-layout', () => {
    it('should return house layout', async () => {
      const response = await request(app.getHttpServer())
        .get('/users/me/house-layout')
        .set('X-Device-ID', testDeviceId)
        .expect(200);

      expect(response.body).toHaveProperty('id', testLayoutId);
      expect(response.body).toHaveProperty('houseType', '两居室');
      expect(response.body).toHaveProperty('totalArea', 85);
      expect(response.body).toHaveProperty('rooms');
      expect(response.body.rooms).toHaveLength(3);
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .get('/users/me/house-layout')
        .expect(401);
    });
  });
});
