# 智能家居方案设计APP 详细设计文档

| 文档版本 | 修改日期   | 修改人 | 修改内容 |
|----------|------------|--------|----------|
| v1.0     | 2026-03-24 | 开发 | 适配PRD v1.2 / ARCH v1.2：新增日志管理模块 |

---

## 一、文档说明

### 1.1 目的

本详细设计文档在架构文档定义的模块边界内，输出模块内部逻辑设计、类图、算法流程和异常处理策略。

### 1.2 架构约束

- **API接口约束**：严格遵循 `ARCH/API.md` 定义的接口规范
- **模块边界约束**：不得跨越架构定义的模块边界调用内部实现
- **数据存储约束**：本地数据使用SQLite，遵循 `ARCH/DATABASE.md` 定义的结构

### 1.3 相关文档

| 文档 | 路径 |
|------|------|
| 产品需求文档 | `../PRD/PRD.md` |
| 系统架构文档 | `../ARCH/ARCHITECTURE.md` |
| API规范 | `../ARCH/API.md` |
| 数据模型 | `../ARCH/DATABASE.md` |
| UI设计文档 | `../UI/UI_DESIGN.md` |

---

## 二、技术栈

### 2.1 前端

| 项目 | 技术 | 版本 |
|------|------|------|
| 框架 | Flutter | 3.24.0 |
| 状态管理 | Provider | - |
| 本地数据库 | sqflite | - |
| 日志库 | logger | - |
| HTTP客户端 | http | - |

### 2.2 后端

| 项目 | 技术 | 版本 |
|------|------|------|
| 框架 | FastAPI | 0.100+ |
| Python | Python | 3.11+ |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.0 |
| 异步任务 | Celery | 5.x |
| AI服务 | DeepSeek API | - |

---

## 三、前端模块详细设计

### 3.1 日志管理模块

#### 3.1.1 模块职责

根据 PRD v1.2 和 UI_DESIGN.md，日志管理模块负责：
1. 自动记录APP运行日志（ERROR、WARN、INFO、DEBUG级别）
2. 用户手动上传日志到服务器
3. 本地日志保留策略（7天/5MB限制）
4. 上传进度和结果展示

#### 3.1.2 内部类设计

```
lib/
├── core/
│   └── services/
│       └── logger_service.dart      # 日志服务（核心）
├── modules/
│   ├── user/
│   │   └── profile_page.dart       # 日志上传入口（已存在）
│   └── ...
└── shared/
    └── models/
        └── log_entry.dart          # 日志条目模型
```

**类图：**

```
┌─────────────────────────────────────┐
│           LoggerService             │
├─────────────────────────────────────┤
│ - _logBuffer: List<LogEntry>       │
│ - _logFile: File                   │
│ - _deviceId: String                │
├─────────────────────────────────────┤
│ + init(): Future<void>             │
│ + log(level, message, context): void │
│ + error(message, context): void    │
│ + warn(message, context): void     │
│ + info(message, context): void     │
│ + debug(message, context): void    │
│ + getLogs([days]): Future<List<LogEntry>> │
│ + getLogFiles(): Future<List<File>> │
│ + cleanOldLogs(): Future<void>      │
│ + uploadLogs(progress): Future<bool> │
│ - _writeToFile(entry): Future<void> │
│ - _rotateLogFile(): Future<void>    │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│            LogEntry                │
├─────────────────────────────────────┤
│ - timestamp: DateTime              │
│ - level: LogLevel                  │
│ - message: String                  │
│ - deviceId: String                  │
│ - appVersion: String               │
│ - platform: String                 │
│ - osVersion: String                │
│ - apiEndpoint: String?             │
│ - errorCode: String?              │
│ - stackTrace: String?             │
└─────────────────────────────────────┘

enum LogLevel { debug, info, warn, error }
```

#### 3.1.3 算法流程

**日志记录流程：**
```
log(message, context)
  │
  ├─► 创建 LogEntry(timestamp=now, level, message, context)
  │
  ├─► _logBuffer.add(entry)
  │
  ├─► 如果 _logBuffer.size >= BUFFER_SIZE (50条)
  │     │
  │     ├─► 批量写入文件 (_writeToFileBatch)
  │     │
  │     └─► 清空缓冲区
  │
  └─► 如果日志文件大小 >= MAX_FILE_SIZE (5MB)
        │
        └─► 轮转日志文件 (_rotateLogFile)
```

