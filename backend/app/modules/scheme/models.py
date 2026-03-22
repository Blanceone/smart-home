from sqlalchemy import Column, BigInteger, String, DateTime, SmallInteger, ForeignKey, DECIMAL, Text, JSON, Boolean, Integer
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base


class UserPreference(Base):
    __tablename__ = "user_preferences"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    user_id = Column(BigInteger, nullable=True)
    budget_min = Column(DECIMAL(12, 2), default=0)
    budget_max = Column(DECIMAL(12, 2), default=100000)
    preferred_brands = Column(JSON, nullable=True)
    excluded_brands = Column(JSON, nullable=True)
    created_at = Column(DateTime, server_default=func.now(), nullable=False)
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now(), nullable=False)


class Questionnaire(Base):
    __tablename__ = "questionnaires"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    user_id = Column(BigInteger, nullable=True)
    living_status = Column(String(20), nullable=False)
    resident_count = Column(Integer, default=1)
    has_elderly = Column(Boolean, default=False)
    has_children = Column(Boolean, default=False)
    has_pets = Column(Boolean, default=False)
    preferred_scenarios = Column(JSON, nullable=True)
    sleep_pattern = Column(String(20), nullable=True)
    knowledge_level = Column(String(20), nullable=True)
    created_at = Column(DateTime, server_default=func.now(), nullable=False)


class Scheme(Base):
    __tablename__ = "schemes"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    user_id = Column(BigInteger, nullable=True)
    house_id = Column(BigInteger, ForeignKey("houses.id"), nullable=False, index=True)
    scheme_name = Column(String(100), nullable=False)
    description = Column(Text, nullable=True)
    total_price = Column(DECIMAL(12, 2), default=0)
    status = Column(SmallInteger, default=1, nullable=False)
    created_at = Column(DateTime, server_default=func.now(), nullable=False)
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now(), nullable=False)

    house = relationship("House")
    devices = relationship("SchemeDevice", back_populates="scheme", cascade="all, delete-orphan")


class SchemeDevice(Base):
    __tablename__ = "scheme_devices"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    scheme_id = Column(BigInteger, ForeignKey("schemes.id"), nullable=False, index=True)
    product_id = Column(BigInteger, ForeignKey("products.id"), nullable=True)
    product_snapshot = Column(JSON, nullable=True)
    room_name = Column(String(50), nullable=False)
    quantity = Column(Integer, default=1)
    unit_price = Column(DECIMAL(10, 2), nullable=False)
    subtotal = Column(DECIMAL(12, 2), nullable=False)
    reason = Column(Text, nullable=True)

    scheme = relationship("Scheme", back_populates="devices")
    product = relationship("Product")


class Task(Base):
    __tablename__ = "tasks"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    task_id = Column(String(100, collation='utf8mb4_unicode_ci'), unique=True, nullable=False, index=True)
    user_id = Column(BigInteger, nullable=True)
    task_type = Column(String(50), nullable=False)
    status = Column(String(20), default="pending", nullable=False)
    result = Column(JSON, nullable=True)
    error = Column(Text, nullable=True)
    created_at = Column(DateTime, server_default=func.now(), nullable=False)
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now(), nullable=False)
