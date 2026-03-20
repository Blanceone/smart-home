import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { PrismaService } from '../src/common/prisma/prisma.service';

describe('Scheme API (e2e)', () => {
  let app: INestApplication;
  let prisma: PrismaService;
  let testDeviceId: string;
  let testUserId: string;
  let testSchemeId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));
    prisma = app.get(PrismaService);
    await app.init();

    testDeviceId = 'b2c3d4e5f6g7h8i9';

    const user = await prisma.user.create({
      data: {
        deviceId: testDeviceId,
        nickname: 'SchemeTestUser',
      },
    });
    testUserId = user.id;

    await prisma.userInfo.create({
      data: {
        userId: testUserId,
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
      },
    });

    await prisma.houseLayout.create({
      data: {
        userId: testUserId,
        houseType: '两居室',
        totalArea: 85,
        rooms: {
          create: [
            { name: '客厅', area: 25, sortOrder: 1 },
            { name: '主卧', area: 18, sortOrder: 2 },
          ],
        },
      },
    });
  });

  afterAll(async () => {
    await prisma.feedback.deleteMany({ where: { userId: testUserId } });
    await prisma.schemeDevice.deleteMany({
      where: { scheme: { userId: testUserId } },
    });
    await prisma.scheme.deleteMany({ where: { userId: testUserId } });
    await prisma.houseLayout.deleteMany({ where: { userId: testUserId } });
    await prisma.userInfo.deleteMany({ where: { userId: testUserId } });
    await prisma.user.deleteMany({ where: { id: testUserId } });
    await app.close();
  });

  describe('POST /schemes/generate', () => {
    it('should generate a scheme successfully', async () => {
      const response = await request(app.getHttpServer())
        .post('/schemes/generate')
        .set('X-Device-ID', testDeviceId)
        .send({ budget: 10000 })
        .expect(201);

      expect(response.body).toHaveProperty('id');
      expect(response.body).toHaveProperty('name');
      expect(response.body).toHaveProperty('budget');
      expect(response.body).toHaveProperty('totalPrice');
      expect(response.body).toHaveProperty('devices');
      testSchemeId = response.body.id;
    }, 60000);

    it('should fail without budget', async () => {
      const response = await request(app.getHttpServer())
        .post('/schemes/generate')
        .set('X-Device-ID', testDeviceId)
        .send({})
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail with invalid budget value', async () => {
      const response = await request(app.getHttpServer())
        .post('/schemes/generate')
        .set('X-Device-ID', testDeviceId)
        .send({ budget: -1000 })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .post('/schemes/generate')
        .send({ budget: 10000 })
        .expect(401);
    });
  });

  describe('GET /schemes/:schemeId', () => {
    it('should return scheme detail', async () => {
      if (!testSchemeId) {
        console.warn('Skipping test: no scheme generated');
        return;
      }

      const response = await request(app.getHttpServer())
        .get(`/schemes/${testSchemeId}`)
        .set('X-Device-ID', testDeviceId)
        .expect(200);

      expect(response.body).toHaveProperty('id', testSchemeId);
      expect(response.body).toHaveProperty('devices');
    });

    it('should fail with non-existent scheme', async () => {
      const response = await request(app.getHttpServer())
        .get('/schemes/non-existent-id')
        .set('X-Device-ID', testDeviceId)
        .expect(404);

      expect(response.body.message).toContain('方案不存在');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .get(`/schemes/${testSchemeId || 'test-id'}`)
        .expect(401);
    });
  });

  describe('POST /schemes/:schemeId/save', () => {
    it('should save scheme successfully', async () => {
      if (!testSchemeId) {
        console.warn('Skipping test: no scheme generated');
        return;
      }

      const response = await request(app.getHttpServer())
        .post(`/schemes/${testSchemeId}/save`)
        .set('X-Device-ID', testDeviceId)
        .expect(201);

      expect(response.body).toHaveProperty('id', testSchemeId);
      expect(response.body).toHaveProperty('savedAt');
    });

    it('should fail with non-existent scheme', async () => {
      const response = await request(app.getHttpServer())
        .post('/schemes/non-existent-id/save')
        .set('X-Device-ID', testDeviceId)
        .expect(404);

      expect(response.body.message).toContain('方案不存在');
    });
  });

  describe('GET /users/me/schemes', () => {
    it('should return user schemes list', async () => {
      const response = await request(app.getHttpServer())
        .get('/users/me/schemes')
        .set('X-Device-ID', testDeviceId)
        .expect(200);

      expect(response.body).toHaveProperty('list');
      expect(response.body).toHaveProperty('total');
      expect(response.body).toHaveProperty('maxAllowed', 3);
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer()).get('/users/me/schemes').expect(401);
    });
  });

  describe('DELETE /schemes/:schemeId', () => {
    it('should delete scheme successfully', async () => {
      if (!testSchemeId) {
        console.warn('Skipping test: no scheme generated');
        return;
      }

      await request(app.getHttpServer())
        .delete(`/schemes/${testSchemeId}`)
        .set('X-Device-ID', testDeviceId)
        .expect(200);
    });

    it('should fail with non-existent scheme', async () => {
      const response = await request(app.getHttpServer())
        .delete('/schemes/non-existent-id')
        .set('X-Device-ID', testDeviceId)
        .expect(404);

      expect(response.body.message).toContain('方案不存在');
    });
  });
});
