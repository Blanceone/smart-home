from sqlalchemy import Column, BigInteger, String, DateTime, SmallInteger, ForeignKey, DECIMAL, Integer, Text, JSON
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base


class Brand(Base):
    __tablename__ = "brands"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    brand_name = Column(String(50), unique=True, nullable=False)
    brand_code = Column(String(30), unique=True, nullable=False)
    logo_url = Column(String(500), nullable=True)
    sort_order = Column(Integer, default=0)

    products = relationship("Product", back_populates="brand")


class Category(Base):
    __tablename__ = "categories"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    category_name = Column(String(50), nullable=False)
    category_code = Column(String(30), nullable=False)
    parent_id = Column(BigInteger, ForeignKey("categories.id"), nullable=True)
    level = Column(SmallInteger, default=1, nullable=False)
    sort_order = Column(Integer, default=0)

    parent = relationship("Category", remote_side=[id], backref="children")
    products = relationship("Product", back_populates="category")


class Product(Base):
    __tablename__ = "products"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    product_id = Column(String(50), unique=True, index=True, nullable=False)
    product_name = Column(String(200), nullable=False)
    brand_id = Column(BigInteger, ForeignKey("brands.id"), nullable=True)
    category_id = Column(BigInteger, ForeignKey("categories.id"), nullable=False)
    price = Column(DECIMAL(10, 2), nullable=False)
    original_price = Column(DECIMAL(10, 2), nullable=True)
    image_url = Column(String(500), nullable=False)
    product_url = Column(String(500), nullable=False)
    specs = Column(JSON, nullable=True)
    rating = Column(DECIMAL(3, 1), nullable=True)
    sales_count = Column(Integer, default=0)
    status = Column(SmallInteger, default=1, nullable=False)
    last_synced_at = Column(DateTime, server_default=func.now(), nullable=False)

    brand = relationship("Brand", back_populates="products")
    category = relationship("Category", back_populates="products")
    scheme_devices = relationship("SchemeDevice", back_populates="product")
