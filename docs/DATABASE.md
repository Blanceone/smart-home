# 数据库设计文档

## 文档信息

| 项目名称 | 智能家居方案定制APP |
|---------|-------------------|
| 文档版本 | V1.2 |
| 创建日期 | 2026-03-14 |
| 更新日期 | 2026-03-17 |
| 架构师 | AI System Architect |
| 文档状态 | 待评审 |

---

## 一、数据库概述

### 1.1 数据库选型

| 项目 | 说明 |
|------|------|
| 数据库类型 | PostgreSQL 15+ |
| 字符集 | UTF-8 |
| 排序规则 | zh_CN.UTF-8 |
| 时区 | UTC+8 (北京时间) |

### 1.2 命名规范

| 项目 | 规范 | 示例 |
|------|------|------|
| 表名 | 小写蛇形命名，复数形式 | `users`, `schemes` |
| 字段名 | 小写蛇形命名 | `created_at`, `user_id` |
| 主键 | `id` | `id` |
| 外键 | `{表名单数}_id` | `user_id`, `scheme_id` |
| 索引 | `idx_{表名}_{字段名}` | `idx_users_device_id` |
| 唯一索引 | `uk_{表名}_{字段名}` | `uk_users_device_id` |

### 1.3 公共字段

所有表都包含以下公共字段：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | UUID | 主键 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

---

## 二、ER 图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                              │
│  ┌──────────────┐       ┌──────────────────┐       ┌──────────────────┐    │
│  │    users     │──1:1──│   user_infos     │       │     devices      │    │
│  │ (跨平台用户)  │       └──────────────────┘       └──────────────────┘    │
│  └──────────────┘               │                          │               │
│         │                       │1                         │               │
│         │1                      │                          │               │
│         │                       │                          │               │
│         ▼1                      ▼1                         │               │
│  ┌──────────────┐       ┌──────────────────┐               │               │
│  │ house_layouts│───1:N─│      rooms       │               │               │
│  └──────────────┘       └──────────────────┘               │               │
│         │                                                   │               │
│         │1                                                  │               │
│         │                                                   │               │
│         ▼N                                                  │               │
│  ┌──────────────┐       ┌──────────────────┐       ┌───────┴──────────┐   │
│  │   schemes    │───1:N─│  scheme_devices  │──N:1──│ scheme_devices   │   │
│  └──────────────┘       └──────────────────┘       └──────────────────┘   │
│         │                                                                  │
│         │1                                                                 │
│         │                                                                  │
│         ▼N                                                                 │
│  ┌──────────────┐                                                          │
│  │  feedbacks   │                                                          │
│  └──────────────┘                                                          │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、数据表设计

### 3.1 用户表 (users)

存储用户基本信息，基于设备标识识别用户（支持跨平台）。

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | UUID | PK | gen_random_uuid() | 主键 |
| device_id | VARCHAR(128) | UNIQUE, NOT NULL | - | 设备唯一标识 (跨平台) |
| platform | VARCHAR(20) | NOT NULL | - | 首次注册平台: android, ios, web |
| nickname | VARCHAR(100) | NOT NULL | '用户' | 用户昵称 |
| avatar | VARCHAR(500) | | NULL | 头像URL |
| device_model | VARCHAR(100) | | NULL | 设备型号 |
| os_version | VARCHAR(50) | | NULL | 操作系统版本 |
| status | SMALLINT | NOT NULL | 1 | 状态: 0-禁用, 1-正常 |
| last_active_at | TIMESTAMP | | NULL | 最后活跃时间 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 更新时间 |

**设备标识说明**：

| 平台 | device_id 来源 | 说明 |
|------|---------------|------|
| Android | androidInfo.id | Android 8.0+ 的唯一设备标识 |
| iOS | identifierForVendor | 同一开发商应用共享的标识 |
| Web | Browser Fingerprint | 基于User-Agent + Platform生成的指纹 |

**索引**：
- `uk_users_device_id` UNIQUE (device_id)
- `idx_users_platform` (platform)
- `idx_users_status` (status)
- `idx_users_created_at` (created_at)

**SQL**：
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id VARCHAR(128) UNIQUE NOT NULL,
    platform VARCHAR(20) NOT NULL,
    nickname VARCHAR(100) NOT NULL DEFAULT '用户',
    avatar VARCHAR(500),
    device_model VARCHAR(100),
    os_version VARCHAR(50),
    status SMALLINT NOT NULL DEFAULT 1,
    last_active_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_users_device_id ON users(device_id);
