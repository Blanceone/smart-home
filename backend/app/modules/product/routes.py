from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session, joinedload
from sqlalchemy import desc
from typing import List, Optional
from decimal import Decimal
from app.core.database import get_db
from app.modules.product.models import Product, Brand, Category
from app.modules.product.schemas import (
    BrandCreate, BrandResponse, CategoryCreate, CategoryResponse,
    ProductCreate, ProductResponse, ProductMatchRequest, ProductMatchResponse
)
from app.shared.schema import success_response, error_response

router = APIRouter(prefix="/api/v1/products", tags=["商品"])
brand_router = APIRouter(prefix="/api/v1/brands", tags=["品牌"])
category_router = APIRouter(prefix="/api/v1/categories", tags=["分类"])


@brand_router.get("", response_model=dict)
def list_brands(
    db: Session = Depends(get_db)
):
    brands = db.query(Brand).order_by(Brand.sort_order).all()
    return success_response(
        data=[
            {
                "id": brand.id,
                "brand_name": brand.brand_name,
                "brand_code": brand.brand_code,
                "logo_url": brand.logo_url,
                "sort_order": brand.sort_order
            }
            for brand in brands
        ]
    )


@brand_router.get("/{brand_id}", response_model=dict)
def get_brand(brand_id: int, db: Session = Depends(get_db)):
    brand = db.query(Brand).filter(Brand.id == brand_id).first()
    if not brand:
        return error_response(code=50001, message="品牌不存在")
    return success_response(
        data={
            "id": brand.id,
            "brand_name": brand.brand_name,
            "brand_code": brand.brand_code,
            "logo_url": brand.logo_url,
            "sort_order": brand.sort_order
        }
    )


@category_router.get("", response_model=dict)
def list_categories(
    parent_id: Optional[int] = None,
    db: Session = Depends(get_db)
):
    query = db.query(Category)
    if parent_id is not None:
        query = query.filter(Category.parent_id == parent_id)
    else:
        query = query.filter(Category.parent_id == None)
    
    categories = query.order_by(Category.sort_order).all()
    return success_response(
        data=[
            {
                "id": cat.id,
                "category_name": cat.category_name,
                "category_code": cat.category_code,
                "parent_id": cat.parent_id,
                "level": cat.level,
                "sort_order": cat.sort_order
            }
            for cat in categories
        ]
    )


@router.get("", response_model=dict)
def list_products(
    page: int = 1,
    page_size: int = 20,
    category_id: Optional[int] = None,
    brand_id: Optional[int] = None,
    keyword: Optional[str] = None,
    min_price: Optional[Decimal] = None,
    max_price: Optional[Decimal] = None,
    sort_by: Optional[str] = "created_at",
    order: Optional[str] = "desc",
    db: Session = Depends(get_db)
):
    query = db.query(Product).filter(Product.status == 1)

    if category_id:
        query = query.filter(Product.category_id == category_id)
    if brand_id:
        query = query.filter(Product.brand_id == brand_id)
    if keyword:
        query = query.filter(Product.product_name.like(f"%{keyword}%"))
    if min_price is not None:
        query = query.filter(Product.price >= min_price)
    if max_price is not None:
        query = query.filter(Product.price <= max_price)

    if sort_by == "price":
        order_col = Product.price
    elif sort_by == "rating":
        order_col = Product.rating
    elif sort_by == "sales":
        order_col = Product.sales_count
    else:
        order_col = Product.last_synced_at

    if order == "asc":
        query = query.order_by(order_col)
    else:
        query = query.order_by(desc(order_col))

    total = query.count()
    products = query.offset((page - 1) * page_size).limit(page_size).all()

    product_list = []
    for product in products:
        product_list.append({
            "id": product.id,
            "product_id": product.product_id,
            "product_name": product.product_name,
            "brand_id": product.brand_id,
            "category_id": product.category_id,
            "price": product.price,
            "original_price": product.original_price,
            "image_url": product.image_url,
            "product_url": product.product_url,
            "specs": product.specs,
            "rating": product.rating,
            "sales_count": product.sales_count,
            "status": product.status,
            "last_synced_at": product.last_synced_at.isoformat() if product.last_synced_at else None,
            "brand_name": product.brand.brand_name if product.brand else None,
            "category_name": product.category.category_name if product.category else None
        })

    return success_response(
        data={
            "list": product_list,
            "pagination": {
                "page": page,
                "page_size": page_size,
                "total": total,
                "total_pages": (total + page_size - 1) // page_size if total > 0 else 0
            }
        }
    )


@router.get("/{product_id}", response_model=dict)
def get_product(product_id: int, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id, Product.status == 1).first()
    if not product:
        return error_response(code=50001, message="商品不存在")

    return success_response(
        data={
            "id": product.id,
            "product_id": product.product_id,
            "product_name": product.product_name,
            "brand_id": product.brand_id,
            "category_id": product.category_id,
            "price": product.price,
            "original_price": product.original_price,
            "image_url": product.image_url,
            "product_url": product.product_url,
            "specs": product.specs,
            "rating": product.rating,
            "sales_count": product.sales_count,
            "status": product.status,
            "last_synced_at": product.last_synced_at.isoformat() if product.last_synced_at else None,
            "brand_name": product.brand.brand_name if product.brand else None,
            "category_name": product.category.category_name if product.category else None
        }
    )


@router.post("/match", response_model=dict)
def match_products(
    match_request: ProductMatchRequest,
    db: Session = Depends(get_db)
):
    query = db.query(Product).filter(Product.status == 1)

    if match_request.category_id:
        query = query.filter(Product.category_id == match_request.category_id)
    
    if match_request.preferred_brands:
        query = query.filter(Product.brand_id.in_(match_request.preferred_brands))
    
    if match_request.excluded_brands:
        query = query.filter(Product.brand_id.notin_(match_request.excluded_brands) if Product.brand_id else True)
    
    if match_request.budget_min is not None:
        query = query.filter(Product.price >= match_request.budget_min)
    if match_request.budget_max is not None:
        query = query.filter(Product.price <= match_request.budget_max)

    products = query.order_by(desc(Product.rating), desc(Product.sales_count)).limit(match_request.limit).all()

    matched = []
    for product in products:
        matched.append({
            "product_id": product.id,
            "product_name": product.product_name,
            "brand_name": product.brand.brand_name if product.brand else None,
            "category_name": product.category.category_name if product.category else None,
            "price": product.price,
            "image_url": product.image_url,
            "product_url": product.product_url,
            "rating": product.rating,
            "sales_count": product.sales_count,
            "match_score": None
        })

    return success_response(data=matched)
