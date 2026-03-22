# API 通用规范

| 文档版本 | 修改日期   | 修改人 | 修改内容 |
|----------|------------|--------|----------|
| v1.0     | 2026-03-21 | 架构师 | 初稿     |
| v1.1     | 2026-03-21 | 架构师 | 适配PRD v1.1：移除认证规范，简化为无状态API |

---

## 一、API 设计原则

1. **RESTful 风格**：遵循 REST 架构风格，资源导向设计
2. **版本控制**：所有 API 统一使用版本前缀
3. **统一响应**：所有接口返回统一的响应格式
4. **无状态设计**：服务端不存储用户会话，每次请求携带完整数据
5. **幂等性**：GET、PUT、DELETE 操作保证幂等性

---

## 二、URL 规范

### 2.1 基础路径

```
https://api.smart-home.com/api/v1
```

### 2.2 路径规则

| 规则 | 说明 | 示例 |
|------|------|------|
| 使用小写字母 | 路径全部小写 | `/api/v1/schemes` |
| 使用连字符分隔 | 多个单词用 `-` 连接 | `/api/v1/product-categories` |
| 使用复数名词 | 资源使用复数形式 | `/api/v1/products` |
| 避免嵌套过深 | 最多2层嵌套 | `/api/v1/categories/{id}/products` |

### 2.3 HTTP 方法语义

| 方法 | 语义 | 幂等性 | 示例 |
|------|------|--------|------|
| GET | 查询资源 | 是 | `GET /products` 查询商品列表 |
| POST | 创建资源/执行操作 | 否 | `POST /schemes/generate` 生成方案 |

---

## 三、请求规范

### 3.1 请求头

| Header | 必填 | 说明 |
|--------|------|------|
| `Content-Type` | 是 | 固定值：`application/json` |
| `X-Request-ID` | 否 | 请求追踪ID，用于日志追踪 |
| `X-Device-ID` | 否 | 设备标识，用于限流统计 |

### 3.2 查询参数

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` | int | 页码，从1开始 |
| `page_size` | int | 每页数量，默认20，最大100 |
| `sort_by` | string | 排序字段 |
| `order` | string | 排序方向：`asc` / `desc` |

---

## 四、响应规范

### 4.1 统一响应格式

**成功响应：**
```json
{
  "code": 0,
  "message": "success",
  "data": {
  },
  "timestamp": 1710979200000
}
```

**失败响应：**
```json
{
  "code": 10001,
  "message": "参数错误",
  "data": null,
  "timestamp": 1710979200000
}
```

**分页响应：**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [],
    "pagination": {
      "page": 1,
      "page_size": 20,
      "total": 100,
      "total_pages": 5
    }
  },
  "timestamp": 1710979200000
}
```

### 4.2 HTTP 状态码

| 状态码 | 说明 | 使用场景 |
|--------|------|----------|
| 200 | 成功 | 请求处理成功 |
| 400 | 请求参数错误 | 参数校验失败 |
| 404 | 资源不存在 | 资源未找到 |
| 429 | 请求过于频繁 | 触发限流 |
| 500 | 服务器内部错误 | 服务异常 |
| 502 | 上游服务错误 | AI服务异常 |
| 503 | 服务不可用 | 服务维护中 |

---

## 五、错误码规范

### 5.1 错误码结构

```
错误码格式：XXYYZ
- XX：模块代码
- YY：错误类型
- Z：具体错误序号
```

### 5.2 模块代码

| 模块代码 | 模块名称 |
|----------|----------|
| 10 | 通用/系统 |
| 20 | AI方案服务 |
| 30 | 商品服务 |

### 5.3 错误类型

| 类型代码 | 错误类型 |
|----------|----------|
| 00 | 系统错误 |
| 01 | 参数错误 |
| 02 | 业务错误 |

### 5.4 常用错误码

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 10001 | 参数错误 | 请求参数校验失败 |
| 10002 | 系统繁忙 | 服务器内部错误 |
| 10003 | 请求过于频繁 | 触发限流 |
| 20001 | AI生成失败 | DeepSeek调用失败 |
| 20002 | AI生成超时 | 方案生成超时 |
| 20003 | 图片解析失败 | OCR识别失败 |
| 30001 | 商品不存在 | 商品ID无效 |
| 30002 | 商品已下架 | 商品状态不可用 |
| 30003 | 无匹配商品 | 预算范围内无匹配商品 |

