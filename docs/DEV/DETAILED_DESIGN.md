# 智能家居方案设计APP 详细设计文档

| 版本 | 日期 | 作者 | 状态 |
|------|------|------|------|
| v1.0 | 2026-03-22 | 开发工程师 | 初稿 |

---

## 一、文档说明

### 1.1 文档目的

本文档基于架构文档 [ARCHITECTURE.md](../ARCH/ARCHITECTURE.md)、[API.md](../ARCH/API.md)、[DATABASE.md](../ARCH/DATABASE.md)、[PRD.md](../PRD/PRD.md) 和 [UI_DESIGN.md](../UI/UI_DESIGN.md) 进行微观层面的详细设计。

### 1.2 设计原则

1. **契约优先**：严格遵守架构文档定义的 API 接口
2. **模块自治**：模块内部实现不受外部影响
3. **防御性编程**：处理所有边界情况和异常
4. **极致性能**：针对低端设备优化

---

## 二、系统架构

### 2.1 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 前端 | Flutter 3.x | 跨平台移动应用 |
| 前端本地存储 | SQLite | 本地业务数据存储 |
| 后端 | FastAPI + Python 3.10+ | RESTful API 服务 |
| 数据库 | MySQL 8.0 | 商品数据存储 |
| 缓存 | Redis | 会话缓存/任务队列 |
| AI服务 | DeepSeek API | 方案生成 |
| 外部数据源 | 淘宝开放平台 | 商品数据爬取 |

### 2.2 模块边界

```
┌─────────────────────────────────────────────────────────────┐
│                        Flutter APP                          │
├─────────────────────────────────────────────────────────────┤
│  户型管理模块  │  问卷偏好模块  │  方案展示模块  │  本地数据管理  │
│  (house)      │  (questionnaire)│  (scheme)      │  (settings)   │
└─────────────────────────────────────────────────────────────┘
                              │ HTTP API
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     FastAPI Backend                         │
├─────────────────────────────────────────────────────────────┤
│  AI方案服务(scheme)  │  商品服务(product/brand/category)   │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、后端详细设计

### 3.1 后端模块结构

```
backend/
├── app/
│   ├── __init__.py
│   ├── main.py                    # 应用入口
│   ├── celery_app.py              # Celery配置
│   ├── core/
│   │   ├── config.py              # 配置管理
│   │   ├── database.py            # 数据库连接
│   │   └── dependencies.py        # 依赖注入
│   ├── modules/
│   │   ├── house/                 # 户型模块(前端本地存储，后端仅提供参考)
│   │   ├── product/               # 商品模块
│   │   │   ├── models.py          # Product/Brand/Category模型
│   │   │   ├── routes.py          # 商品/品牌/分类路由
│   │   │   └── schemas.py         # Pydantic schemas
│   │   ├── scheme/                # 方案模块
│   │   │   ├── models.py          # Scheme/Task模型
│   │   │   ├── routes.py          # 方案生成路由
│   │   │   └── schemas.py         # Pydantic schemas
│   │   └── user/                  # 用户模块(保留结构但不使用)
│   └── shared/
│       ├── exceptions.py          # 全局异常处理
│       └── schema.py             # 统一响应格式
├── celery_tasks/
│   ├── __init__.py
│   ├── generation.py              # AI方案生成任务
│   └── crawl.py                   # 商品爬取任务
└── requirements.txt
```

### 3.2 核心模块类图

#### 3.2.1 商品服务 (Product Service)

```
┌─────────────────────────────────────────────────────────────┐
│                     ProductService                           │
├─────────────────────────────────────────────────────────────┤
│  - get_products(filters) → List[Product]                  │
│  - get_product_by_id(id) → Product                         │
│  - match_products(criteria) → List[MatchedProduct]          │
│  - get_brands() → List[Brand]                              │
│  - get_categories() → List[Category]                        │
└─────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│                     ProductRepository                        │
├─────────────────────────────────────────────────────────────┤
│  + find_all(filters: ProductFilter) → List[Product]        │
│  + find_by_id(id: int) → Optional[Product]                 │
│  + find_by_category(category_id) → List[Product]           │
│  + find_by_brand(brand_id) → List[Product]                 │
│  + find_matching(params) → List[Product]                   │
└─────────────────────────────────────────────────────────────┘
```

#### 3.2.2 AI方案服务 (Scheme Service)

```
┌─────────────────────────────────────────────────────────────┐
│                      SchemeService                          │
├─────────────────────────────────────────────────────────────┤
│  - generate_scheme(request) → TaskResult                    │
│  - get_task_status(task_id) → TaskStatus                   │
│  - build_prompt(params) → str                              │
│  - match_products_to_scheme(devices, preferences)           │
└─────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│                    CeleryTaskExecutor                       │
├─────────────────────────────────────────────────────────────┤
│  + submit_generation(params) → task_id                      │
│  + get_result(task_id) → GenerationResult                  │
│  + retry_on_failure(task)                                  │
└─────────────────────────────────────────────────────────────┘
```

### 3.3 核心算法流程

#### 3.3.1 方案生成流程

```
开始
  │
  ▼
