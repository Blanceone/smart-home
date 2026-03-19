import { IsNumber, IsBoolean, IsOptional, Min, Max } from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class GenerateSchemeDto {
  @ApiProperty({ description: '预算金额(元)', example: 10000 })
  @IsNumber()
  @Min(1)
  @Max(1000000)
  budget: number;

  @ApiPropertyOptional({ description: '是否重新生成', default: false })
  @IsBoolean()
  @IsOptional()
  regenerate?: boolean;
}
