# 技术栈说明文档

## 文档信息

| 项目名称 | 智能家居方案定制APP |
|---------|-------------------|
| 文档版本 | V1.2 |
| 创建日期 | 2026-03-14 |
| 更新日期 | 2026-03-17 |
| 架构师 | AI System Architect |
| 文档状态 | 待评审 |

---

## 一、技术架构概览

### 1.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    客户端层 (Flutter Cross-Platform)                     │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    Presentation Layer                            │   │
│  │   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐          │   │
│  │   │   Widgets   │──▶│    BLoC     │──▶│   Events    │          │   │
│  │   │    (UI)     │   │  /Cubit     │   │   States    │          │   │
│  │   └─────────────┘   └─────────────┘   └─────────────┘          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      Domain Layer                                │   │
│  │   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐          │   │
│  │   │  Use Cases  │──▶│  Entities   │──▶│ Repository  │          │   │
│  │   │             │   │             │   │ Interfaces  │          │   │
│  │   └─────────────┘   └─────────────┘   └─────────────┘          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                       Data Layer                                 │   │
│  │   ┌─────────────────────┐   ┌─────────────────────┐             │   │
│  │   │   Local Storage     │   │   Remote API        │             │   │
│  │   │ (Hive/SQLite)       │   │   (Dio/HTTP)        │             │   │
│  │   └─────────────────────┘   └─────────────────────┘             │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                   Platform Adapters                              │   │
│  │   ┌───────────┐   ┌───────────┐   ┌───────────┐                 │   │
│  │   │  Android  │   │    iOS    │   │    Web    │                 │   │
│  │   │  (APK)    │   │   (IPA)   │   │  (HTML5)  │                 │   │
│  │   └───────────┘   └───────────┘   └───────────┘                 │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          服务端层 (Backend)                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                      │
│  │  API Gateway│  │ Biz Services│  │  AI Service │                      │
│  │   (Nginx)   │  │  (Node.js)  │  │  (DeepSeek) │                      │
│  └─────────────┘  └─────────────┘  └─────────────┘                      │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      Data Access Layer                           │   │
│  │                   ORM (Prisma/TypeORM)                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          数据层 (Database)                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                      │
│  │  PostgreSQL │  │    Redis    │  │   OSS/S3    │                      │
│  │  (主数据库)  │  │  (缓存)     │  │  (文件存储) │                      │
│  └─────────────┘  └─────────────┘  └─────────────┘                      │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        第三方服务层 (External)                           │
│  ┌─────────────┐  ┌─────────────┐                                      │
│  │  DeepSeek   │  │  淘宝开放平台│                                      │
│  │    API      │  │   (商品API) │                                      │
│  └─────────────┘  └─────────────┘                                      │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1.2 技术选型原则

| 原则 | 说明 |
|------|------|
| 跨平台优先 | 一套代码支持Android、iOS、Web三端 |
| 成熟稳定 | 优先选择社区活跃、文档完善的技术 |
| 易于维护 | 代码结构清晰，便于团队协作 |
| 性能优先 | 满足性能指标要求 |
| 成本可控 | 在满足需求的前提下控制成本 |
| 简化架构 | 无需用户登录，简化认证流程 |

---

## 二、客户端技术栈

### 2.1 开发语言与框架

| 技术 | 版本 | 说明 |
|------|------|------|
| Flutter | 3.16+ | 跨平台UI框架 |
| Dart | 3.2+ | 开发语言 |
| flutter_bloc | 8.1+ | 状态管理 (BLoC模式) |
| equatable | 2.0+ | 值对象比较 |

### 2.2 架构模式

采用 **BLoC + Clean Architecture** 架构：

```
┌────────────────────────────────────────────────────────────────┐
│                    Presentation Layer                           │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │   Widgets    │◀──▶│    BLoC/     │◀──▶│    States    │     │
│  │   (Screens)  │    │    Cubit     │    │    Events    │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
└────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌────────────────────────────────────────────────────────────────┐
│                      Domain Layer                               │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │   Use Cases  │───▶│   Entities   │───▶│  Repository  │     │
│  │              │    │              │    │  Interfaces  │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
└────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌────────────────────────────────────────────────────────────────┐
│                       Data Layer                                │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │  Repository  │───▶│    Hive/     │    │     Dio      │     │
│  │   Impl       │    │   SQLite     │    │   (Remote)   │     │
│  └──────────────┘    │   (Local)    │    └──────────────┘     │
│                      └──────────────┘                          │
└────────────────────────────────────────────────────────────────┘
```

### 2.3 核心组件

