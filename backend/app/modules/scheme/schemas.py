from pydantic import BaseModel, Field, ConfigDict, AliasChoices
from typing import Optional, List
from decimal import Decimal
from datetime import datetime


class HouseLayoutRoom(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    room_name: str = Field(..., validation_alias=AliasChoices('room_name', 'roomName'))
    room_type: str = Field(..., validation_alias=AliasChoices('room_type', 'roomType'))
    length: Optional[float] = Field(default=None, validation_alias=AliasChoices('length', 'length'))
    width: Optional[float] = Field(default=None, validation_alias=AliasChoices('width', 'width'))
    area: Optional[float] = Field(default=None, validation_alias=AliasChoices('area', 'area'))


class HouseLayout(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    total_area: Optional[float] = Field(default=None, validation_alias=AliasChoices('total_area', 'totalArea'))
    rooms: List[HouseLayoutRoom] = Field(default_factory=list, min_length=1, validation_alias=AliasChoices('rooms', 'rooms'))


class QuestionnaireData(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    living_status: str = Field(..., validation_alias=AliasChoices('living_status', 'livingStatus'))
    resident_count: int = Field(default=1, ge=1, validation_alias=AliasChoices('resident_count', 'residentCount'))
    has_elderly: bool = Field(default=False, validation_alias=AliasChoices('has_elderly', 'hasElderly'))
    has_children: bool = Field(default=False, validation_alias=AliasChoices('has_children', 'hasChildren'))
    has_pets: bool = Field(default=False, validation_alias=AliasChoices('has_pets', 'hasPets'))
    preferred_scenarios: List[str] = Field(default_factory=list, validation_alias=AliasChoices('preferred_scenarios', 'preferredScenarios'))
    sleep_pattern: Optional[str] = Field(default=None, validation_alias=AliasChoices('sleep_pattern', 'sleepPattern'))
    knowledge_level: Optional[str] = Field(default=None, validation_alias=AliasChoices('knowledge_level', 'knowledgeLevel'))


class PreferencesData(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    budget_min: float = Field(default=0, ge=0, validation_alias=AliasChoices('budget_min', 'budgetMin'))
    budget_max: float = Field(default=100000, ge=0, validation_alias=AliasChoices('budget_max', 'budgetMax'))
    preferred_brands: List[str] = Field(default_factory=list, validation_alias=AliasChoices('preferred_brands', 'preferredBrands'))
    excluded_brands: List[str] = Field(default_factory=list, validation_alias=AliasChoices('excluded_brands', 'excludedBrands'))


class SchemeGenerateRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    house_layout: HouseLayout = Field(..., validation_alias=AliasChoices('house_layout', 'houseLayout'))
    questionnaire: QuestionnaireData = Field(..., validation_alias=AliasChoices('questionnaire', 'questionnaire'))
    preferences: PreferencesData = Field(..., validation_alias=AliasChoices('preferences', 'preferences'))


class AIDeviceItem(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    device_type: str = Field(..., validation_alias=AliasChoices('device_type', 'deviceType'))
    device_name: str = Field(..., validation_alias=AliasChoices('device_name', 'deviceName'))
    room: str
    quantity: int
    reason: str
    matched_product: Optional[dict] = Field(default=None, validation_alias=AliasChoices('matched_product', 'matchedProduct'))
    subtotal: float


class AISchemeResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    scheme_name: str = Field(..., validation_alias=AliasChoices('scheme_name', 'schemeName'))
    scheme_description: str = Field(..., validation_alias=AliasChoices('scheme_description', 'schemeDescription'))
    devices: List[AIDeviceItem]
    total_price: float = Field(..., validation_alias=AliasChoices('total_price', 'totalPrice'))
    budget_remaining: float = Field(..., validation_alias=AliasChoices('budget_remaining', 'budgetRemaining'))


class ProductMatchRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    category_id: Optional[int] = Field(default=None, validation_alias=AliasChoices('category_id', 'categoryId'))
    preferred_brands: Optional[List[int]] = None
    excluded_brands: Optional[List[int]] = None
    budget_min: Optional[float] = None
    budget_max: Optional[float] = None
    limit: int = Field(default=20, ge=1, le=100)


class SchemeDeviceResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    id: int
    product_id: int = Field(..., validation_alias=AliasChoices('product_id', 'productId'))
    product_name: str = Field(..., validation_alias=AliasChoices('product_name', 'productName'))
    brand_name: Optional[str] = None
    room_name: str = Field(..., validation_alias=AliasChoices('room_name', 'roomName'))
    quantity: int
    unit_price: float = Field(..., validation_alias=AliasChoices('unit_price', 'unitPrice'))
    subtotal: float
    reason: Optional[str] = None
