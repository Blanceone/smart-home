from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime


class UserBase(BaseModel):
    phone: str = Field(..., min_length=11, max_length=20)


class UserCreate(UserBase):
    password: str = Field(..., min_length=6, max_length=50)
    nickname: Optional[str] = Field(None, max_length=50)


class UserUpdate(BaseModel):
    nickname: Optional[str] = Field(None, max_length=50)
    avatar_url: Optional[str] = None


class UserResponse(UserBase):
    id: int
    nickname: Optional[str]
    avatar_url: Optional[str]
    status: int
    created_at: datetime

    class Config:
        from_attributes = True


class LoginRequest(BaseModel):
    phone: str = Field(..., min_length=11, max_length=20)
    password: str = Field(..., min_length=6)


class TokenResponse(BaseModel):
    access_token: str
    refresh_token: str
    expires_in: int


class RefreshTokenRequest(BaseModel):
    refresh_token: str
