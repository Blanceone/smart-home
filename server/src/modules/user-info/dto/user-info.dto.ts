import {
  IsString,
  IsOptional,
  IsArray,
  IsBoolean,
  ValidateNested,
  MaxLength,
} from 'class-validator';
import { Type } from 'class-transformer';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

class BasicInfoDto {
  @ApiProperty({ description: '年龄段', example: '26-30' })
  @IsString()
  age: string;

  @ApiProperty({ description: '职业', example: '上班族' })
  @IsString()
  occupation: string;

  @ApiProperty({ description: '家庭成员', type: [String], example: ['情侣'] })
  @IsArray()
  @IsString({ each: true })
  familyMembers: string[];

  @ApiProperty({ description: '居住城市', example: '北京' })
  @IsString()
  @MaxLength(50)
  city: string;
}

class LifestyleDto {
  @ApiPropertyOptional({ description: '作息时间' })
  @IsString()
  @IsOptional()
  sleepPattern?: string;

  @ApiPropertyOptional({ description: '在家活动偏好', type: [String] })
  @IsArray()
  @IsString({ each: true })
  @IsOptional()
  homeActivities?: string[];

  @ApiPropertyOptional({ description: '娱乐习惯', type: [String] })
  @IsArray()
  @IsString({ each: true })
  @IsOptional()
  entertainmentHabits?: string[];
}

class DeviceExperienceDto {
  @ApiPropertyOptional({ description: '智能设备了解程度' })
  @IsString()
  @IsOptional()
  knowledgeLevel?: string;

  @ApiPropertyOptional({ description: '使用过的设备', type: [String] })
  @IsArray()
  @IsString({ each: true })
  @IsOptional()
  usedDevices?: string[];
}

class AestheticPreferenceDto {
  @ApiPropertyOptional({ description: '装修风格' })
  @IsString()
  @IsOptional()
  decorStyle?: string;

  @ApiPropertyOptional({ description: '颜色偏好', type: [String] })
  @IsArray()
  @IsString({ each: true })
  @IsOptional()
  colorPreferences?: string[];
}

class BrandPreferenceDto {
  @ApiPropertyOptional({ description: '品牌偏好', type: [String] })
  @IsArray()
  @IsString({ each: true })
  @IsOptional()
  preferredBrands?: string[];
}

export class SaveUserInfoDto {
  @ApiProperty({ type: BasicInfoDto })
  @ValidateNested()
  @Type(() => BasicInfoDto)
  basicInfo: BasicInfoDto;

  @ApiPropertyOptional({ type: LifestyleDto })
  @ValidateNested()
  @Type(() => LifestyleDto)
  @IsOptional()
  lifestyle?: LifestyleDto;

  @ApiPropertyOptional({ type: DeviceExperienceDto })
  @ValidateNested()
  @Type(() => DeviceExperienceDto)
  @IsOptional()
  deviceExperience?: DeviceExperienceDto;

  @ApiPropertyOptional({ type: AestheticPreferenceDto })
  @ValidateNested()
  @Type(() => AestheticPreferenceDto)
  @IsOptional()
  aestheticPreference?: AestheticPreferenceDto;

  @ApiPropertyOptional({ type: BrandPreferenceDto })
  @ValidateNested()
  @Type(() => BrandPreferenceDto)
  @IsOptional()
  brandPreference?: BrandPreferenceDto;
}