┌──────────────────┐
│ 接收请求参数     │
│ (户型+问卷+偏好) │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 构建AI提示词     │
│ (结构化模板)     │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐     ┌─────────────────┐
│ 调用DeepSeek API │────▶│ 超时/失败?      │
└────────┬─────────┘     └────────┬────────┘
         │                        │
         ▼                        ▼
┌──────────────────┐     ┌─────────────────┐
│ 解析AI返回JSON   │     │ 返回错误        │
│ (scheme_data)    │     │ (retry或放弃)   │
└────────┬─────────┘     └─────────────────┘
         │
         ▼
┌──────────────────┐
│ 产品匹配         │
│ (按类型/品牌/预算)│
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 计算总价        │
│ (sum of subtotal)│
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 返回方案结果    │
│ (task_id/result) │
└──────────────────┘
         │
         ▼
       结束
```

#### 3.3.2 产品匹配算法

```
输入: AI生成的设备列表 + 用户偏好

对每个AI设备:
  │
  ▼
┌─────────────────────────────────────┐
│ 1. 按device_type筛选候选商品        │
│    - 设备类型映射到商品分类         │
│    - 例: light → 智能灯泡/灯带/开关  │
└───────────────────┬─────────────────┘
                    │
                    ▼
┌─────────────────────────────────────┐
│ 2. 品牌偏好过滤                     │
│    - 优先: preferred_brands          │
│    - 排除: excluded_brands          │
└───────────────────┬─────────────────┘
                    │
                    ▼
┌─────────────────────────────────────┐
│ 3. 预算约束过滤                     │
│    - 单价 ≤ 剩余预算/数量           │
└───────────────────┬─────────────────┘
                    │
                    ▼
┌─────────────────────────────────────┐
│ 4. 综合排序                         │
│    - 优先级: rating * 0.3 +         │
│             sales_count * 0.2 +     │
│             price_score * 0.5       │
└───────────────────┬─────────────────┘
                    │
                    ▼
┌─────────────────────────────────────┐
│ 5. 选择Top 1商品                    │
│    - 计算小计: price * quantity     │
│    - 快照商品信息                    │
└───────────────────┬─────────────────┘
                    │
                    ▼
输出: 匹配后的商品列表 + 总价
```

### 3.4 后端 API 路由

#### 3.4.1 路由总览

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/health` | 健康检查 | 否 |
| GET | `/` | 根路径 | 否 |
| GET | `/api/v1/houses` | 户型列表(参考) | 否 |
| POST | `/api/v1/houses` | 创建户型(参考) | 否 |
| GET | `/api/v1/products` | 商品列表 | 否 |
| GET | `/api/v1/products/{id}` | 商品详情 | 否 |
| POST | `/api/v1/products/match` | 商品匹配 | 否 |
| GET | `/api/v1/brands` | 品牌列表 | 否 |
| GET | `/api/v1/brands/{id}` | 品牌详情 | 否 |
| GET | `/api/v1/categories` | 分类列表 | 否 |
| POST | `/api/v1/schemes/generate` | 生成方案 | 否 |
| GET | `/api/v1/schemes/tasks/{task_id}` | 方案生成状态 | 否 |

#### 3.4.2 方案生成接口详细设计

**POST `/api/v1/schemes/generate`**

