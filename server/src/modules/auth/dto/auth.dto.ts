import { IsString, IsOptional, MaxLength } from 'class-validator';

export class RegisterDto {
  @IsString()
  deviceId: string;

  @IsOptional()
  @IsString()
  @MaxLength(50)
  nickname?: string;

  @IsOptional()
  @IsString()
  avatar?: string;
}
