import { ApiProperty } from '@nestjs/swagger';

export class ApiResponseDto<T> {
  @ApiProperty({ description: '业务状态码', example: 0 })
  code: number;

  @ApiProperty({ description: '响应消息', example: 'success' })
  message: string;

  @ApiProperty({ description: '响应数据' })
  data: T;

  @ApiProperty({ description: '服务器时间戳', example: 1700000000000 })
  timestamp: number;
}

export class PaginationDto {
  @ApiProperty({ description: '页码', example: 1 })
  page: number;

  @ApiProperty({ description: '每页数量', example: 20 })
  pageSize: number;

  @ApiProperty({ description: '总数', example: 100 })
  total: number;

  @ApiProperty({ description: '总页数', example: 5 })
  totalPages: number;
}

export class PaginatedResponseDto<T> {
  @ApiProperty({ type: [Object] })
  list: T[];

  @ApiProperty({ type: PaginationDto })
  pagination: PaginationDto;
}