| 组件 | 技术选型 | 用途 |
|------|---------|------|
| 状态管理 | flutter_bloc | BLoC/Cubit状态管理 |
| 依赖注入 | get_it + injectable | 服务定位与依赖注入 |
| 网络请求 | dio + retrofit | API调用 |
| 本地存储 | hive + hive_flutter | 轻量级键值存储 |
| 关系型存储 | sqflite | SQLite数据库 |
| 图片加载 | cached_network_image | 图片加载与缓存 |
| 序列化 | json_serializable | JSON序列化 |
| 导航 | go_router | 声明式路由 |
| PDF生成 | pdf + printing | PDF导出功能 |
| 设备信息 | device_info_plus | 获取设备标识 |
| 网络状态 | connectivity_plus | 网络连接检测 |
| 包信息 | package_info_plus | 应用版本信息 |
| 分享功能 | share_plus | 系统分享功能 |
| URL启动 | url_launcher | 跳转淘宝等外部链接 |

### 2.4 跨平台设备标识方案

由于需要支持Android、iOS和Web三端，设备标识获取方案如下：

```dart
import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/foundation.dart' show kIsWeb;

class DeviceIdService {
  final DeviceInfoPlugin _deviceInfo = DeviceInfoPlugin();
  
  Future<String> getDeviceId() async {
    if (kIsWeb) {
      final webInfo = await _deviceInfo.webBrowserInfo;
      return _generateWebDeviceId(webInfo);
    }
    
    if (Platform.isAndroid) {
      final androidInfo = await _deviceInfo.androidInfo;
      return androidInfo.id;
    }
    
    if (Platform.isIOS) {
      final iosInfo = await _deviceInfo.iosInfo;
      return iosInfo.identifierForVendor ?? _generateFallbackId();
    }
    
    return _generateFallbackId();
  }
  
  String _generateWebDeviceId(WebBrowserInfo webInfo) {
    final fingerprint = '${webInfo.userAgent}-${webInfo.platform}';
    return _hashFingerprint(fingerprint);
  }
  
  String _generateFallbackId() {
    return UUID.randomUUID().toString();
  }
}
```

### 2.5 平台适配说明

| 平台 | 设备标识 | 本地存储 | 特殊处理 |
|------|---------|---------|---------|
| Android | androidInfo.id | Hive + SQLite | 无 |
| iOS | identifierForVendor | Hive + SQLite | 无 |
| Web | Browser Fingerprint | Hive (IndexedDB) | 离线功能受限 |

### 2.6 项目目录结构

