from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional
import os


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file='.env',
        env_file_encoding='utf-8',
        case_sensitive=True,
        extra='ignore'
    )

    APP_NAME: str = "Smart Home API"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = False

    DATABASE_URL: str = "mysql+pymysql://root:password@localhost:3306/smart_home"
    REDIS_URL: str = "redis://localhost:6379/0"

    SECRET_KEY: Optional[str] = None
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 120
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7

    ALLOWED_ORIGINS: str = "*"

    CELERY_BROKER_URL: str = "redis://localhost:6379/1"
    CELERY_RESULT_BACKEND: str = "redis://localhost:6379/2"

    DEEPSEEK_API_KEY: Optional[str] = None
    DEEPSEEK_API_URL: str = "https://api.deepseek.com/v1"
    DEEPSEEK_MODEL: str = "deepseek-chat"
    AI_GENERATION_TIMEOUT: int = 30

    TAOBAO_APP_KEY: Optional[str] = None
    TAOBAO_APP_SECRET: Optional[str] = None
    TAOBAO_API_URL: str = "https://eco.taobao.com/router/rest"

    OSS_ENDPOINT: Optional[str] = None
    OSS_ACCESS_KEY_ID: Optional[str] = None
    OSS_ACCESS_KEY_SECRET: Optional[str] = None
    OSS_BUCKET_NAME: Optional[str] = None
    OSS_REGION: Optional[str] = None

    @property
    def is_production(self) -> bool:
        return not self.DEBUG and os.getenv("ENVIRONMENT", "development") == "production"

    def validate_production_config(self) -> None:
        if self.is_production:
            if self.SECRET_KEY is None or self.SECRET_KEY == "your-secret-key-change-in-production":
                raise ValueError("SECRET_KEY must be set in production environment")
            if self.ALLOWED_ORIGINS == "*":
                raise ValueError("ALLOWED_ORIGINS must be configured in production environment")


settings = Settings()
