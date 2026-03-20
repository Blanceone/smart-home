import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { PrismaService } from '../src/common/prisma/prisma.service';

describe('Device API (e2e)', () => {
  let app: INestApplication;
  let prisma: PrismaService;
  let testDeviceId: string;
  let testUserId: string;
  let testDeviceRecordId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));
    prisma = app.get(PrismaService);
    await app.init();

    testDeviceId = 'c3d4e5f6g7h8i9j0';

    const user = await prisma.user.create({
      data: {
        deviceId: testDeviceId,
        nickname: 'DeviceTestUser',
      },
    });
    testUserId = user.id;

    const device = await prisma.device.create({
      data: {
        name: '测试智能灯',
        brand: '小米/米家',
        category: '照明',
        price: 299,
        description: '测试设备描述',
        features: ['亮度调节', '色温调节'],
        specifications: { power: '36W', voltage: '220V' },
        applicableScenes: ['客厅', '卧室'],
      },
    });
    testDeviceRecordId = device.id;
  });

  afterAll(async () => {
    await prisma.device.deleteMany({ where: { id: testDeviceRecordId } });
    await prisma.user.deleteMany({ where: { id: testUserId } });
    await app.close();
  });

  describe('GET /devices/:deviceId', () => {
    it('should return device detail', async () => {
      const response = await request(app.getHttpServer())
        .get(`/devices/${testDeviceRecordId}`)
        .set('X-Device-ID', testDeviceId)
        .expect(200);

      expect(response.body).toHaveProperty('id', testDeviceRecordId);
      expect(response.body).toHaveProperty('name', '测试智能灯');
      expect(response.body).toHaveProperty('brand', '小米/米家');
      expect(response.body).toHaveProperty('category', '照明');
      expect(response.body).toHaveProperty('price');
    });

    it('should fail with non-existent device', async () => {
      const response = await request(app.getHttpServer())
        .get('/devices/non-existent-id')
        .set('X-Device-ID', testDeviceId)
        .expect(404);

      expect(response.body.message).toContain('设备不存在');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer()).get(`/devices/${testDeviceRecordId}`).expect(401);
    });
  });

  describe('GET /devices', () => {
    it('should return paginated device list', async () => {
      const response = await request(app.getHttpServer())
        .get('/devices')
        .set('X-Device-ID', testDeviceId)
        .query({ page: 1, pageSize: 10 })
        .expect(200);

      expect(response.body).toHaveProperty('list');
      expect(response.body).toHaveProperty('pagination');
      expect(response.body.pagination).toHaveProperty('page', 1);
      expect(response.body.pagination).toHaveProperty('pageSize', 10);
      expect(response.body.pagination).toHaveProperty('total');
      expect(response.body.pagination).toHaveProperty('totalPages');
    });

    it('should filter by keyword', async () => {
      const response = await request(app.getHttpServer())
        .get('/devices')
        .set('X-Device-ID', testDeviceId)
        .query({ keyword: '智能灯' })
        .expect(200);

      expect(response.body).toHaveProperty('list');
    });

    it('should filter by category', async () => {
      const response = await request(app.getHttpServer())
        .get('/devices')
        .set('X-Device-ID', testDeviceId)
        .query({ category: '照明' })
        .expect(200);

      expect(response.body).toHaveProperty('list');
    });

    it('should filter by brand', async () => {
      const response = await request(app.getHttpServer())
        .get('/devices')
        .set('X-Device-ID', testDeviceId)
        .query({ brand: '小米/米家' })
        .expect(200);

      expect(response.body).toHaveProperty('list');
    });

    it('should filter by price range', async () => {
      const response = await request(app.getHttpServer())
        .get('/devices')
        .set('X-Device-ID', testDeviceId)
        .query({ minPrice: 100, maxPrice: 500 })
        .expect(200);

      expect(response.body).toHaveProperty('list');
    });

    it('should limit pageSize to 100', async () => {
      const response = await request(app.getHttpServer())
        .get('/devices')
        .set('X-Device-ID', testDeviceId)
        .query({ page: 1, pageSize: 200 })
        .expect(200);

      expect(response.body.pagination.pageSize).toBeLessThanOrEqual(100);
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer()).get('/devices').expect(401);
    });
  });

  describe('GET /devices/:deviceId/purchase-url', () => {
    it('should return purchase URL', async () => {
      const response = await request(app.getHttpServer())
        .get(`/devices/${testDeviceRecordId}/purchase-url`)
        .set('X-Device-ID', testDeviceId)
        .expect(200);

      expect(response.body).toHaveProperty('purchaseUrl');
      expect(response.body).toHaveProperty('price');
    });

    it('should fail with non-existent device', async () => {
      const response = await request(app.getHttpServer())
        .get('/devices/non-existent-id/purchase-url')
        .set('X-Device-ID', testDeviceId)
        .expect(404);

      expect(response.body.message).toContain('设备不存在');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .get(`/devices/${testDeviceRecordId}/purchase-url`)
        .expect(401);
    });
  });
});
