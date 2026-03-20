import { IsString, IsNumber, IsArray, ValidateNested, IsOptional, Min, Max } from 'class-validator';
import { Type } from 'class-transformer';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

class RoomDto {
  @ApiProperty({ description: '房间名称', example: '客厅' })
  @IsString()
  name: string;

  @ApiProperty({ description: '房间面积(平方米)', example: 25 })
  @IsNumber()
  @Min(1)
  area: number;

  @ApiPropertyOptional({ description: '特殊需求' })
  @IsString()
  @IsOptional()
  specialNeeds?: string;
}

export class SaveHouseLayoutDto {
  @ApiProperty({ description: '房屋类型', example: '两居室' })
  @IsString()
  houseType: string;

  @ApiProperty({ description: '建筑面积(平方米)', example: 85 })
  @IsNumber()
  @Min(1)
  @Max(1000)
  totalArea: number;

  @ApiProperty({ description: '房间列表', type: [RoomDto] })
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => RoomDto)
  @Max(20, { message: '房间数量不能超过20个' })
  rooms: RoomDto[];
}