Request:
```json
{
  "house_layout": {
    "total_area": 90.0,
    "rooms": [
      {
        "room_name": "客厅",
        "room_type": "living_room",
        "length": 5.0,
        "width": 4.0,
        "area": 20.0
      }
    ]
  },
  "questionnaire": {
    "living_status": "own",
    "resident_count": 2,
    "has_elderly": false,
    "has_children": false,
    "has_pets": true,
    "preferred_scenarios": ["lighting", "security"],
    "sleep_pattern": "normal",
    "knowledge_level": "basic"
  },
  "preferences": {
    "budget_min": 5000,
    "budget_max": 15000,
    "preferred_brands": ["小米", "Aqara"],
    "excluded_brands": []
  }
}
```

Response:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "task_id": "550e8400-e29b-41d4-a716-446655440000"
  },
  "timestamp": 1710979200000
}
```

**GET `/api/v1/schemes/tasks/{task_id}`**

Response:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "task_id": "550e8400-e29b-41d4-a716-446655440000",
    "status": "completed",
    "result": {
      "scheme_name": "温馨两居智能生活方案",
      "scheme_description": "专为90平米两居室设计...",
      "devices": [
        {
          "device_type": "light",
          "device_name": "米家智能LED灯泡",
          "room": "客厅",
          "quantity": 4,
          "reason": "支持语音控制和APP远程操控",
          "subtotal": 316,
          "brand_name": "小米",
          "price": 79,
          "product_id": 1,
          "image_url": "https://..."
        }
      ],
      "total_price": 8560,
      "budget_remaining": 6440
    }
  },
  "timestamp": 1710979200000
}
```

### 3.5 异常处理策略

| 异常类型 | 处理策略 | 返回码 |
|----------|----------|--------|
| 参数校验失败 | 立即返回 | 10001 |
| 数据库连接失败 | 重试3次后返回 | 10002 |
| AI API超时 | 重试2次后返回 | 20002 |
| 商品未找到 | 返回空列表 | 30001 |
| 预算内无匹配商品 | 返回建议调整 | 30003 |

---

## 四、前端详细设计

### 4.1 前端模块结构

```
frontend/lib/
├── main.dart                     # 应用入口
├── app.dart                      # MaterialApp配置
├── core/
│   ├── constants/
│   │   ├── api_constants.dart    # API端点常量
│   │   └── app_theme.dart        # 主题配置
│   └── services/
│       ├── api_service.dart      # HTTP客户端
│       └── app_state.dart        # 全局状态
├── modules/
│   ├── home/
│   │   └── home_page.dart        # 首页
│   ├── house/
│   │   ├── house_input_page.dart # 户型输入
│   │   └── house_list_page.dart  # 户型列表
│   ├── questionnaire/
│   │   └── questionnaire_page.dart # 问卷页面
│   ├── scheme/
│   │   ├── generating_page.dart  # 生成中页面
│   │   ├── scheme_detail_page.dart # 方案详情
│   │   └── scheme_list_page.dart # 方案列表
│   └── user/
│       └── profile_page.dart     # 个人中心
├── shared/
│   ├── models/
│   │   ├── house.dart            # 户型模型
│   │   ├── questionnaire.dart    # 问卷模型
│   │   ├── scheme.dart           # 方案模型
│   │   └── product.dart          # 商品模型
│   └── widgets/
│       ├── app_button.dart       # 按钮组件
│       ├── app_card.dart         # 卡片组件
│       └── app_input.dart        # 输入框组件
└── utils/
    └── local_storage.dart        # 本地存储工具
```

### 4.2 前端页面流程

```
┌─────────┐    ┌───────────────┐    ┌────────────────┐
│  首页   │───▶│ 户型输入页面 │───▶│ 问卷调查页面   │
└─────────┘    └───────────────┘    └────────────────┘
     │                                      │
     │                                      ▼
     │                            ┌────────────────┐
     │                            │ 偏好设置页面   │
     │                            └────────────────┘
     │                                      │
     ▼                                      ▼
┌───────────────┐                  ┌────────────────┐
│ 历史方案列表  │◀─────────────────│ 生成中页面     │
└───────────────┘                  └────────────────┘
     │                                      │
     ▼                                      ▼
┌───────────────┐                  ┌────────────────┐
│ 方案详情页面  │──────────────────▶│ 返回首页/详情  │
└───────────────┘                  └────────────────┘
```