```
lib/
├── main.dart                              # 入口文件
├── app.dart                               # App配置
├── core/                                  # 核心模块
│   ├── constants/                         # 常量定义
│   │   ├── app_constants.dart
│   │   └── api_constants.dart
│   ├── errors/                            # 错误处理
│   │   ├── exceptions.dart
│   │   └── failures.dart
│   ├── network/                           # 网络配置
│   │   ├── dio_client.dart
│   │   ├── api_interceptor.dart
│   │   └── network_info.dart
│   ├── storage/                           # 本地存储
│   │   ├── hive_service.dart
│   │   └── secure_storage.dart
│   ├── theme/                             # 主题配置
│   │   ├── app_theme.dart
│   │   ├── colors.dart
│   │   └── text_styles.dart
│   ├── utils/                             # 工具类
│   │   ├── device_id_service.dart
│   │   ├── date_utils.dart
│   │   └── validators.dart
│   └── widgets/                           # 公共组件
│       ├── buttons/
│       ├── inputs/
│       ├── cards/
│       └── loading/
├── features/                              # 功能模块
│   ├── splash/                            # 启动页
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   │       ├── splash_screen.dart
│   │       └── splash_cubit.dart
│   ├── home/                              # 首页
│   │   ├── data/
│   │   │   ├── models/
│   │   │   └── repositories/
│   │   ├── domain/
│   │   │   ├── entities/
│   │   │   ├── repositories/
│   │   │   └── usecases/
│   │   └── presentation/
│   │       ├── pages/
│   │       │   └── home_page.dart
│   │       ├── widgets/
│   │       └── bloc/
│   │           ├── home_bloc.dart
│   │           ├── home_event.dart
│   │           └── home_state.dart
│   ├── info_collection/                   # 信息采集
│   │   ├── data/
│   │   │   ├── models/
│   │   │   │   ├── user_info_model.dart
│   │   │   │   ├── house_layout_model.dart
│   │   │   │   └── room_model.dart
│   │   │   └── repositories/
│   │   │       └── info_repository_impl.dart
│   │   ├── domain/
│   │   │   ├── entities/
│   │   │   │   ├── user_info.dart
│   │   │   │   ├── house_layout.dart
│   │   │   │   └── room.dart
│   │   │   ├── repositories/
│   │   │   │   └── info_repository.dart
│   │   │   └── usecases/
│   │   │       ├── save_user_info.dart
│   │   │       ├── save_house_layout.dart
│   │   │       └── save_budget.dart
│   │   └── presentation/
│   │       ├── pages/
│   │       │   ├── basic_info_page.dart
│   │       │   ├── lifestyle_page.dart
│   │       │   ├── device_experience_page.dart
│   │       │   ├── aesthetic_preference_page.dart
│   │       │   ├── brand_preference_page.dart
│   │       │   ├── house_layout_page.dart
│   │       │   └── budget_page.dart
│   │       ├── widgets/
│   │       │   ├── step_indicator.dart
│   │       │   └── form_fields.dart
│   │       └── bloc/
│   │           ├── info_collection_bloc.dart
│   │           ├── info_collection_event.dart
│   │           └── info_collection_state.dart
│   ├── scheme/                            # 方案模块
│   │   ├── data/
│   │   │   ├── models/
│   │   │   │   ├── scheme_model.dart
│   │   │   │   ├── scheme_device_model.dart
│   │   │   │   └── decoration_guide_model.dart
│   │   │   └── repositories/
│   │   │       └── scheme_repository_impl.dart
│   │   ├── domain/
│   │   │   ├── entities/
│   │   │   │   ├── scheme.dart
│   │   │   │   ├── scheme_device.dart
│   │   │   │   └── decoration_guide.dart
│   │   │   ├── repositories/
│   │   │   │   └── scheme_repository.dart
│   │   │   └── usecases/
│   │   │       ├── generate_scheme.dart
│   │   │       ├── get_scheme.dart
│   │   │       ├── save_scheme.dart
│   │   │       ├── delete_scheme.dart
│   │   │       └── export_scheme_pdf.dart
│   │   └── presentation/
│   │       ├── pages/
│   │       │   ├── generating_page.dart
│   │       │   ├── scheme_detail_page.dart
│   │       │   └── scheme_list_page.dart
│   │       ├── widgets/
│   │       │   ├── scheme_card.dart
│   │       │   ├── device_list_tile.dart
│   │       │   └── decoration_guide_view.dart
│   │       └── bloc/
│   │           ├── scheme_bloc.dart
│   │           ├── scheme_event.dart
│   │           └── scheme_state.dart
│   ├── device/                            # 设备模块
│   │   ├── data/
│   │   │   ├── models/
│   │   │   │   └── device_model.dart
│   │   │   └── repositories/
│   │   │       └── device_repository_impl.dart
│   │   ├── domain/
│   │   │   ├── entities/
│   │   │   │   └── device.dart
│   │   │   ├── repositories/
│   │   │   │   └── device_repository.dart
│   │   │   └── usecases/
│   │   │       ├── get_device_detail.dart
│   │   │       └── get_device_price.dart
│   │   └── presentation/
│   │       ├── pages/
│   │       │   └── device_detail_page.dart
│   │       └── bloc/
│   │           ├── device_bloc.dart
│   │           ├── device_event.dart
│   │           └── device_state.dart
│   ├── my_schemes/                        # 我的方案
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   │       ├── pages/
│   │       │   └── my_schemes_page.dart
│   │       └── bloc/
│   │           ├── my_schemes_bloc.dart
│   │           ├── my_schemes_event.dart
│   │           └── my_schemes_state.dart
│   ├── profile/                           # 个人中心
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   │       ├── pages/
│   │       │   └── profile_page.dart
│   │       └── bloc/
│   │           ├── profile_bloc.dart
│   │           ├── profile_event.dart
│   │           └── profile_state.dart
│   └── feedback/                          # 反馈模块
│       ├── data/
│       ├── domain/
│       └── presentation/
│           ├── pages/
│           │   └── feedback_page.dart
│           └── bloc/
│               ├── feedback_bloc.dart
│               ├── feedback_event.dart
│               └── feedback_state.dart
├── injection_container.dart               # 依赖注入配置
└── routes/                                # 路由配置
    ├── app_router.dart
    └── route_names.dart

test/                                       # 测试目录
├── unit/
├── widget/
└── integration/
```

---

## 三、服务端技术栈

### 3.1 开发语言与框架

| 技术 | 版本 | 说明 |
|------|------|------|
| Node.js | 18 LTS | 运行时环境 |
| TypeScript | 5.0+ | 开发语言 |
| NestJS | 10+ | 后端框架 |
| Express | 4.18+ | HTTP 服务框架 |

### 3.2 数据存储