CREATE INDEX idx_users_platform ON users(platform);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);
```

---

### 3.2 用户信息表 (user_infos)

存储用户填写的详细信息（基础信息、生活习惯、设备经验、审美偏好、品牌偏好）。

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | UUID | PK | gen_random_uuid() | 主键 |
| user_id | UUID | FK, UNIQUE, NOT NULL | - | 用户ID |
| age | VARCHAR(20) | | NULL | 年龄段 |
| occupation | VARCHAR(50) | | NULL | 职业 |
| family_members | JSONB | | NULL | 家庭成员数组 |
| city | VARCHAR(100) | | NULL | 居住城市 |
| sleep_pattern | VARCHAR(50) | | NULL | 作息时间 |
| home_activities | JSONB | | NULL | 在家活动偏好数组 |
| entertainment_habits | JSONB | | NULL | 娱乐习惯数组 |
| device_knowledge_level | VARCHAR(50) | | NULL | 智能设备了解程度 |
| used_devices | JSONB | | NULL | 使用过的设备数组 |
| decor_style | VARCHAR(50) | | NULL | 装修风格偏好 |
| color_preferences | JSONB | | NULL | 颜色偏好数组 |
| preferred_brands | JSONB | | NULL | 品牌偏好数组 |
| is_completed | BOOLEAN | NOT NULL | FALSE | 信息是否填写完整 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- `uk_user_infos_user_id` UNIQUE (user_id)
- `idx_user_infos_is_completed` (is_completed)

**SQL**：
```sql
CREATE TABLE user_infos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    age VARCHAR(20),
    occupation VARCHAR(50),
    family_members JSONB,
    city VARCHAR(100),
    sleep_pattern VARCHAR(50),
    home_activities JSONB,
    entertainment_habits JSONB,
    device_knowledge_level VARCHAR(50),
    used_devices JSONB,
    decor_style VARCHAR(50),
    color_preferences JSONB,
    preferred_brands JSONB,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_user_infos_user_id ON user_infos(user_id);
CREATE INDEX idx_user_infos_is_completed ON user_infos(is_completed);
```

---

### 3.3 户型表 (house_layouts)

存储用户的户型信息。

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | UUID | PK | gen_random_uuid() | 主键 |
| user_id | UUID | FK, UNIQUE, NOT NULL | - | 用户ID |
| house_type | VARCHAR(50) | NOT NULL | - | 房屋类型 |
| total_area | DECIMAL(10,2) | NOT NULL | - | 建筑面积(平方米) |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- `uk_house_layouts_user_id` UNIQUE (user_id)
- `idx_house_layouts_house_type` (house_type)

**SQL**：
```sql
CREATE TABLE house_layouts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    house_type VARCHAR(50) NOT NULL,
    total_area DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_house_layouts_user_id ON house_layouts(user_id);