**日志上传流程：**
```
uploadLogs(progressCallback)
  │
  ├─► 收集本地日志文件 (getLogFiles)
  │
  ├─► 如果没有日志文件
  │     │
  │     └─► 返回 false (无可上传日志)
  │
  ├─► 创建临时zip文件
  │
  ├─► 遍历日志文件并添加到zip
  │     │
  │     └─► progressCallback(current, total)
  │
  ├─► 调用 API POST /api/v1/logs/upload
  │     │
  │     ├─► Content-Type: multipart/form-data
  │     ├─► X-Device-ID: <device_id>
  │     └─► file: <zip_file>
  │
  ├─► 如果上传成功
  │     │
  │     └─► 可选择删除本地日志
  │
  └─► 返回上传结果
```

#### 3.1.4 异常处理

| 异常场景 | 处理策略 |
|----------|----------|
| 日志写入失败 | 保留在缓冲区，等待下次重试 |
| 文件系统满 | 跳过最旧的日志文件 |
| 上传超时 | 返回错误，允许用户重试 |
| 服务器返回错误码 | 根据错误码显示用户提示 |
| 日志文件损坏 | 跳过该文件，继续处理其他文件 |

---

## 四、后端模块详细设计

### 4.1 日志收集服务

#### 4.1.1 模块职责

根据 ARCH v1.2 和 API.md：
1. 接收客户端上传的日志文件
2. 解析日志内容并存储到数据库
3. 验证日志格式和大小
4. 提供日志分析查询接口（预留）

#### 4.1.2 内部类设计

```
backend/
├── app/
│   ├── modules/
│   │   └── log/
│   │       ├── __init__.py
│   │       ├── routes.py          # 日志上传接口
│   │       ├── models.py          # 日志数据模型
│   │       ├── schemas.py         # Pydantic模型
│   │       └── service.py         # 日志处理服务
│   └── main.py                    # 路由注册
└── celery_tasks/
    └── crawl.py                   # 商品爬取任务
```

**类图：**

```
┌─────────────────────────────────────┐
│           LogUploadService         │
├─────────────────────────────────────┤
│ - MAX_FILE_SIZE: int = 5 * 1024 * 1024 │
│ - ALLOWED_EXTENSIONS: List[str]     │
├─────────────────────────────────────┤
│ + validate_file(file): bool        │
│ + parse_log_content(content): List[Dict] │
│ + save_logs(logs, device_id): int  │
│ + cleanup_old_logs(days): int      │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│         UploadLogRequest            │
├─────────────────────────────────────┤
│ - app_version: str                 │
│ - platform: str                    │
│ - os_version: str                  │
│ - log_start_date: datetime         │
│ - log_end_date: datetime           │
│ - file: UploadFile                 │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│          DeviceLog (Model)          │
├─────────────────────────────────────┤
│ - id: BigInteger PK                │
│ - device_id: String                │
│ - log_level: String                │
│ - message: Text                    │
│ - context: JSON                    │
│ - app_version: String              │
│ - platform: String                 │
│ - os_version: String               │
│ - api_endpoint: String?            │
│ - error_code: String?              │
│ - stack_trace: Text?               │
│ - log_timestamp: DateTime          │
│ - received_at: DateTime            │
└─────────────────────────────────────┘
```

#### 4.1.3 接口实现

**POST /api/v1/logs/upload**

```python
@router.post("/upload", response_model=dict)
async def upload_logs(
    request: UploadLogRequest,
    device_id: Optional[str] = Header(None),
    db: Session = Depends(get_db)
):
    # 1. 验证文件大小
    if request.file.size > MAX_FILE_SIZE:
        return error_response(
            code=40001,
            message=f"日志文件过大，最大支持5MB",
            data={"maxSize": MAX_FILE_SIZE, "actualSize": request.file.size}
        )

    # 2. 读取并解析日志文件
    content = await request.file.read()
    try:
        logs = parse_log_content(content.decode('utf-8'))
    except Exception as e:
        return error_response(code=40002, message="日志格式错误")

    # 3. 批量保存日志
    saved_count = save_logs(logs, device_id)

    return success_response(data={
        "uploadId": f"upload_{datetime.now().strftime('%Y%m%d%H%M%S')}",
        "receivedAt": datetime.now().isoformat(),
        "logCount": saved_count,
        "status": "received"
    })
```