### 4.3 核心组件设计

#### 4.3.1 首页组件结构

```dart
HomePage
├── _HomePageState
│   ├── Widget build(BuildContext context)
│   │   ├── Scaffold
│   │   │   ├── AppBar
│   │   │   │   └── title: '智能家居方案'
│   │   │   └── body: SingleChildScrollView
│   │   │       ├── _buildHeader() // 欢迎语
│   │   │       ├── _buildDesignEntryCard() // 设计入口
│   │   │       │   ├── 渐变背景
│   │   │       │   ├── _buildStepIndicator() // 4步指示器
│   │   │       │   └── _buildStartButton() // 开始按钮
│   │   │       └── _buildHistorySection() // 历史方案
│   │   └── BottomNavigationBar
│   │       └── 导航项: 首页/方案/我的
```

#### 4.3.2 步骤指示器组件

```dart
Widget _buildStepIndicator() {
  return Row(
    mainAxisAlignment: MainAxisAlignment.spaceAround,
    children: [
      _buildStepItem('①', '户型', _currentStep >= 1),
      _buildStepConnector(_currentStep >= 2),
      _buildStepItem('②', '问卷', _currentStep >= 2),
      _buildStepConnector(_currentStep >= 3),
      _buildStepItem('③', '偏好', _currentStep >= 3),
      _buildStepConnector(_currentStep >= 4),
      _buildStepItem('④', '方案', _currentStep >= 4),
    ],
  );
}
```

### 4.4 状态管理

采用 Provider 模式进行状态管理：

```dart
// 全局状态
class AppState extends ChangeNotifier {
  House? _currentHouse;
  QuestionnaireData? _currentQuestionnaire;
  PreferencesData? _currentPreferences;
  Scheme? _currentScheme;

  // 状态更新方法
  void setHouse(House house) { ... }
  void setQuestionnaire(QuestionnaireData q) { ... }
  void setPreferences(PreferencesData p) { ... }
  void setScheme(Scheme scheme) { ... }
  void clearAll() { ... } // 数据清除
}
```

### 4.5 本地存储设计

使用 sqflite 进行本地数据存储：

```dart
// 数据库表结构
// 1. houses - 户型表
// 2. questionnaires - 问卷表
// 3. preferences - 偏好表
// 4. schemes - 方案表
// 5. scheme_devices - 方案设备表

class LocalStorage {
  static Database? _database;

  Future<Database> get database async {
    _database ??= await _initDatabase();
    return _database!;
  }

  Future<void> saveHouse(House house) async { ... }
  Future<List<House>> getHouses() async { ... }
  Future<void> deleteHouse(int id) async { ... }
  // ... 其他CRUD方法
}
```

### 4.6 API 交互

```dart
class ApiService {
  static const String baseUrl = 'http://8.137.174.58:8000';

  Future<SchemeTaskResult> generateScheme(SchemeRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/schemes/generate'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      return SchemeTaskResult.fromJson(jsonDecode(response.body));
    } else {
      throw ApiException('生成失败: ${response.statusCode}');
    }
  }

  Future<SchemeResult> pollTaskResult(String taskId) async {
    // 轮询方案生成状态
    while (true) {
      final response = await http.get(
        Uri.parse('$baseUrl/api/v1/schemes/tasks/$taskId'),
      );

      final result = TaskResult.fromJson(jsonDecode(response.body));

      if (result.status == 'completed') {
        return result.result;
      } else if (result.status == 'failed') {
        throw ApiException('方案生成失败');
      }

      await Future.delayed(Duration(seconds: 2)); // 2秒轮询间隔
    }
  }
}
```

---

## 五、数据模型

### 5.1 后端数据库模型 (MySQL)

