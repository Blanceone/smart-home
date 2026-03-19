import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { PrismaService } from '../src/common/prisma/prisma.service';

describe('Feedback API (e2e)', () => {
  let app: INestApplication;
  let prisma: PrismaService;
  let testDeviceId: string;
  let testUserId: string;
  let testSchemeId: string;
  let testDeviceRecordId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));
    prisma = app.get(PrismaService);
    await app.init();

    testDeviceId = 'd4e5f6g7h8i9j0k1';
    
    const user = await prisma.user.create({
      data: {
        deviceId: testDeviceId,
        nickname: 'FeedbackTestUser',
      },
    });
    testUserId = user.id;

    const scheme = await prisma.scheme.create({
      data: {
        userId: testUserId,
        name: '测试方案',
        budget: 10000,
        totalPrice: 8500,
        status: 'completed',
        decorationGuide: { summary: '测试摘要' },
      },
    });
    testSchemeId = scheme.id;

    const device = await prisma.device.create({
      data: {
        name: '测试设备',
        brand: '测试品牌',
        category: '测试分类',
        price: 199,
      },
    });
    testDeviceRecordId = device.id;
  });

  afterAll(async () => {
    await prisma.feedback.deleteMany({ where: { userId: testUserId } });
    await prisma.scheme.deleteMany({ where: { id: testSchemeId } });
    await prisma.device.deleteMany({ where: { id: testDeviceRecordId } });
    await prisma.user.deleteMany({ where: { id: testUserId } });
    await app.close();
  });

  describe('POST /feedback/scheme-rating', () => {
    it('should submit scheme rating successfully', async () => {
      const response = await request(app.getHttpServer())
        .post('/feedback/scheme-rating')
        .set('X-Device-ID', testDeviceId)
        .send({
          schemeId: testSchemeId,
          rating: 5,
          content: '非常满意的方案',
        })
        .expect(201);

      expect(response.body).toHaveProperty('id');
      expect(response.body).toHaveProperty('createdAt');
    });

    it('should accept rating without content', async () => {
      const response = await request(app.getHttpServer())
        .post('/feedback/scheme-rating')
        .set('X-Device-ID', testDeviceId)
        .send({
          schemeId: testSchemeId,
          rating: 4,
        })
        .expect(201);

      expect(response.body).toHaveProperty('id');
    });

    it('should fail with invalid rating value', async () => {
      const response = await request(app.getHttpServer())
        .post('/feedback/scheme-rating')
        .set('X-Device-ID', testDeviceId)
        .send({
          schemeId: testSchemeId,
          rating: 6,
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail with non-existent scheme', async () => {
      const response = await request(app.getHttpServer())
        .post('/feedback/scheme-rating')
        .set('X-Device-ID', testDeviceId)
        .send({
          schemeId: 'non-existent-id',
          rating: 5,
        })
        .expect(404);

      expect(response.body.message).toContain('方案不存在');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .post('/feedback/scheme-rating')
        .send({
          schemeId: testSchemeId,
          rating: 5,
        })
        .expect(401);
    });
  });

  describe('POST /feedback/suggestion', () => {
    it('should submit suggestion successfully', async () => {
      const response = await request(app.getHttpServer())
        .post('/feedback/suggestion')
        .set('X-Device-ID', testDeviceId)
        .send({
          type: '功能建议',
          content: '希望能增加设备对比功能',
          contact: 'test@example.com',
        })
        .expect(201);

      expect(response.body).toHaveProperty('id');
      expect(response.body).toHaveProperty('createdAt');
    });

    it('should fail without content', async () => {
      const response = await request(app.getHttpServer())
        .post('/feedback/suggestion')
        .set('X-Device-ID', testDeviceId)
        .send({
          type: '功能建议',
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail with invalid type', async () => {
      const response = await request(app.getHttpServer())
        .post('/feedback/suggestion')
        .set('X-Device-ID', testDeviceId)
        .send({
          type: '无效类型',
          content: '测试内容',
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .post('/feedback/suggestion')
        .send({
          type: '功能建议',
          content: '测试内容',
        })
        .expect(401);
    });
  });

  describe('POST /feedback/data-correction', () => {
    it('should submit data correction successfully', async () => {
      const response = await request(app.getHttpServer())
        .post('/feedback/data-correction')
        .set('X-Device-ID', testDeviceId)
        .send({
          deviceId: testDeviceRecordId,
          errorType: '价格错误',
          correctInfo: '实际价格应该是199元',
        })
        .expect(201);

      expect(response.body).toHaveProperty('id');
      expect(response.body).toHaveProperty('createdAt');
    });

    it('should fail with non-existent device', async () => {
      const response = await request(app.getHttpServer())
        .post('/feedback/data-correction')
        .set('X-Device-ID', testDeviceId)
        .send({
          deviceId: 'non-existent-id',
          errorType: '价格错误',
          correctInfo: '测试信息',
        })
        .expect(404);

      expect(response.body.message).toContain('设备不存在');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .post('/feedback/data-correction')
        .send({
          deviceId: testDeviceRecordId,
          errorType: '价格错误',
          correctInfo: '测试信息',
        })
        .expect(401);
    });
  });
});
