from sqlalchemy import Column, BigInteger, String, DateTime, SmallInteger, ForeignKey, DECIMAL, Integer
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.core.database import Base


class House(Base):
    __tablename__ = "houses"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    user_id = Column(BigInteger, ForeignKey("users.id"), nullable=True, index=True)
    total_area = Column(DECIMAL(10, 2), nullable=False)
    image_url = Column(String(500), nullable=True)
    status = Column(SmallInteger, default=1, nullable=False)
    created_at = Column(DateTime, server_default=func.now(), nullable=False)
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now(), nullable=False)

    user = relationship("User", back_populates="houses")
    rooms = relationship("Room", back_populates="house", cascade="all, delete-orphan")


class Room(Base):
    __tablename__ = "rooms"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    house_id = Column(BigInteger, ForeignKey("houses.id"), nullable=False, index=True)
    room_name = Column(String(50), nullable=False)
    room_type = Column(String(30), nullable=False)
    length = Column(DECIMAL(10, 2), nullable=False)
    width = Column(DECIMAL(10, 2), nullable=False)
    area = Column(DECIMAL(10, 2), nullable=False)
    sort_order = Column(Integer, default=0)

    house = relationship("House", back_populates="rooms")