```sql
-- 品牌表
CREATE TABLE brands (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    brand_name VARCHAR(100) NOT NULL,
    brand_code VARCHAR(50) UNIQUE,
    logo_url VARCHAR(500),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 分类表
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL,
    category_code VARCHAR(50) UNIQUE,
    parent_id BIGINT,
    level TINYINT DEFAULT 1,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id VARCHAR(100) UNIQUE NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    brand_id BIGINT,
    category_id BIGINT,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    image_url VARCHAR(500),
    product_url VARCHAR(500),
    specs JSON,
    rating DECIMAL(3,1),
    sales_count INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    last_synced_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (brand_id) REFERENCES brands(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 方案任务表 (Celery)
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id VARCHAR(100) UNIQUE NOT NULL,
    user_id BIGINT,
    task_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    result JSON,
    error TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 5.2 前端本地模型 (SQLite)

```sql
-- 户型表
CREATE TABLE houses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    total_area REAL,
    image_path TEXT,
    created_at TEXT,
    updated_at TEXT
);

-- 房间表
CREATE TABLE rooms (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    house_id INTEGER,
    room_name TEXT,
    room_type TEXT,
    length REAL,
    width REAL,
    area REAL,
    sort_order INTEGER,
    FOREIGN KEY (house_id) REFERENCES houses(id)
);

-- 问卷表
CREATE TABLE questionnaires (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    living_status TEXT,
    resident_count INTEGER,
    has_elderly INTEGER,
    has_children INTEGER,
    has_pets INTEGER,
    preferred_scenarios TEXT,
    sleep_pattern TEXT,
    knowledge_level TEXT,
    created_at TEXT
);

-- 偏好表
CREATE TABLE preferences (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    budget_min REAL,
    budget_max REAL,
    preferred_brands TEXT,
    excluded_brands TEXT
);

-- 方案表
CREATE TABLE schemes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    scheme_name TEXT,
    description TEXT,
    total_price REAL,
    house_id INTEGER,
    questionnaire_id INTEGER,
    preference_id INTEGER,
    created_at TEXT,
    FOREIGN KEY (house_id) REFERENCES houses(id)
);

-- 方案设备表
CREATE TABLE scheme_devices (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    scheme_id INTEGER,
    device_type TEXT,
    device_name TEXT,
    room_name TEXT,
    quantity INTEGER,
    unit_price REAL,
    subtotal REAL,
    reason TEXT,
    product_snapshot TEXT,
    FOREIGN KEY (scheme_id) REFERENCES schemes(id)
);
```

---

## 六、性能优化策略

### 6.1 前端优化

| 优化项 | 实现方式 | 预期效果 |
|--------|----------|----------|
| 代码分割 | 使用 `deferred as` 延迟加载非首屏模块 | 首屏体积减少 30% |
| 图片缓存 | 使用 `cached_network_image` | 减少重复下载 |
| 列表虚拟化 | 使用 `ListView.builder` | 内存占用减少 50% |
| 骨架屏 | 预加载占位动画 | 感知加载时间减少 |
| Web构建优化 | 使用 `--web-renderer html` | 体积减少 20% |

### 6.2 后端优化

| 优化项 | 实现方式 | 预期效果 |
|--------|----------|----------|
| Redis缓存 | 热门商品缓存 1 小时 | 数据库查询减少 80% |
| 数据库索引 | 对 `status`, `category_id`, `brand_id` 建立索引 | 查询速度提升 10x |
| 连接池 | 使用 SQLAlchemy 连接池 | 并发能力提升 5x |
| 异步任务 | Celery 处理 AI 生成 | API响应时间 < 100ms |

---

## 七、测试策略

### 7.1 单元测试

| 模块 | 测试内容 | 覆盖率目标 |
|------|----------|------------|
| 后端 - generation | 提示词构建、产品匹配算法 | 90% |
| 后端 - routes | API 端点测试 | 85% |
| 前端 - models | 模型序列化/反序列化 | 95% |
| 前端 - services | API 交互逻辑 | 80% |

### 7.2 集成测试

- 方案生成完整流程测试
- 商品查询和匹配测试
- 本地存储 CRUD 测试

---

## 八、待确认事项

以下事项需要架构师确认：

1. **商品分类映射**: AI 返回的 device_type (light/speaker/sensor 等) 如何映射到商品分类？
2. **方案生成超时时间**: 当前设置为 30 秒是否合理？
3. **轮询间隔**: 前端轮询方案状态间隔 2 秒是否合适？
4. **本地存储加密**: 本地 SQLite 数据是否需要加密存储？

---

*文档状态：待确认*
*请架构师审核以上设计，确认后冻结文档进行开发。*
