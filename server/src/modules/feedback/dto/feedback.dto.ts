import {
  IsString,
  IsNumber,
  IsOptional,
  Min,
  Max,
  MaxLength,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class SchemeRatingDto {
  @ApiProperty({ description: '方案ID' })
  @IsString()
  schemeId: string;

  @ApiProperty({ description: '评分(1-5)', minimum: 1, maximum: 5 })
  @IsNumber()
  @Min(1)
  @Max(5)
  rating: number;

  @ApiPropertyOptional({ description: '评价内容', maxLength: 500 })
  @IsString()
  @IsOptional()
  @MaxLength(500)
  content?: string;
}

export class SuggestionDto {
  @ApiProperty({ description: '类型', enum: ['功能建议', '问题反馈', '其他'] })
  @IsString()
  type: string;

  @ApiProperty({ description: '反馈内容', maxLength: 500 })
  @IsString()
  @MaxLength(500)
  content: string;

  @ApiPropertyOptional({ description: '联系方式', maxLength: 100 })
  @IsString()
  @IsOptional()
  @MaxLength(100)
  contact?: string;
}

export class DataCorrectionDto {
  @ApiProperty({ description: '设备ID' })
  @IsString()
  deviceId: string;

  @ApiProperty({
    description: '错误类型',
    enum: ['价格错误', '信息错误', '其他'],
  })
  @IsString()
  errorType: string;

  @ApiProperty({ description: '正确信息', maxLength: 500 })
  @IsString()
  @MaxLength(500)
  correctInfo: string;
}
