from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    APP_NAME: str = "Smart Home API"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = False

    DATABASE_URL: str = "mysql+pymysql://root:password@localhost:3306/smart_home"
    REDIS_URL: str = "redis://localhost:6379/0"

    SECRET_KEY: str = "your-secret-key-change-in-production"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 120
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7

    DEEPSEEK_API_KEY: Optional[str] = None
    DEEPSEEK_API_URL: str = "https://api.deepseek.com/v1"
    DEEPSEEK_MODEL: str = "deepseek-chat"

    TAOBAO_APP_KEY: Optional[str] = None
    TAOBAO_APP_SECRET: Optional[str] = None
    TAOBAO_API_URL: str = "https://eco.taobao.com/router/rest"

    OSS_ENDPOINT: Optional[str] = None
    OSS_ACCESS_KEY_ID: Optional[str] = None
    OSS_ACCESS_KEY_SECRET: Optional[str] = None
    OSS_BUCKET_NAME: Optional[str] = None
    OSS_REGION: Optional[str] = None

    ALLOWED_ORIGINS: str = "*"

    CELERY_BROKER_URL: str = "redis://localhost:6379/1"
    CELERY_RESULT_BACKEND: str = "redis://localhost:6379/2"

    AI_GENERATION_TIMEOUT: int = 30
    AI_GENERATION_MAX_RETRIES: int = 2

    class Config:
        env_file = ".env"
        case_sensitive = True


settings = Settings()