---

## 六、限流规范

### 6.1 限流策略

| 接口类型 | 限流规则 | 说明 |
|----------|----------|------|
| 普通接口 | 100次/分钟/设备 | 常规业务接口 |
| 方案生成 | 10次/小时/设备 | AI生成接口 |
| 商品查询 | 200次/分钟/设备 | 商品列表查询 |

### 6.2 限流响应

```json
{
  "code": 10003,
  "message": "请求过于频繁，请稍后再试",
  "data": {
    "retry_after": 60
  }
}
```

---

## 七、接口模块划分

### 7.1 接口分组

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| AI方案 | `/api/v1/schemes` | 方案生成、图片解析 |
| 商品 | `/api/v1/products` | 商品查询、商品匹配 |
| 分类 | `/api/v1/categories` | 商品分类查询 |
| 品牌 | `/api/v1/brands` | 品牌列表查询 |

### 7.2 核心接口概要

#### AI方案服务

| 接口 | 方法 | 说明 |
|------|------|------|
| `/schemes/generate` | POST | 生成智能方案 |
| `/schemes/parse-image` | POST | 解析户型图（可选） |

**方案生成请求示例：**
```json
{
  "houseLayout": {
    "totalArea": 90.0,
    "rooms": [
      {
        "roomName": "客厅",
        "roomType": "living_room",
        "length": 5.0,
        "width": 4.0,
        "area": 20.0
      }
    ]
  },
  "questionnaire": {
    "livingStatus": "own",
    "residentCount": 2,
    "hasElderly": false,
    "hasChildren": false,
    "hasPets": true,
    "preferredScenarios": ["lighting", "security"],
    "sleepPattern": "normal",
    "knowledgeLevel": "basic"
  },
  "preferences": {
    "budgetMin": 5000,
    "budgetMax": 15000,
    "preferredBrands": ["小米", "Aqara"],
    "excludedBrands": []
  }
}
```

**方案生成响应示例：**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "schemeName": "温馨两居智能生活方案",
    "schemeDescription": "本方案为90平米两居室设计...",
    "devices": [
      {
        "deviceType": "智能灯泡",
        "deviceName": "智能LED灯泡",
        "room": "客厅",
        "quantity": 4,
        "reason": "客厅面积较大，4个灯泡可满足整体照明需求",
        "matchedProduct": {
          "productId": "tb_12345678",
          "productName": "米家智能LED灯泡 彩光版",
          "brand": "小米",
          "price": 79.00,
          "imageUrl": "https://...",
          "productUrl": "https://item.taobao.com/..."
        },
        "subtotal": 316.00
      }
    ],
    "totalPrice": 8560.00,
    "budgetRemaining": 6440.00
  }
}
```

#### 商品服务

| 接口 | 方法 | 说明 |
|------|------|------|
| `/products` | GET | 查询商品列表 |
| `/products/{id}` | GET | 查询商品详情 |
| `/products/match` | POST | 商品匹配（按预算和类型） |
| `/categories` | GET | 查询商品分类 |
| `/brands` | GET | 查询品牌列表 |

### 7.3 开发人员职责

> **架构师定义**：以上为API通用规范，所有接口必须遵循。
> 
> **开发人员职责**：各模块的具体接口定义（详细参数、返回值）由开发人员在详细设计阶段完成，需遵循本规范。

---

## 八、接口文档

### 8.1 文档工具

使用 **Swagger/OpenAPI** 自动生成接口文档。

### 8.2 访问地址

```
开发环境：http://localhost:8000/docs
生产环境：https://api.smart-home.com/docs
```

---

## 九、版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|----------|--------|
| v1.0 | 2026-03-21 | 初稿 | 架构师 |
| v1.1 | 2026-03-21 | 适配PRD v1.1：移除认证规范，简化为无状态API | 架构师 |
