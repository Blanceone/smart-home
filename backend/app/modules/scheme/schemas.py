from pydantic import BaseModel, Field
from typing import Optional, List
from decimal import Decimal
from datetime import datetime


class HouseLayoutRoom(BaseModel):
    room_name: str
    room_type: str
    length: Optional[float] = None
    width: Optional[float] = None
    area: Optional[float] = None


class HouseLayout(BaseModel):
    total_area: Optional[float] = None
    rooms: List[HouseLayoutRoom] = Field(default_factory=list, min_length=1)


class QuestionnaireData(BaseModel):
    living_status: str = Field(..., description="居住状态: own/rent")
    resident_count: int = Field(default=1, ge=1, description="居住人数")
    has_elderly: bool = Field(default=False, description="是否有老人")
    has_children: bool = Field(default=False, description="是否有儿童")
    has_pets: bool = Field(default=False, description="是否有宠物")
    preferred_scenarios: List[str] = Field(default_factory=list, description="偏好场景")
    sleep_pattern: Optional[str] = Field(None, description="睡眠模式: early/late/irregular")
    knowledge_level: Optional[str] = Field(None, description="智能家居了解程度: none/basic/familiar")


class PreferencesData(BaseModel):
    budget_min: float = Field(default=0, ge=0, description="最低预算")
    budget_max: float = Field(default=100000, ge=0, description="最高预算")
    preferred_brands: List[str] = Field(default_factory=list, description="偏好品牌")
    excluded_brands: List[str] = Field(default_factory=list, description="排除品牌")


class SchemeGenerateRequest(BaseModel):
    house_layout: HouseLayout
    questionnaire: QuestionnaireData
    preferences: PreferencesData


class AIDeviceItem(BaseModel):
    device_type: str
    device_name: str
    room: str
    quantity: int
    reason: str
    matched_product: Optional[dict] = None
    subtotal: float


class AISchemeResponse(BaseModel):
    scheme_name: str
    scheme_description: str
    devices: List[AIDeviceItem]
    total_price: float
    budget_remaining: float


class ProductMatchRequest(BaseModel):
    category_id: Optional[int] = None
    preferred_brands: Optional[List[int]] = None
    excluded_brands: Optional[List[int]] = None
    budget_min: Optional[float] = None
    budget_max: Optional[float] = None
    limit: int = Field(default=20, ge=1, le=100)


class SchemeDeviceResponse(BaseModel):
    id: int
    product_id: int
    product_name: str
    brand_name: Optional[str] = None
    room_name: str
    quantity: int
    unit_price: float
    subtotal: float
    reason: Optional[str] = None
    image_url: Optional[str] = None
    product_url: Optional[str] = None


class SchemeResponse(BaseModel):
    id: int
    house_id: int
    scheme_name: str
    description: Optional[str]
    total_price: float
    status: int
    created_at: datetime
    devices: List[SchemeDeviceResponse] = Field(default_factory=list)


class SchemeDetailResponse(SchemeResponse):
    house_layout: HouseLayout


class TaskResponse(BaseModel):
    task_id: str
    task_type: str
    status: str
    result: Optional[dict] = None
    error: Optional[str] = None


class ProductMatchItem(BaseModel):
    id: int
    product_name: str
    brand_id: Optional[int] = None
    brand_name: Optional[str] = None
    category_id: Optional[int] = None
    category_name: Optional[str] = None
    price: float
    image_url: Optional[str] = None
    product_url: Optional[str] = None
    match_score: Optional[float] = None


class ProductMatchResponse(BaseModel):
    total: int
    items: List[ProductMatchItem]