#### 4.1.4 异常处理

| 异常场景 | HTTP状态码 | 错误码 | 处理策略 |
|----------|------------|--------|----------|
| 文件超过5MB | 413 | 40001 | 返回最大限制，实际大小 |
| 文件格式错误 | 400 | 40002 | 提示正确的日志格式 |
| 文件扩展名不支持 | 400 | 40002 | 提示支持 .txt/.log/.zip |
| 解析失败 | 400 | 40002 | 返回具体解析错误位置 |
| 数据库存储失败 | 500 | 40003 | 记录服务器错误，返回通用消息 |
| 设备ID缺失 | 400 | 10001 | 使用默认值或要求重新上传 |

---

## 五、现有模块完善

### 5.1 问卷偏好模块（前端）

根据 UI_DESIGN.md 和 PRD，需要确保以下组件正常工作：

| 组件 | 功能 | 状态 |
|------|------|------|
| QuestionnaireItem | 问卷问题展示 | 需验证 |
| BrandSelector | 品牌偏好选择 | 需验证 |
| BudgetSlider | 预算滑块 | 需验证 |

**数据流：**
```
用户选择品牌偏好
      │
      ▼
BrandSelector.onSelect(brand)
      │
      ▼
UserPreference.preferredBrands.add(brand)
      │
      ▼
保存到本地 SQLite (preferences表)
```

### 5.2 方案展示模块（前端）

根据 UI_DESIGN.md 和 DATABASE.md：

| 组件 | 功能 | 数据来源 |
|------|------|----------|
| SchemeCard | 方案卡片 | 本地 SQLite (schemes表) |
| DeviceCard | 设备卡片 | 本地 SQLite (scheme_devices表) + product_snapshots表 |

**数据结构：**
```
Scheme
├── id: String (UUID)
├── scheme_name: String
├── description: String
├── total_price: Decimal
├── status: Int (1=生成中, 2=成功, 3=失败)
├── created_at: DateTime
└── devices: List<SchemeDevice>

SchemeDevice
├── id: String (UUID)
├── scheme_id: String (FK)
├── product_snapshot: ProductSnapshot
├── room_name: String
├── quantity: Int
├── unit_price: Decimal
├── subtotal: Decimal
└── reason: String

ProductSnapshot
├── id: String (UUID)
├── product_id: String
├── product_name: String
├── brand: String
├── price: Decimal
├── image_url: String
├── product_url: String
├── specs: JSON
└── snapshot_at: DateTime
```

---

## 六、部署环境要求

### 6.1 后端环境

详见 `../DEV/ENV.md`

### 6.2 前端环境

| 项目 | 要求 |
|------|------|
| Flutter SDK | 3.24.0 |
| Dart SDK | 3.0+ |
| Android SDK | API 21+ |
| iOS | 12.0+ |

---

## 七、测试要点

### 7.1 日志模块测试

| 测试项 | 预期结果 |
|--------|----------|
| 日志记录 | ERROR/WARN/INFO/DEBUG级别的日志正确写入文件 |
| 日志轮转 | 文件超过5MB时自动创建新文件 |
| 日志清理 | 删除7天前的日志文件 |
| 日志上传 | 成功上传并返回logCount |
| 上传进度 | 进度回调正确触发 |
| 错误处理 | 网络异常时返回友好错误提示 |

### 7.2 集成测试

| 测试项 | 预期结果 |
|--------|----------|
| 品牌列表加载 | API返回品牌数据并正确展示 |
| 方案生成 | 完整流程：提交请求 → 等待动画 → 方案展示 |
| 方案列表 | 本地方案列表正确加载和展示 |

---

## 八、版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|----------|--------|
| v1.0 | 2026-03-24 | 适配PRD v1.2新增日志管理模块 | 开发 |
