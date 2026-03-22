# 数据模型设计

| 文档版本 | 修改日期   | 修改人 | 修改内容 |
|----------|------------|--------|----------|
| v1.0     | 2026-03-21 | 架构师 | 初稿     |
| v1.1     | 2026-03-21 | 架构师 | 适配PRD v1.1：移除用户表，新增本地存储设计 |

---

## 一、数据存储架构

### 1.1 存储策略概览

根据PRD v1.1，采用**本地优先**的数据存储策略：

| 数据类型 | 存储位置 | 技术选型 | 说明 |
|----------|----------|----------|------|
| 用户业务数据 | 本地设备 | SQLite / Realm | 户型、问卷、偏好、方案 |
| 用户文件数据 | 本地设备 | 文件系统 | 户型图片、缓存 |
| 商品数据 | 服务端 | MySQL | 淘宝商品信息 |
| 商品缓存 | 服务端 | Redis | 热门商品缓存 |

### 1.2 数据流向

```
┌─────────────────────────────────────────────────────────────────┐
│                        数据存储架构                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    客户端 (Flutter APP)                   │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐      │   │
│  │  │ 户型数据    │  │ 问卷偏好    │  │ 方案数据    │      │   │
│  │  │ (SQLite)   │  │ (SQLite)   │  │ (SQLite)   │      │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘      │   │
│  │  ┌─────────────┐                                          │   │
│  │  │ 户型图片    │                                          │   │
│  │  │ (文件系统)  │                                          │   │
│  │  └─────────────┘                                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│                              │ HTTP API                         │
│                              ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    服务端 (FastAPI)                       │   │
│  │  ┌─────────────┐  ┌─────────────┐                        │   │
│  │  │ 商品数据    │  │ 商品缓存    │                        │   │
│  │  │ (MySQL)    │  │ (Redis)    │                        │   │
│  │  └─────────────┘  └─────────────┘                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 二、本地数据模型（SQLite）

### 2.1 本地ER图

```mermaid
erDiagram
    HOUSE_LAYOUT ||--o{ ROOM : contains
    SCHEME ||--o| QUESTIONNAIRE : references
    SCHEME ||--o| PREFERENCE : references
    SCHEME ||--o{ SCHEME_DEVICE : includes
    SCHEME_DEVICE }o--|| PRODUCT_SNAPSHOT : references

    HOUSE_LAYOUT {
        string id PK
        decimal total_area
        string image_path
        datetime created_at
        datetime updated_at
    }

    ROOM {
        string id PK
        string house_id FK
        string room_name
        string room_type
        decimal length
        decimal width
        decimal area
        int sort_order
    }

    QUESTIONNAIRE {
        string id PK
        string living_status
        int resident_count
        boolean has_elderly
        boolean has_children
        boolean has_pets
        text preferred_scenarios
        string sleep_pattern
        string knowledge_level
        datetime created_at
    }

    PREFERENCE {
        string id PK
        decimal budget_min
        decimal budget_max
        text preferred_brands
        text excluded_brands
        datetime created_at
    }

    SCHEME {
        string id PK
        string house_id FK
        string questionnaire_id FK
        string preference_id FK
        string scheme_name
        text description
        decimal total_price
        string status
        datetime created_at
        datetime updated_at
    }

    SCHEME_DEVICE {
        string id PK
        string scheme_id FK
        string product_snapshot_id FK
        string room_name
        int quantity
        decimal unit_price
        decimal subtotal
        text reason
    }

    PRODUCT_SNAPSHOT {
        string id PK
        string product_id
        string product_name
        string brand
        decimal price
        string image_url
        string product_url
        text specs
        datetime snapshot_at
    }
```

### 2.2 本地表结构

#### house_layouts 表（户型）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | TEXT | 是 | 主键，UUID |
| total_area | REAL | 是 | 总面积（平方米） |
| image_path | TEXT | 否 | 户型图本地路径 |
| created_at | TEXT | 是 | 创建时间（ISO8601） |
| updated_at | TEXT | 是 | 更新时间（ISO8601） |

#### rooms 表（房间）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | TEXT | 是 | 主键，UUID |
| house_id | TEXT | 是 | 户型ID，外键 |
| room_name | TEXT | 是 | 房间名称 |
| room_type | TEXT | 是 | 房间类型编码 |
| length | REAL | 是 | 长度（米） |
| width | REAL | 是 | 宽度（米） |
| area | REAL | 是 | 面积（平方米） |
| sort_order | INTEGER | 是 | 排序序号 |

#### questionnaires 表（问卷）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | TEXT | 是 | 主键，UUID |
| living_status | TEXT | 是 | 居住情况：own/rent |
| resident_count | INTEGER | 是 | 常住人数 |
| has_elderly | INTEGER | 是 | 是否有老人（0/1） |
| has_children | INTEGER | 是 | 是否有儿童（0/1） |
| has_pets | INTEGER | 是 | 是否有宠物（0/1） |
| preferred_scenarios | TEXT | 是 | 偏好场景（JSON数组） |
| sleep_pattern | TEXT | 是 | 作息习惯 |
| knowledge_level | TEXT | 是 | 了解程度 |
| created_at | TEXT | 是 | 创建时间 |

#### preferences 表（偏好）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | TEXT | 是 | 主键，UUID |
| budget_min | REAL | 是 | 最小预算 |
| budget_max | REAL | 是 | 最大预算 |
| preferred_brands | TEXT | 否 | 偏好品牌（JSON数组） |
| excluded_brands | TEXT | 否 | 排除品牌（JSON数组） |
| created_at | TEXT | 是 | 创建时间 |

#### schemes 表（方案）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | TEXT | 是 | 主键，UUID |
| house_id | TEXT | 是 | 户型ID |
| questionnaire_id | TEXT | 是 | 问卷ID |
| preference_id | TEXT | 是 | 偏好ID |
| scheme_name | TEXT | 是 | 方案名称 |
| description | TEXT | 是 | 方案描述 |
| total_price | REAL | 是 | 方案总价 |
| status | TEXT | 是 | 状态：pending/success/failed |
| created_at | TEXT | 是 | 创建时间 |
| updated_at | TEXT | 是 | 更新时间 |

#### scheme_devices 表（方案设备）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | TEXT | 是 | 主键，UUID |
| scheme_id | TEXT | 是 | 方案ID |
| product_snapshot_id | TEXT | 是 | 商品快照ID |
| room_name | TEXT | 是 | 放置房间 |
| quantity | INTEGER | 是 | 数量 |
| unit_price | REAL | 是 | 单价 |
| subtotal | REAL | 是 | 小计 |
| reason | TEXT | 否 | 推荐理由 |

#### product_snapshots 表（商品快照）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | TEXT | 是 | 主键，UUID |
| product_id | TEXT | 是 | 淘宝商品ID |
| product_name | TEXT | 是 | 商品名称 |
| brand | TEXT | 否 | 品牌名称 |
| price | REAL | 是 | 价格 |
| image_url | TEXT | 是 | 商品图片URL |
| product_url | TEXT | 是 | 商品详情页URL |
| specs | TEXT | 否 | 商品规格（JSON） |
| snapshot_at | TEXT | 是 | 快照时间 |

---

## 三、服务端数据模型（MySQL）

### 3.1 服务端ER图

```mermaid
erDiagram
    PRODUCT }o--|| CATEGORY : belongs_to
    PRODUCT }o--|| BRAND : manufactured_by

    PRODUCT {
        bigint id PK
        string product_id UK
        string product_name
        bigint brand_id FK
        bigint category_id FK
        decimal price
        decimal original_price
        string image_url
        string product_url
        json specs
        decimal rating
        int sales_count
        string status
        datetime last_synced_at
    }

    BRAND {
        bigint id PK
        string brand_name UK
        string brand_code UK
        string logo_url
        int sort_order
    }

    CATEGORY {
        bigint id PK
        string category_name
        string category_code
        bigint parent_id FK
        int level
        int sort_order
    }