CREATE INDEX idx_house_layouts_house_type ON house_layouts(house_type);
```

---

### 3.4 房间表 (rooms)

存储户型中的房间信息。

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | UUID | PK | gen_random_uuid() | 主键 |
| layout_id | UUID | FK, NOT NULL | - | 户型ID |
| name | VARCHAR(50) | NOT NULL | - | 房间名称 |
| area | DECIMAL(10,2) | NOT NULL | - | 房间面积(平方米) |
| special_needs | TEXT | | NULL | 特殊需求 |
| sort_order | SMALLINT | NOT NULL | 0 | 排序序号 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- `idx_rooms_layout_id` (layout_id)
- `idx_rooms_name` (name)

**SQL**：
```sql
CREATE TABLE rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    layout_id UUID NOT NULL REFERENCES house_layouts(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    area DECIMAL(10,2) NOT NULL,
    special_needs TEXT,
    sort_order SMALLINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rooms_layout_id ON rooms(layout_id);
CREATE INDEX idx_rooms_name ON rooms(name);
```

---

### 3.5 方案表 (schemes)

存储用户生成的智能家居方案。

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | UUID | PK | gen_random_uuid() | 主键 |
| user_id | UUID | FK, NOT NULL | - | 用户ID |
| name | VARCHAR(200) | NOT NULL | - | 方案名称 |
| budget | DECIMAL(12,2) | NOT NULL | - | 预算金额(元) |
| total_price | DECIMAL(12,2) | NOT NULL | 0 | 设备总价(元) |
| status | VARCHAR(20) | NOT NULL | 'draft' | 状态: draft-草稿, saved-已保存 |
| decoration_guide | JSONB | | NULL | 装修说明(JSON) |
| is_saved | BOOLEAN | NOT NULL | FALSE | 是否已保存 |
| saved_at | TIMESTAMP | | NULL | 保存时间 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 更新时间 |

**decoration_guide JSON 结构**：
```json
{
  "summary": "整体布局建议...",
  "rooms": [
    {
      "name": "客厅",
      "layout": "建议在客厅入口处安装智能开关...",
      "devices": ["智能吸顶灯", "智能窗帘", "智能音箱"],
      "installationPoints": [
        "智能开关安装在入口墙面，高度1.3米"
      ],
      "notes": "注意预留电源插座位置"
    }
  ],
  "professionalAdvice": "建议优先考虑设备兼容性..."
}
```

**索引**：
- `idx_schemes_user_id` (user_id)
- `idx_schemes_status` (status)
- `idx_schemes_is_saved` (is_saved)
- `idx_schemes_created_at` (created_at)

**SQL**：
```sql
CREATE TABLE schemes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    budget DECIMAL(12,2) NOT NULL,
    total_price DECIMAL(12,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    decoration_guide JSONB,
    is_saved BOOLEAN NOT NULL DEFAULT FALSE,
    saved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_schemes_user_id ON schemes(user_id);
CREATE INDEX idx_schemes_status ON schemes(status);
CREATE INDEX idx_schemes_is_saved ON schemes(is_saved);
CREATE INDEX idx_schemes_created_at ON schemes(created_at);
```

---

### 3.6 设备库表 (devices)

存储智能家居设备的基础信息。

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | UUID | PK | gen_random_uuid() | 主键 |
| name | VARCHAR(200) | NOT NULL | - | 设备名称 |
| brand | VARCHAR(100) | NOT NULL | - | 品牌 |
| category | VARCHAR(50) | NOT NULL | - | 分类 |
| price | DECIMAL(10,2) | NOT NULL | - | 价格(元) |
| original_price | DECIMAL(10,2) | | NULL | 原价(元) |
| description | TEXT | | NULL | 功能描述 |
| features | JSONB | | NULL | 功能特性数组 |
| specifications | JSONB | | NULL | 技术参数(JSON) |
| applicable_scenes | JSONB | | NULL | 适用场景数组 |
| image_url | VARCHAR(500) | | NULL | 主图URL |
| images | JSONB | | NULL | 图片URL数组 |
| taobao_item_id | VARCHAR(100) | | NULL | 淘宝商品ID |
| taobao_url | VARCHAR(500) | | NULL | 淘宝商品链接 |
| status | SMALLINT | NOT NULL | 1 | 状态: 0-下架, 1-上架 |
| price_updated_at | TIMESTAMP | | NULL | 价格更新时间 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- `idx_devices_brand` (brand)
- `idx_devices_category` (category)
- `idx_devices_status` (status)
- `idx_devices_price` (price)
- `idx_devices_name` (name)

**SQL**：
```sql
CREATE TABLE devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    description TEXT,
    features JSONB,
    specifications JSONB,
    applicable_scenes JSONB,
    image_url VARCHAR(500),
    images JSONB,
    taobao_item_id VARCHAR(100),
    taobao_url VARCHAR(500),
    status SMALLINT NOT NULL DEFAULT 1,
    price_updated_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_devices_brand ON devices(brand);
CREATE INDEX idx_devices_category ON devices(category);
CREATE INDEX idx_devices_status ON devices(status);
CREATE INDEX idx_devices_price ON devices(price);
CREATE INDEX idx_devices_name ON devices(name);
```

---

### 3.7 方案设备关联表 (scheme_devices)

存储方案中推荐的设备信息。

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | UUID | PK | gen_random_uuid() | 主键 |
| scheme_id | UUID | FK, NOT NULL | - | 方案ID |
| device_id | UUID | FK, NOT NULL | - | 设备ID |
| room_name | VARCHAR(50) | | NULL | 所属房间 |
| quantity | SMALLINT | NOT NULL | 1 | 数量 |
| price | DECIMAL(10,2) | NOT NULL | - | 单价(元) |
| recommend_reason | TEXT | | NULL | 推荐理由 |
| sort_order | SMALLINT | NOT NULL | 0 | 排序序号 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |

**索引**：
- `idx_scheme_devices_scheme_id` (scheme_id)
- `idx_scheme_devices_device_id` (device_id)
- `uk_scheme_devices_scheme_device` UNIQUE (scheme_id, device_id, room_name)

**SQL**：
```sql
CREATE TABLE scheme_devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scheme_id UUID NOT NULL REFERENCES schemes(id) ON DELETE CASCADE,
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE RESTRICT,
    room_name VARCHAR(50),
    quantity SMALLINT NOT NULL DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    recommend_reason TEXT,
    sort_order SMALLINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_scheme_devices_scheme_id ON scheme_devices(scheme_id);
CREATE INDEX idx_scheme_devices_device_id ON scheme_devices(device_id);
CREATE UNIQUE INDEX uk_scheme_devices_scheme_device ON scheme_devices(scheme_id, device_id, room_name);
```

---

### 3.8 反馈表 (feedbacks)

存储用户提交的各类反馈。

| 字段名 | 类型 | 约束 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | UUID | PK | gen_random_uuid() | 主键 |
| user_id | UUID | FK, NOT NULL | - | 用户ID |
| type | VARCHAR(50) | NOT NULL | - | 反馈类型 |
| content | TEXT | NOT NULL | - | 反馈内容 |
| related_id | UUID | | NULL | 关联ID(方案ID/设备ID) |
| rating | SMALLINT | | NULL | 评分(1-5) |
| contact | VARCHAR(200) | | NULL | 联系方式 |
| status | VARCHAR(20) | NOT NULL | 'pending' | 状态: pending-待处理, processed-已处理 |
| reply | TEXT | | NULL | 回复内容 |
| replied_at | TIMESTAMP | | NULL | 回复时间 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 更新时间 |

**反馈类型说明**：
- `scheme_rating` - 方案评价
- `suggestion` - 意见反馈
- `data_correction` - 数据纠错

**索引**：
- `idx_feedbacks_user_id` (user_id)
- `idx_feedbacks_type` (type)
- `idx_feedbacks_status` (status)
- `idx_feedbacks_created_at` (created_at)

**SQL**：
```sql
CREATE TABLE feedbacks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    related_id UUID,
    rating SMALLINT,
    contact VARCHAR(200),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    reply TEXT,
    replied_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_feedbacks_user_id ON feedbacks(user_id);
CREATE INDEX idx_feedbacks_type ON feedbacks(type);
CREATE INDEX idx_feedbacks_status ON feedbacks(status);
CREATE INDEX idx_feedbacks_created_at ON feedbacks(created_at);
```

---

## 四、数据字典

### 4.1 用户平台 (users.platform)

| 值 | 说明 |
|----|------|
| android | Android平台 |
| ios | iOS平台 |
| web | Web平台 |

### 4.2 用户状态 (users.status)

| 值 | 说明 |
|----|------|
| 0 | 禁用 |
| 1 | 正常 |

### 4.3 方案状态 (schemes.status)

| 值 | 说明 |
|----|------|
| draft | 草稿 |
| saved | 已保存 |

### 4.4 设备状态 (devices.status)

| 值 | 说明 |
|----|------|
| 0 | 下架 |
| 1 | 上架 |

### 4.5 反馈类型 (feedbacks.type)

| 值 | 说明 |
|----|------|
| scheme_rating | 方案评价 |
| suggestion | 意见反馈 |
| data_correction | 数据纠错 |

### 4.6 反馈状态 (feedbacks.status)

| 值 | 说明 |
|----|------|
| pending | 待处理 |
| processed | 已处理 |

### 4.7 年龄段选项 (user_infos.age)

| 值 | 说明 |
|----|------|
| 18-25 | 18-25岁 |
| 26-30 | 26-30岁 |
| 31-35 | 31-35岁 |
| 35+ | 35岁以上 |

### 4.8 房屋类型选项 (house_layouts.house_type)

| 值 | 说明 |
|----|------|
| 一居室 | 一居室 |
| 两居室 | 两居室 |
| 三居室 | 三居室 |
| 四居室及以上 | 四居室及以上 |
| 复式/别墅 | 复式或别墅 |

### 4.9 房间名称选项 (rooms.name)

| 值 | 说明 |
|----|------|
| 客厅 | 客厅 |
| 主卧 | 主卧 |
| 次卧 | 次卧 |
| 书房 | 书房 |
| 厨房 | 厨房 |
| 卫生间 | 卫生间 |
| 阳台 | 阳台 |
| 其他 | 其他房间 |

---

## 五、数据库约束

### 5.1 外键约束

| 子表 | 外键字段 | 父表 | 删除规则 |
|------|----------|------|----------|
| user_infos | user_id | users | CASCADE |
| house_layouts | user_id | users | CASCADE |
| rooms | layout_id | house_layouts | CASCADE |
| schemes | user_id | users | CASCADE |
| scheme_devices | scheme_id | schemes | CASCADE |
| scheme_devices | device_id | devices | RESTRICT |
| feedbacks | user_id | users | CASCADE |

### 5.2 业务约束

| 约束名称 | 说明 |
|---------|------|
| 用户方案数量限制 | 每个用户最多保存3个方案 |
| 设备价格非负 | 设备价格必须大于等于0 |
| 预算非负 | 预算金额必须大于0 |
| 评分范围 | 评分必须在1-5之间 |

---

## 六、数据库迁移脚本

### 6.1 V1.1 到 V1.2 迁移脚本

```sql
-- 添加平台字段
ALTER TABLE users ADD COLUMN platform VARCHAR(20) NOT NULL DEFAULT 'android';
ALTER TABLE users ADD COLUMN device_model VARCHAR(100);
ALTER TABLE users ADD COLUMN os_version VARCHAR(50);

-- 扩展device_id长度以支持Web指纹
ALTER TABLE users ALTER COLUMN device_id TYPE VARCHAR(128);

-- 添加平台索引
CREATE INDEX idx_users_platform ON users(platform);

-- 更新注释
COMMENT ON COLUMN users.device_id IS '设备唯一标识 (跨平台: Android ID / iOS identifierForVendor / Web Fingerprint)';
COMMENT ON COLUMN users.platform IS '首次注册平台: android, ios, web';
```

---

## 七、Prisma Schema 定义

```prisma
generator client {
  provider = "prisma-client-js"
}

datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}

model User {
  id              String        @id @default(uuid())
  deviceId        String        @unique @map("device_id") @db.VarChar(128)
  platform        String        @db.VarChar(20)
  nickname        String        @default("用户") @db.VarChar(100)
  avatar          String?       @db.VarChar(500)
  deviceModel     String?       @map("device_model") @db.VarChar(100)
  osVersion       String?       @map("os_version") @db.VarChar(50)
  status          Int           @default(1) @db.SmallInt
  lastActiveAt    DateTime?     @map("last_active_at")
  createdAt       DateTime      @default(now()) @map("created_at")
  updatedAt       DateTime      @updatedAt @map("updated_at")
  
  userInfo        UserInfo?
  houseLayout     HouseLayout?
  schemes         Scheme[]
  feedbacks       Feedback[]
  
  @@map("users")
}

model UserInfo {
  id                      String    @id @default(uuid())
  userId                  String    @unique @map("user_id")
  age                     String?   @db.VarChar(20)
  occupation              String?   @db.VarChar(50)
  familyMembers           Json?     @map("family_members")
  city                    String?   @db.VarChar(100)
  sleepPattern            String?   @map("sleep_pattern") @db.VarChar(50)
  homeActivities          Json?     @map("home_activities")
  entertainmentHabits     Json?     @map("entertainment_habits")
  deviceKnowledgeLevel    String?   @map("device_knowledge_level") @db.VarChar(50)
  usedDevices             Json?     @map("used_devices")
  decorStyle              String?   @map("decor_style") @db.VarChar(50)
  colorPreferences        Json?     @map("color_preferences")
  preferredBrands         Json?     @map("preferred_brands")
  isCompleted             Boolean   @default(false) @map("is_completed")
  createdAt               DateTime  @default(now()) @map("created_at")
  updatedAt               DateTime  @updatedAt @map("updated_at")
  
  user                    User      @relation(fields: [userId], references: [id], onDelete: Cascade)
  
  @@map("user_infos")
}

model HouseLayout {
  id              String    @id @default(uuid())
  userId          String    @unique @map("user_id")
  houseType       String    @map("house_type") @db.VarChar(50)
  totalArea       Decimal   @map("total_area") @db.Decimal(10, 2)
  createdAt       DateTime  @default(now()) @map("created_at")
  updatedAt       DateTime  @updatedAt @map("updated_at")
  
  user            User      @relation(fields: [userId], references: [id], onDelete: Cascade)
  rooms           Room[]
  
  @@map("house_layouts")
}

model Room {
  id              String      @id @default(uuid())
  layoutId        String      @map("layout_id")
  name            String      @db.VarChar(50)
  area            Decimal     @db.Decimal(10, 2)
  specialNeeds    String?     @map("special_needs") @db.Text
  sortOrder       Int         @default(0) @map("sort_order") @db.SmallInt
  createdAt       DateTime    @default(now()) @map("created_at")
  updatedAt       DateTime    @updatedAt @map("updated_at")
  
  houseLayout     HouseLayout @relation(fields: [layoutId], references: [id], onDelete: Cascade)
  
  @@map("rooms")
}

model Scheme {
  id                String          @id @default(uuid())
  userId            String          @map("user_id")
  name              String          @db.VarChar(200)
  budget            Decimal         @db.Decimal(12, 2)
  totalPrice        Decimal         @default(0) @map("total_price") @db.Decimal(12, 2)
  status            String          @default("draft") @db.VarChar(20)
  decorationGuide   Json?           @map("decoration_guide")
  isSaved           Boolean         @default(false) @map("is_saved")
  savedAt           DateTime?       @map("saved_at")
  createdAt         DateTime        @default(now()) @map("created_at")
  updatedAt         DateTime        @updatedAt @map("updated_at")
  
  user              User            @relation(fields: [userId], references: [id], onDelete: Cascade)
  schemeDevices     SchemeDevice[]
  
  @@map("schemes")
}

model Device {
  id                String      @id @default(uuid())
  name              String      @db.VarChar(200)
  brand             String      @db.VarChar(100)
  category          String      @db.VarChar(50)
  price             Decimal     @db.Decimal(10, 2)
  originalPrice     Decimal?    @map("original_price") @db.Decimal(10, 2)
  description       String?     @db.Text
  features          Json?
  specifications    Json?
  applicableScenes  Json?       @map("applicable_scenes")
  imageUrl          String?     @map("image_url") @db.VarChar(500)
  images            Json?
  taobaoItemId      String?     @map("taobao_item_id") @db.VarChar(100)
  taobaoUrl         String?     @map("taobao_url") @db.VarChar(500)
  status            Int         @default(1) @db.SmallInt
  priceUpdatedAt    DateTime?   @map("price_updated_at")
  createdAt         DateTime    @default(now()) @map("created_at")
  updatedAt         DateTime    @updatedAt @map("updated_at")
  
  schemeDevices     SchemeDevice[]
  
  @@map("devices")
}

model SchemeDevice {
  id              String    @id @default(uuid())
  schemeId        String    @map("scheme_id")
  deviceId        String    @map("device_id")
  roomName        String?   @map("room_name") @db.VarChar(50)
  quantity        Int       @default(1) @db.SmallInt
  price           Decimal   @db.Decimal(10, 2)
  recommendReason String?   @map("recommend_reason") @db.Text
  sortOrder       Int       @default(0) @map("sort_order") @db.SmallInt
  createdAt       DateTime  @default(now()) @map("created_at")
  
  scheme          Scheme    @relation(fields: [schemeId], references: [id], onDelete: Cascade)
  device          Device    @relation(fields: [deviceId], references: [id], onDelete: Restrict)
  
  @@unique([schemeId, deviceId, roomName])
  @@map("scheme_devices")
}

model Feedback {
  id          String    @id @default(uuid())
  userId      String    @map("user_id")
  type        String    @db.VarChar(50)
  content     String    @db.Text
  relatedId   String?   @map("related_id")
  rating      Int?      @db.SmallInt
  contact     String?   @db.VarChar(200)
  status      String    @default("pending") @db.VarChar(20)
  reply       String?   @db.Text
  repliedAt   DateTime? @map("replied_at")
  createdAt   DateTime  @default(now()) @map("created_at")
  updatedAt   DateTime  @updatedAt @map("updated_at")
  
  user        User      @relation(fields: [userId], references: [id], onDelete: Cascade)
  
  @@map("feedbacks")
}
```

---

## 八、变更日志

| 版本 | 日期 | 变更内容 |
|------|------|---------|
| V1.2 | 2026-03-17 | 增加跨平台支持，用户表新增platform、device_model、os_version字段，扩展device_id长度 |
| V1.1 | 2026-03-15 | 移除微信登录相关字段，改用device_id认证 |
| V1.0 | 2026-03-14 | 初始版本 |
