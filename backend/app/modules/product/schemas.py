from pydantic import BaseModel, Field
from typing import Optional, List
from decimal import Decimal
from datetime import datetime


class BrandBase(BaseModel):
    brand_name: str = Field(..., min_length=1, max_length=50)
    brand_code: str = Field(..., min_length=1, max_length=30)


class BrandCreate(BrandBase):
    logo_url: Optional[str] = None
    sort_order: int = 0


class BrandResponse(BrandBase):
    id: int
    logo_url: Optional[str]
    sort_order: int

    class Config:
        from_attributes = True


class CategoryBase(BaseModel):
    category_name: str = Field(..., min_length=1, max_length=50)
    category_code: str = Field(..., min_length=1, max_length=30)


class CategoryCreate(CategoryBase):
    parent_id: Optional[int] = None
    level: int = 1
    sort_order: int = 0


class CategoryResponse(CategoryBase):
    id: int
    parent_id: Optional[int]
    level: int
    sort_order: int

    class Config:
        from_attributes = True


class ProductBase(BaseModel):
    product_id: str = Field(..., min_length=1, max_length=50)
    product_name: str = Field(..., min_length=1, max_length=200)


class ProductCreate(ProductBase):
    brand_id: Optional[int] = None
    category_id: int
    price: Decimal = Field(..., gt=0)
    original_price: Optional[Decimal] = Field(None, gt=0)
    image_url: str
    product_url: str
    specs: Optional[dict] = None
    rating: Optional[Decimal] = Field(None, ge=0, le=5)
    sales_count: int = 0


class ProductResponse(ProductBase):
    id: int
    brand_id: Optional[int]
    category_id: int
    price: Decimal
    original_price: Optional[Decimal]
    image_url: str
    product_url: str
    specs: Optional[dict]
    rating: Optional[Decimal]
    sales_count: int
    status: int
    last_synced_at: datetime
    brand_name: Optional[str] = None
    category_name: Optional[str] = None

    class Config:
        from_attributes = True


class ProductMatchRequest(BaseModel):
    device_type: str = Field(..., min_length=1)
    category_id: Optional[int] = None
    preferred_brands: Optional[List[int]] = None
    excluded_brands: Optional[List[int]] = None
    budget_min: Optional[Decimal] = Field(None, ge=0)
    budget_max: Optional[Decimal] = Field(None, ge=0)
    limit: int = Field(default=5, ge=1, le=20)


class ProductMatchResponse(BaseModel):
    product_id: int
    product_name: str
    brand_name: Optional[str]
    category_name: Optional[str]
    price: Decimal
    image_url: str
    product_url: str
    rating: Optional[Decimal]
    sales_count: int
    match_score: Optional[float] = None