```

### 3.2 服务端表结构

#### products 表（商品）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | BIGINT | 是 | 主键，自增 |
| product_id | VARCHAR(50) | 是 | 淘宝商品ID，唯一索引 |
| product_name | VARCHAR(200) | 是 | 商品名称 |
| brand_id | BIGINT | 否 | 品牌ID，外键 |
| category_id | BIGINT | 是 | 分类ID，外键 |
| price | DECIMAL(10,2) | 是 | 当前价格 |
| original_price | DECIMAL(10,2) | 否 | 原价 |
| image_url | VARCHAR(500) | 是 | 商品图片URL |
| product_url | VARCHAR(500) | 是 | 商品详情页URL |
| specs | JSON | 否 | 商品规格参数 |
| rating | DECIMAL(3,1) | 否 | 评分（0-5） |
| sales_count | INT | 否 | 销量 |
| status | TINYINT | 是 | 状态：1在售 2下架 |
| last_synced_at | DATETIME | 是 | 最后同步时间 |

#### brands 表（品牌）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | BIGINT | 是 | 主键，自增 |
| brand_name | VARCHAR(50) | 是 | 品牌名称，唯一 |
| brand_code | VARCHAR(30) | 是 | 品牌编码，唯一 |
| logo_url | VARCHAR(500) | 否 | 品牌Logo URL |
| sort_order | INT | 是 | 排序序号 |

#### categories 表（分类）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | BIGINT | 是 | 主键，自增 |
| category_name | VARCHAR(50) | 是 | 分类名称 |
| category_code | VARCHAR(30) | 是 | 分类编码 |
| parent_id | BIGINT | 否 | 父分类ID |
| level | TINYINT | 是 | 层级：1一级 2二级 |
| sort_order | INT | 是 | 排序序号 |

---

## 四、数据字典

### 4.1 房间类型枚举

| 编码 | 名称 | 说明 |
|------|------|------|
| living_room | 客厅 | 主要活动空间 |
| master_bedroom | 主卧 | 主卧室 |
| second_bedroom | 次卧 | 次卧室 |
| kitchen | 厨房 | 烹饪空间 |
| bathroom | 卫生间 | 洗漱空间 |
| balcony | 阳台 | 阳台空间 |
| study | 书房 | 工作/学习空间 |
| entrance | 玄关 | 入户区域 |

### 4.2 智能场景枚举

| 编码 | 名称 | 说明 |
|------|------|------|
| lighting | 智能照明 | 灯光控制场景 |
| security | 智能安防 | 安防监控场景 |
| curtain | 智能窗帘 | 窗帘控制场景 |
| appliance | 智能家电 | 家电控制场景 |
| audio | 智能影音 | 影音娱乐场景 |
| environment | 智能环境 | 环境控制场景 |

### 4.3 商品分类体系

| 一级分类 | 二级分类 |
|----------|----------|
| 智能照明 | 智能灯泡、智能灯带、智能开关、智能吸顶灯 |
| 智能安防 | 智能门锁、智能摄像头、门窗传感器、烟雾报警器 |
| 智能窗帘 | 智能窗帘电机、智能卷帘 |
| 智能控制 | 智能插座、智能遥控器、智能音箱 |
| 智能环境 | 空气净化器、智能空调、智能加湿器 |
| 智能厨卫 | 智能马桶、智能热水器、智能洗碗机 |

---

## 五、本地数据管理

### 5.1 数据生命周期

```
┌─────────────────────────────────────────────────────────────────┐
│                      本地数据生命周期                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  创建 ──▶ 使用 ──▶ 归档 ──▶ 清理                                │
│                                                                 │
│  1. 创建：用户输入户型/问卷/偏好，生成方案                        │
│  2. 使用：查看方案详情、购买跳转                                  │
│  3. 归档：方案超过30天自动标记为归档                              │
│  4. 清理：用户手动清除或应用卸载时删除                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 数据导出功能

支持导出方案为以下格式：
- **PDF格式**：包含方案详情、设备列表、价格汇总
- **图片格式**：方案概览截图
- **JSON格式**：完整方案数据（用于备份）

### 5.3 数据同步策略

由于采用本地存储，**无需数据同步**。但需注意：
- 商品价格可能变动，方案中保存的是生成时的价格快照
- 用户可手动刷新商品价格（重新请求服务端）

---

## 六、开发人员职责

> **架构师定义**：以上为数据模型设计，定义了本地和服务端的表结构、字段及数据字典。
> 
> **开发人员职责**：
> 1. **本地数据库**：使用sqflite或drift实现SQLite数据库
> 2. **服务端数据库**：编写建表SQL、设计索引
> 3. **数据迁移**：设计数据库版本升级策略
> 4. **性能优化**：优化查询性能

---

## 七、版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|----------|--------|
| v1.0 | 2026-03-21 | 初稿 | 架构师 |
| v1.1 | 2026-03-21 | 适配PRD v1.1：移除用户表，新增本地存储设计 | 架构师 |
