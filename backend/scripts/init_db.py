from sqlalchemy import create_engine, text
from app.core.config import settings
from app.core.database import Base, engine
from app.modules.user.models import User
from app.modules.house.models import House, Room
from app.modules.product.models import Brand, Category, Product
from app.modules.scheme.models import Scheme, SchemeDevice, UserPreference, Questionnaire, Task

def init_database():
    Base.metadata.create_all(bind=engine)
    print("Database tables created successfully!")

    with engine.connect() as conn:
        result = conn.execute(text("SHOW TABLES"))
        tables = [row[0] for row in result]
        print(f"Existing tables: {tables}")

def seed_brands():
    brands_data = [
        {"brand_name": "小米", "brand_code": "xiaomi", "sort_order": 1},
        {"brand_name": "华为", "brand_code": "huawei", "sort_order": 2},
        {"brand_name": "涂鸦智能", "brand_code": "tuya", "sort_order": 3},
        {"brand_name": "Aqara", "brand_code": "aqara", "sort_order": 4},
        {"brand_name": "欧瑞博", "brand_code": "orvibo", "sort_order": 5},
        {"brand_name": "海尔智家", "brand_code": "haier", "sort_order": 6},
        {"brand_name": "美的", "brand_code": "midea", "sort_order": 7},
    ]

    from sqlalchemy.orm import Session
    with Session(engine) as session:
        for brand_data in brands_data:
            existing = session.query(Brand).filter(Brand.brand_code == brand_data["brand_code"]).first()
            if not existing:
                brand = Brand(**brand_data)
                session.add(brand)
        session.commit()
        print("Brands seeded successfully!")

def seed_categories():
    categories_data = [
        {"category_name": "智能照明", "category_code": "lighting", "level": 1, "sort_order": 1},
        {"category_name": "智能安防", "category_code": "security", "level": 1, "sort_order": 2},
        {"category_name": "智能窗帘", "category_code": "curtain", "level": 1, "sort_order": 3},
        {"category_name": "智能控制", "category_code": "control", "level": 1, "sort_order": 4},
        {"category_name": "智能环境", "category_code": "environment", "level": 1, "sort_order": 5},
        {"category_name": "智能厨卫", "category_code": "kitchen", "level": 1, "sort_order": 6},
    ]

    from sqlalchemy.orm import Session
    with Session(engine) as session:
        for cat_data in categories_data:
            existing = session.query(Category).filter(Category.category_code == cat_data["category_code"]).first()
            if not existing:
                category = Category(**cat_data)
                session.add(category)
        session.commit()
        print("Categories seeded successfully!")

if __name__ == "__main__":
    init_database()
    seed_brands()
    seed_categories()
    print("Database initialization complete!")
