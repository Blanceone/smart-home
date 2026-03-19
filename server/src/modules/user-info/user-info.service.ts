import { Injectable } from '@nestjs/common';
import { PrismaService } from '../../common/prisma/prisma.service';
import { SaveUserInfoDto } from './dto/user-info.dto';

@Injectable()
export class UserInfoService {
  constructor(private prisma: PrismaService) {}

  async saveUserInfo(userId: string, dto: SaveUserInfoDto) {
    const userInfo = await this.prisma.userInfo.upsert({
      where: { userId },
      create: {
        userId,
        age: dto.basicInfo.age,
        occupation: dto.basicInfo.occupation,
        familyMembers: dto.basicInfo.familyMembers,
        city: dto.basicInfo.city,
        sleepPattern: dto.lifestyle?.sleepPattern,
        homeActivities: dto.lifestyle?.homeActivities,
        entertainmentHabits: dto.lifestyle?.entertainmentHabits,
        deviceKnowledgeLevel: dto.deviceExperience?.knowledgeLevel,
        usedDevices: dto.deviceExperience?.usedDevices,
        decorStyle: dto.aestheticPreference?.decorStyle,
        colorPreferences: dto.aestheticPreference?.colorPreferences,
        preferredBrands: dto.brandPreference?.preferredBrands,
        isCompleted: true,
      },
      update: {
        age: dto.basicInfo.age,
        occupation: dto.basicInfo.occupation,
        familyMembers: dto.basicInfo.familyMembers,
        city: dto.basicInfo.city,
        sleepPattern: dto.lifestyle?.sleepPattern,
        homeActivities: dto.lifestyle?.homeActivities,
        entertainmentHabits: dto.lifestyle?.entertainmentHabits,
        deviceKnowledgeLevel: dto.deviceExperience?.knowledgeLevel,
        usedDevices: dto.deviceExperience?.usedDevices,
        decorStyle: dto.aestheticPreference?.decorStyle,
        colorPreferences: dto.aestheticPreference?.colorPreferences,
        preferredBrands: dto.brandPreference?.preferredBrands,
        isCompleted: true,
        updatedAt: new Date(),
      },
    });

    return {
      id: userInfo.id,
      userId: userInfo.userId,
      completedAt: userInfo.updatedAt,
    };
  }

  async getUserInfo(userId: string) {
    const userInfo = await this.prisma.userInfo.findUnique({
      where: { userId },
    });

    if (!userInfo) {
      return null;
    }

    return {
      basicInfo: {
        age: userInfo.age,
        occupation: userInfo.occupation,
        familyMembers: userInfo.familyMembers,
        city: userInfo.city,
      },
      lifestyle: {
        sleepPattern: userInfo.sleepPattern,
        homeActivities: userInfo.homeActivities,
        entertainmentHabits: userInfo.entertainmentHabits,
      },
      deviceExperience: {
        knowledgeLevel: userInfo.deviceKnowledgeLevel,
        usedDevices: userInfo.usedDevices,
      },
      aestheticPreference: {
        decorStyle: userInfo.decorStyle,
        colorPreferences: userInfo.colorPreferences,
      },
      brandPreference: {
        preferredBrands: userInfo.preferredBrands,
      },
      isCompleted: userInfo.isCompleted,
      updatedAt: userInfo.updatedAt,
    };
  }
}
