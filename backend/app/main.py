from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.shared.exceptions import register_exception_handlers
from app.modules.product.routes import router as product_router, brand_router, category_router
from app.modules.scheme.routes import router as scheme_router
from app.modules.house.routes import router as house_router
from app.modules.product.models import Brand, Category, Product
from app.modules.scheme.models import Scheme, SchemeDevice, Task
from app.modules.house.models import House, Room

app = FastAPI(
    title=settings.APP_NAME,
    version=settings.APP_VERSION,
    docs_url="/docs",
    redoc_url="/redoc"
)

origins = settings.ALLOWED_ORIGINS.split(",") if settings.ALLOWED_ORIGINS else ["*"]
if settings.is_production and "*" in origins:
    raise ValueError("Wildcard '*' origin is not allowed in production. Configure ALLOWED_ORIGINS properly.")

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allow_headers=["*"],
)

register_exception_handlers(app)

app.include_router(house_router)
app.include_router(product_router)
app.include_router(brand_router)
app.include_router(category_router)
app.include_router(scheme_router)


@app.get("/health")
def health_check():
    return {"status": "healthy"}


@app.get("/")
def root():
    return {
        "name": settings.APP_NAME,
        "version": settings.APP_VERSION,
        "status": "running"
    }