| 技术 | 版本 | 说明 |
|------|------|------|
| PostgreSQL | 15+ | 主数据库 |
| Redis | 7+ | 缓存与会话存储 |
| Prisma | 5+ | ORM 框架 |

### 3.3 服务端架构

采用 **模块化分层架构**：

```
┌────────────────────────────────────────────────────────────────┐
│                      API Gateway (Nginx)                        │
└────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌────────────────────────────────────────────────────────────────┐
│                    Application Layer                            │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │ User Module  │    │Scheme Module │    │Device Module │     │
│  │  (用户管理)   │    │  (方案管理)   │    │  (设备管理)  │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │Feedback Mod  │    │  AI Module   │    │ Export Module│     │
│  │  (反馈管理)   │    │ (AI服务集成) │    │  (PDF导出)   │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
└────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌────────────────────────────────────────────────────────────────┐
│                      Core Layer                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │   Services   │    │  Repositories│    │    Utils     │     │
│  │   (业务逻辑)  │    │  (数据访问)   │    │   (工具类)   │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
└────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌────────────────────────────────────────────────────────────────┐
│                   Infrastructure Layer                          │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │  PostgreSQL  │    │    Redis     │    │ External API │     │
│  │   (数据库)    │    │   (缓存)     │    │  (第三方服务) │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
└────────────────────────────────────────────────────────────────┘
```

### 3.4 服务端项目目录结构

```
server/
├── package.json
├── tsconfig.json
├── nest-cli.json
├── prisma/
│   ├── schema.prisma                    # 数据库模型
│   └── migrations/                      # 数据库迁移
├── src/
│   ├── main.ts                          # 入口文件
│   ├── app.module.ts                    # 根模块
│   ├── common/                          # 公共模块
│   │   ├── decorators/                  # 自定义装饰器
│   │   ├── filters/                     # 异常过滤器
│   │   ├── guards/                      # 守卫
│   │   ├── interceptors/                # 拦截器
│   │   ├── pipes/                       # 管道
│   │   └── dto/                         # 公共 DTO
│   ├── config/                          # 配置模块
│   │   ├── app.config.ts
│   │   ├── database.config.ts
│   │   └── third-party.config.ts
│   ├── modules/                         # 业务模块
│   │   ├── user/                        # 用户模块
│   │   │   ├── user.module.ts
│   │   │   ├── user.controller.ts
│   │   │   ├── user.service.ts
│   │   │   └── dto/
│   │   ├── user-info/                   # 用户信息模块
│   │   │   ├── user-info.module.ts
│   │   │   ├── user-info.controller.ts
│   │   │   ├── user-info.service.ts
│   │   │   └── dto/
│   │   ├── scheme/                      # 方案模块
│   │   │   ├── scheme.module.ts
│   │   │   ├── scheme.controller.ts
│   │   │   ├── scheme.service.ts
│   │   │   └── dto/
│   │   ├── device/                      # 设备模块
│   │   │   ├── device.module.ts
│   │   │   ├── device.controller.ts
│   │   │   ├── device.service.ts
│   │   │   └── dto/
│   │   ├── feedback/                    # 反馈模块
│   │   │   ├── feedback.module.ts
│   │   │   ├── feedback.controller.ts
│   │   │   ├── feedback.service.ts
│   │   │   └── dto/
│   │   ├── ai/                          # AI服务模块
│   │   │   ├── ai.module.ts
│   │   │   ├── ai.service.ts
│   │   │   └── deepseek/
│   │   │       └── deepseek.service.ts
│   │   └── export/                      # 导出模块
│   │       ├── export.module.ts
│   │       ├── export.controller.ts
│   │       ├── export.service.ts
│   │       └── pdf/
│   │           └── pdf.generator.ts
│   ├── entities/                        # 数据库实体
│   │   ├── user.entity.ts
│   │   ├── user-info.entity.ts
│   │   ├── house-layout.entity.ts
│   │   ├── room.entity.ts
│   │   ├── scheme.entity.ts
│   │   ├── scheme-device.entity.ts
│   │   ├── device.entity.ts
│   │   └── feedback.entity.ts
│   └── utils/                           # 工具类
│       ├── device-id.util.ts
│       ├── price.util.ts
│       └── validation.util.ts
└── test/                                # 测试目录
    ├── unit/
    └── e2e/
```

---

## 四、第三方服务集成

### 4.1 DeepSeek API 集成

| 项目 | 说明 |
|------|------|
| 服务商 | DeepSeek |
| API类型 | RESTful API |
| 用途 | AI方案生成 |
| 认证方式 | API Key |

**调用示例**：

