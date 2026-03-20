import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { PrismaService } from '../src/common/prisma/prisma.service';

describe('User Info API (e2e)', () => {
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

    testDeviceId = 'e5f6g7h8i9j0k1l2';

    const user = await prisma.user.create({
      data: {
        deviceId: testDeviceId,
        nickname: 'UserInfoTestUser',
      },
    });
    testUserId = user.id;
  });

  afterAll(async () => {
    await prisma.userInfo.deleteMany({ where: { userId: testUserId } });
    await prisma.user.deleteMany({ where: { id: testUserId } });
    await app.close();
  });

  describe('POST /users/me/info', () => {
    it('should save user info successfully', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/me/info')
        .set('X-Device-ID', testDeviceId)
        .send({
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
        })
        .expect(201);

      expect(response.body).toHaveProperty('id');
      expect(response.body).toHaveProperty('userId', testUserId);
    });

    it('should fail with missing required fields', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/me/info')
        .set('X-Device-ID', testDeviceId)
        .send({
          basicInfo: {
            age: '26-30',
          },
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail with invalid age value', async () => {
      const response = await request(app.getHttpServer())
        .post('/users/me/info')
        .set('X-Device-ID', testDeviceId)
        .send({
          basicInfo: {
            age: 'invalid-age',
            occupation: '上班族',
            familyMembers: ['情侣'],
            city: '北京',
          },
        })
        .expect(400);

      expect(response.body).toHaveProperty('message');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer())
        .post('/users/me/info')
        .send({
          basicInfo: {
            age: '26-30',
            occupation: '上班族',
            familyMembers: ['情侣'],
            city: '北京',
          },
        })
        .expect(401);
    });
  });

  describe('GET /users/me/info', () => {
    it('should return user info', async () => {
      const response = await request(app.getHttpServer())
        .get('/users/me/info')
        .set('X-Device-ID', testDeviceId)
        .expect(200);

      expect(response.body).toHaveProperty('basicInfo');
      expect(response.body).toHaveProperty('lifestyle');
      expect(response.body).toHaveProperty('deviceExperience');
      expect(response.body).toHaveProperty('aestheticPreference');
      expect(response.body).toHaveProperty('brandPreference');
    });

    it('should fail without authentication', async () => {
      await request(app.getHttpServer()).get('/users/me/info').expect(401);
    });
  });
});