```typescript
const response = await deepseekService.generateScheme({
  userInfo: userInfo,
  houseLayout: houseLayout,
  budget: budget,
  preferences: preferences
});
```

### 4.2 淘宝开放平台集成

| 项目 | 说明 |
|------|------|
| 服务商 | 阿里巴巴 |
| API类型 | RESTful API |
| 用途 | 商品价格查询、购买链接 |
| 认证方式 | AppKey + AppSecret |

**调用示例**：

```typescript
const priceInfo = await taobaoService.getItemPrice({
  itemId: device.taobaoItemId,
  fields: ['price', 'title', 'pic_url']
});
```

---

## 五、安全设计

### 5.1 数据传输安全

| 项目 | 说明 |
|------|------|
| 协议 | HTTPS (TLS 1.2+) |
| 证书 | Let's Encrypt / 商业证书 |
| 证书校验 | 客户端强制校验 |

### 5.2 设备认证

```typescript
@Injectable()
export class DeviceAuthGuard implements CanActivate {
  canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest();
    const deviceId = request.headers['x-device-id'];
    
    if (!deviceId) {
      throw new UnauthorizedException('Device ID required');
    }
    
    return this.validateDevice(deviceId);
  }
}
```

### 5.3 API密钥管理

| 密钥类型 | 存储位置 | 说明 |
|---------|---------|------|
| DeepSeek API Key | 服务端环境变量 | 不暴露给客户端 |
| 淘宝 AppSecret | 服务端环境变量 | 不暴露给客户端 |
| 数据库密码 | 服务端环境变量 | 加密存储 |

---

## 六、性能优化策略

### 6.1 客户端优化

| 优化项 | 策略 |
|--------|------|
| 启动速度 | 延迟加载非必要模块 |
| 图片加载 | cached_network_image缓存 |
| 列表性能 | ListView.builder懒加载 |
| 状态管理 | BLoC避免不必要的重建 |
| 离线缓存 | Hive本地数据缓存 |

### 6.2 服务端优化

| 优化项 | 策略 |
|--------|------|
| 数据库查询 | 索引优化、查询缓存 |
| API响应 | Redis缓存热点数据 |
| AI调用 | 异步处理、超时控制 |
| 并发处理 | 集群部署、负载均衡 |

---

## 七、部署架构

### 7.1 生产环境部署图

```
┌─────────────────────────────────────────────────────────────────┐
│                         CDN (静态资源)                           │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Load Balancer (Nginx)                       │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │  Node.js │   │  Node.js │   │  Node.js │
        │  Server 1│   │  Server 2│   │  Server 3│
        └──────────┘   └──────────┘   └──────────┘
              │               │               │
              └───────────────┼───────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Database Cluster                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  PostgreSQL │  │  PostgreSQL │  │    Redis    │             │
│  │   Primary   │──│   Replica   │  │   Cluster   │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 客户端发布渠道

| 平台 | 发布渠道 | 说明 |
|------|---------|------|
| Android | 应用商店、APK直装 | 支持Google Play、国内应用商店 |
| iOS | App Store | 需Apple开发者账号 |
| Web | 云服务器部署 | 支持主流浏览器访问 |

---

## 八、版本兼容性要求

### 8.1 客户端兼容性

| 平台 | 最低版本 | 目标版本 |
|------|---------|---------|
| Android | Android 5.0 (API 21) | Android 14 (API 34) |
| iOS | iOS 11.0 | iOS 17 |
| Web | Chrome 80+, Safari 13+ | 最新版本 |

### 8.2 服务端环境

| 组件 | 版本要求 |
|------|---------|
| Node.js | 18 LTS 或更高 |
| PostgreSQL | 15+ |
| Redis | 7+ |
| Nginx | 1.24+ |

---

## 九、开发工具与规范

### 9.1 开发工具

| 工具 | 用途 |
|------|------|
| VS Code / Android Studio | 代码编辑器 |
| Flutter SDK | 跨平台开发框架 |
| Dart Analysis | 代码静态分析 |
| Postman | API测试 |
| DBeaver | 数据库管理 |
| Docker | 容器化部署 |

### 9.2 代码规范

| 项目 | 规范 |
|------|------|
| Dart代码 | Effective Dart |
| TypeScript代码 | ESLint + Prettier |
| 提交规范 | Conventional Commits |
| 分支策略 | Git Flow |

### 9.3 测试策略

| 测试类型 | 工具 | 覆盖率要求 |
|---------|------|-----------|
| 单元测试 | flutter_test / Jest | ≥ 80% |
| Widget测试 | flutter_test | 核心组件 |
| 集成测试 | integration_test | 核心流程 |
| E2E测试 | Cypress / Playwright | 关键业务 |
