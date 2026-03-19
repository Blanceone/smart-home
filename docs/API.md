# API 接口文档

## 文档信息

| 项目名称 | 智能家居方案定制APP |
|---------|-------------------|
| 文档版本 | V1.2 |
| 创建日期 | 2026-03-14 |
| 更新日期 | 2026-03-17 |
| 架构师 | AI System Architect |
| 文档状态 | 待评审 |

---

## 一、接口规范

### 1.1 基础信息

| 项目 | 说明 |
|------|------|
| 基础路径 | `https://api.smarthome.com/v1` |
| 协议 | HTTPS |
| 数据格式 | JSON |
| 编码 | UTF-8 |
| 时区 | UTC+8 (北京时间) |

### 1.2 请求规范

#### 请求头

| Header | 必填 | 说明 |
|--------|------|------|
| Content-Type | 是 | `application/json` |
| X-Device-ID | 是 | 设备唯一标识 (跨平台) |
| X-Request-ID | 否 | 请求唯一标识，用于问题追踪 |
| X-Platform | 是 | 平台标识: `android`, `ios`, `web` |
| X-Version | 是 | APP版本号: `1.0.0` |
| X-Device-Model | 否 | 设备型号 (如: iPhone 15, Pixel 8) |
| X-OS-Version | 否 | 操作系统版本 |

#### 设备标识说明

| 平台 | 设备标识来源 | 说明 |
|------|-------------|------|
| Android | `androidInfo.id` | Android 8.0+ 的唯一设备标识 |
| iOS | `identifierForVendor` | 同一开发商应用共享的标识 |
| Web | Browser Fingerprint | 基于User-Agent + Platform生成的指纹 |

#### 请求方法

| 方法 | 用途 |
|------|------|
| GET | 获取资源 |
| POST | 创建资源 |
| PUT | 更新资源 (全量) |
| PATCH | 更新资源 (部分) |
| DELETE | 删除资源 |

### 1.3 响应规范

#### 响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "timestamp": 1700000000000
}
```

#### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| code | number | 业务状态码，0 表示成功 |
| message | string | 响应消息 |
| data | object | 响应数据 |
| timestamp | number | 服务器时间戳 (毫秒) |

#### 业务状态码

| 状态码 | 说明 |
|--------|------|
| 0 | 成功 |
| 1001 | 参数错误 |
| 1002 | 设备未注册 |
| 1003 | 资源不存在 |
| 1004 | 平台不支持 |
| 2001 | 用户不存在 |
| 3001 | 方案生成失败 |
| 3002 | 方案不存在 |
| 3003 | 方案数量已达上限 |
| 4001 | 第三方服务异常 |
| 4002 | AI服务超时 |
| 5001 | 服务器内部错误 |

### 1.4 分页规范

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码，默认 1 |
| pageSize | number | 否 | 每页数量，默认 20，最大 100 |

#### 响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 100,
      "totalPages": 5
    }
  },
  "timestamp": 1700000000000
}
```

---

## 二、用户模块 API

### 2.1 注册/获取用户

**接口描述**：根据设备标识获取或创建用户

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `POST /users/register` |
| 认证 | 否 |

**请求参数**：
```json
{
  "deviceId": "device_unique_identifier",
  "platform": "android",
  "nickname": "用户",
  "avatar": null,
  "deviceModel": "Pixel 8",
  "osVersion": "Android 14"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | string | 是 | 设备唯一标识 |
| platform | string | 是 | 平台: `android`, `ios`, `web` |
| nickname | string | 否 | 用户昵称，默认"用户" |
| avatar | string | 否 | 头像URL，默认系统头像 |
| deviceModel | string | 否 | 设备型号 |
| osVersion | string | 否 | 操作系统版本 |

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "user_123",
    "deviceId": "device_unique_identifier",
    "nickname": "用户",
    "avatar": "https://.../default_avatar.png",
    "platform": "android",
    "isNewUser": true,
    "createdAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

### 2.2 获取用户信息

**接口描述**：获取当前用户的详细信息

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /users/me` |
| 认证 | 设备ID |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "user_123",
    "deviceId": "device_unique_identifier",
    "nickname": "用户",
    "avatar": "https://.../default_avatar.png",
    "platform": "android",
    "schemeCount": 2,
    "createdAt": "2026-03-17T10:00:00Z",
    "updatedAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

### 2.3 更新用户信息

**接口描述**：更新用户基本信息

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `PATCH /users/me` |
| 认证 | 设备ID |

**请求参数**：
```json
{
  "nickname": "新昵称",
  "avatar": "https://..."
}
```

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "user_123",
    "nickname": "新昵称",
    "avatar": "https://...",
    "updatedAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

## 三、用户信息采集模块 API

### 3.1 保存用户信息

**接口描述**：保存用户填写的完整信息（基础信息、生活习惯、设备经验、审美偏好、品牌偏好）

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `POST /users/me/info` |
| 认证 | 设备ID |

**请求参数**：
```json
{
  "basicInfo": {
    "age": "26-30",
    "occupation": "上班族",
    "familyMembers": ["情侣"],
    "city": "北京"
  },
  "lifestyle": {
    "sleepPattern": "晚睡晚起",
    "homeActivities": ["休闲娱乐", "健身运动"],
    "entertainmentHabits": ["看电影追剧", "听音乐"]
  },
  "deviceExperience": {
    "knowledgeLevel": "用过一些",
    "usedDevices": ["智能音箱", "智能灯具"]
  },
  "aestheticPreference": {
    "decorStyle": "现代简约",
    "colorPreferences": ["白色系", "原木色"]
  },
  "brandPreference": {
    "preferredBrands": ["小米/米家", "华为"]
  }
}
```

**参数说明**：

| 参数路径 | 类型 | 必填 | 说明 |
|----------|------|------|------|
| basicInfo.age | string | 是 | 年龄段: `18-25`, `26-30`, `31-35`, `35+` |
| basicInfo.occupation | string | 是 | 职业: `学生`, `上班族`, `自由职业`, `其他` |
| basicInfo.familyMembers | string[] | 是 | 家庭成员: `独居`, `情侣`, `夫妻+孩子`, `与父母同住`, `其他` |
| basicInfo.city | string | 是 | 居住城市 |
| lifestyle.sleepPattern | string | 否 | 作息: `早睡早起`, `晚睡晚起`, `作息不规律` |
| lifestyle.homeActivities | string[] | 否 | 在家活动: `工作学习`, `休闲娱乐`, `健身运动`, `烹饪美食`, `其他` |
| lifestyle.entertainmentHabits | string[] | 否 | 娱乐习惯: `看电影追剧`, `听音乐`, `玩游戏`, `阅读`, `其他` |
| deviceExperience.knowledgeLevel | string | 否 | 了解程度: `完全不了解`, `听说过但没用过`, `用过一些`, `非常熟悉` |
| deviceExperience.usedDevices | string[] | 否 | 使用过的设备 |
| aestheticPreference.decorStyle | string | 否 | 装修风格: `现代简约`, `北欧风`, `日式`, `工业风`, `中式`, `其他` |
| aestheticPreference.colorPreferences | string[] | 否 | 颜色偏好 |
| brandPreference.preferredBrands | string[] | 否 | 品牌偏好 |

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "info_123",
    "userId": "user_123",
    "completedAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

### 3.2 获取用户信息

**接口描述**：获取当前用户已填写的信息

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /users/me/info` |
| 认证 | 设备ID |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "basicInfo": {
      "age": "26-30",
      "occupation": "上班族",
      "familyMembers": ["情侣"],
      "city": "北京"
    },
    "lifestyle": {
      "sleepPattern": "晚睡晚起",
      "homeActivities": ["休闲娱乐", "健身运动"],
      "entertainmentHabits": ["看电影追剧", "听音乐"]
    },
    "deviceExperience": {
      "knowledgeLevel": "用过一些",
      "usedDevices": ["智能音箱", "智能灯具"]
    },
    "aestheticPreference": {
      "decorStyle": "现代简约",
      "colorPreferences": ["白色系", "原木色"]
    },
    "brandPreference": {
      "preferredBrands": ["小米/米家", "华为"]
    },
    "isCompleted": true,
    "updatedAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

## 四、户型模块 API

### 4.1 保存户型信息

**接口描述**：保存用户的户型数据

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `POST /users/me/house-layout` |
| 认证 | 设备ID |

**请求参数**：
```json
{
  "houseType": "两居室",
  "totalArea": 85,
  "rooms": [
    {
      "name": "客厅",
      "area": 25,
      "specialNeeds": "需要智能灯光控制"
    },
    {
      "name": "主卧",
      "area": 18,
      "specialNeeds": ""
    },
    {
      "name": "厨房",
      "area": 8,
      "specialNeeds": "需要智能烟感"
    }
  ]
}
```

**参数说明**：

| 参数路径 | 类型 | 必填 | 说明 |
|----------|------|------|------|
| houseType | string | 是 | 房屋类型: `一居室`, `两居室`, `三居室`, `四居室及以上`, `复式/别墅` |
| totalArea | number | 是 | 建筑面积 (平方米) |
| rooms | array | 是 | 房间列表，至少1个 |
| rooms[].name | string | 是 | 房间名称: `客厅`, `主卧`, `次卧`, `书房`, `厨房`, `卫生间`, `阳台`, `其他` |
| rooms[].area | number | 是 | 房间面积 (平方米) |
| rooms[].specialNeeds | string | 否 | 特殊需求描述 |

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "layout_123",
    "userId": "user_123",
    "houseType": "两居室",
    "totalArea": 85,
    "roomCount": 3,
    "createdAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

### 4.2 获取户型信息

**接口描述**：获取当前用户的户型数据

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /users/me/house-layout` |
| 认证 | 设备ID |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "layout_123",
    "houseType": "两居室",
    "totalArea": 85,
    "rooms": [
      {
        "id": "room_1",
        "name": "客厅",
        "area": 25,
        "specialNeeds": "需要智能灯光控制"
      },
      {
        "id": "room_2",
        "name": "主卧",
        "area": 18,
        "specialNeeds": ""
      },
      {
        "id": "room_3",
        "name": "厨房",
        "area": 8,
        "specialNeeds": "需要智能烟感"
      }
    ],
    "createdAt": "2026-03-17T10:00:00Z",
    "updatedAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

## 五、方案模块 API

### 5.1 生成方案

**接口描述**：根据用户信息调用 AI 生成智能家居方案

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `POST /schemes/generate` |
| 认证 | 设备ID |
| 超时 | 30秒 |

**请求参数**：
```json
{
  "budget": 10000,
  "regenerate": false
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| budget | number | 是 | 预算金额 (元) |
| regenerate | boolean | 否 | 是否重新生成，默认 false |

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "scheme_123",
    "name": "智能家居方案 #1",
    "budget": 10000,
    "totalPrice": 8560,
    "status": "completed",
    "decorationGuide": {
      "summary": "整体布局建议...",
      "rooms": [
        {
          "name": "客厅",
          "layout": "建议在客厅入口处安装智能开关...",
          "devices": ["智能吸顶灯", "智能窗帘", "智能音箱"],
          "installationPoints": [
            "智能开关安装在入口墙面，高度1.3米",
            "智能窗帘电机安装于窗帘盒内"
          ],
          "notes": "注意预留电源插座位置"
        }
      ],
      "professionalAdvice": "建议优先考虑设备兼容性..."
    },
    "devices": [
      {
        "id": "device_1",
        "name": "小米智能吸顶灯",
        "brand": "小米/米家",
        "category": "照明",
        "price": 299,
        "quantity": 1,
        "description": "支持亮度调节、色温调节",
        "recommendReason": "性价比高，支持米家生态",
        "imageUrl": "https://...",
        "taobaoUrl": "https://..."
      }
    ],
    "createdAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

### 5.2 获取方案详情

**接口描述**：获取指定方案的详细信息

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /schemes/{schemeId}` |
| 认证 | 设备ID |

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| schemeId | string | 是 | 方案ID |

**请求参数**：无

**响应示例**：同 5.1 生成方案响应

---

### 5.3 获取我的方案列表

**接口描述**：获取当前用户的方案列表（最多3条）

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /users/me/schemes` |
| 认证 | 设备ID |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "scheme_123",
        "name": "智能家居方案 #1",
        "budget": 10000,
        "totalPrice": 8560,
        "deviceCount": 12,
        "createdAt": "2026-03-17T10:00:00Z"
      },
      {
        "id": "scheme_122",
        "name": "智能家居方案 #2",
        "budget": 15000,
        "totalPrice": 12300,
        "deviceCount": 18,
        "createdAt": "2026-03-16T10:00:00Z"
      }
    ],
    "total": 2,
    "maxAllowed": 3
  },
  "timestamp": 1700000000000
}
```

---

### 5.4 保存方案

**接口描述**：保存方案到用户的方案列表

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `POST /schemes/{schemeId}/save` |
| 认证 | 设备ID |

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| schemeId | string | 是 | 方案ID |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "scheme_123",
    "savedAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

**错误响应**：
```json
{
  "code": 3003,
  "message": "方案数量已达上限，最多保存3个方案",
  "data": null,
  "timestamp": 1700000000000
}
```

---

### 5.5 删除方案

**接口描述**：删除指定方案

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `DELETE /schemes/{schemeId}` |
| 认证 | 设备ID |

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| schemeId | string | 是 | 方案ID |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": null,
  "timestamp": 1700000000000
}
```

---

### 5.6 导出方案 PDF

**接口描述**：导出方案为 PDF 文档

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /schemes/{schemeId}/export` |
| 认证 | 设备ID |

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| schemeId | string | 是 | 方案ID |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "pdfUrl": "https://...",
    "fileName": "智能家居方案_20260317.pdf",
    "fileSize": 1024000,
    "expiresAt": "2026-03-17T11:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

## 六、设备模块 API

### 6.1 获取设备详情

**接口描述**：获取单个设备的详细信息

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /devices/{deviceId}` |
| 认证 | 设备ID |

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | string | 是 | 设备ID |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "device_1",
    "name": "小米智能吸顶灯",
    "brand": "小米/米家",
    "category": "照明",
    "price": 299,
    "originalPrice": 399,
    "description": "支持亮度调节、色温调节，可接入米家APP",
    "recommendReason": "性价比高，支持米家生态，安装简便",
    "features": [
      "亮度无级调节",
      "色温调节 2700K-6500K",
      "支持语音控制",
      "定时开关"
    ],
    "specifications": {
      "功率": "36W",
      "尺寸": "Φ450mm",
      "材质": "铝合金+亚克力"
    },
    "applicableScenes": ["客厅", "卧室", "书房"],
    "imageUrl": "https://...",
    "images": [
      "https://...",
      "https://..."
    ],
    "taobaoUrl": "https://...",
    "priceUpdatedAt": "2026-03-17T08:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

### 6.2 获取设备购买链接

**接口描述**：获取设备的淘宝购买链接

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /devices/{deviceId}/purchase-url` |
| 认证 | 设备ID |

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | string | 是 | 设备ID |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "purchaseUrl": "https://s.click.taobao.com/...",
    "price": 299,
    "coupon": {
      "value": 20,
      "description": "满300减20"
    },
    "expiresAt": "2026-03-18T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

### 6.3 搜索设备

**接口描述**：搜索设备（用于设备库管理）

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /devices` |
| 认证 | 设备ID |

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| category | string | 否 | 设备分类 |
| brand | string | 否 | 品牌 |
| minPrice | number | 否 | 最低价格 |
| maxPrice | number | 否 | 最高价格 |
| page | number | 否 | 页码，默认 1 |
| pageSize | number | 否 | 每页数量，默认 20 |

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "device_1",
        "name": "小米智能吸顶灯",
        "brand": "小米/米家",
        "category": "照明",
        "price": 299,
        "imageUrl": "https://..."
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 100,
      "totalPages": 5
    }
  },
  "timestamp": 1700000000000
}
```

---

## 七、反馈模块 API

### 7.1 提交方案评价

**接口描述**：用户对方案进行评价

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `POST /feedback/scheme-rating` |
| 认证 | 设备ID |

**请求参数**：
```json
{
  "schemeId": "scheme_123",
  "rating": 5,
  "content": "方案很专业，推荐设备性价比高"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| schemeId | string | 是 | 方案ID |
| rating | number | 是 | 评分 1-5 |
| content | string | 否 | 评价内容，最多500字 |

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "rating_123",
    "createdAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

### 7.2 提交意见反馈

**接口描述**：用户提交问题或建议

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `POST /feedback/suggestion` |
| 认证 | 设备ID |

**请求参数**：
```json
{
  "type": "功能建议",
  "content": "希望能增加设备对比功能",
  "contact": "user@example.com"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 是 | 类型: `功能建议`, `问题反馈`, `其他` |
| content | string | 是 | 反馈内容，最多1000字 |
| contact | string | 否 | 联系方式 |

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "feedback_123",
    "createdAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

### 7.3 提交数据纠错

**接口描述**：用户报告设备信息错误或价格异常

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `POST /feedback/data-correction` |
| 认证 | 设备ID |

**请求参数**：
```json
{
  "deviceId": "device_1",
  "errorType": "价格错误",
  "correctInfo": "实际价格应该是199元"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | string | 是 | 设备ID |
| errorType | string | 是 | 错误类型: `价格错误`, `信息错误`, `其他` |
| correctInfo | string | 是 | 正确信息 |

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "correction_123",
    "createdAt": "2026-03-17T10:00:00Z"
  },
  "timestamp": 1700000000000
}
```

---

## 八、通用接口 API

### 8.1 获取配置信息

**接口描述**：获取APP配置信息（字典数据、版本信息等）

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /config` |
| 认证 | 否 |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "version": {
      "latestVersion": "1.0.1",
      "minVersion": "1.0.0",
      "updateUrl": "https://...",
      "forceUpdate": false
    },
    "dictionaries": {
      "ageRanges": ["18-25", "26-30", "31-35", "35+"],
      "occupations": ["学生", "上班族", "自由职业", "其他"],
      "familyMembers": ["独居", "情侣", "夫妻+孩子", "与父母同住", "其他"],
      "sleepPatterns": ["早睡早起", "晚睡晚起", "作息不规律"],
      "homeActivities": ["工作学习", "休闲娱乐", "健身运动", "烹饪美食", "其他"],
      "entertainmentHabits": ["看电影追剧", "听音乐", "玩游戏", "阅读", "其他"],
      "knowledgeLevels": ["完全不了解", "听说过但没用过", "用过一些", "非常熟悉"],
      "decorStyles": ["现代简约", "北欧风", "日式", "工业风", "中式", "其他"],
      "colorPreferences": ["白色系", "灰色系", "原木色", "黑色系", "彩色系"],
      "brands": ["小米/米家", "华为", "天猫精灵", "小度", "Apple HomeKit", "其他", "无偏好"],
      "houseTypes": ["一居室", "两居室", "三居室", "四居室及以上", "复式/别墅"],
      "roomTypes": ["客厅", "主卧", "次卧", "书房", "厨房", "卫生间", "阳台", "其他"],
      "deviceCategories": ["照明", "安防", "环境", "影音", "家电", "其他"]
    },
    "platforms": {
      "android": {
        "minVersion": "5.0",
        "targetVersion": "14"
      },
      "ios": {
        "minVersion": "11.0",
        "targetVersion": "17"
      },
      "web": {
        "supportedBrowsers": ["Chrome 80+", "Safari 13+", "Firefox 75+", "Edge 80+"]
      }
    }
  },
  "timestamp": 1700000000000
}
```

---

### 8.2 健康检查

**接口描述**：检查服务健康状态

**请求信息**：
| 项目 | 内容 |
|------|------|
| URL | `GET /health` |
| 认证 | 否 |

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "status": "healthy",
    "services": {
      "database": "ok",
      "redis": "ok",
      "deepseek": "ok",
      "taobao": "ok"
    },
    "timestamp": 1700000000000
  },
  "timestamp": 1700000000000
}
```

---

## 九、错误处理

### 9.1 错误响应格式

```json
{
  "code": 1001,
  "message": "参数错误",
  "data": {
    "errors": [
      {
        "field": "budget",
        "message": "预算金额必须大于0"
      }
    ]
  },
  "timestamp": 1700000000000
}
```

### 9.2 常见错误码

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| 1001 | 400 | 参数错误 |
| 1002 | 401 | 设备未注册 |
| 1003 | 404 | 资源不存在 |
| 1004 | 400 | 平台不支持 |
| 2001 | 404 | 用户不存在 |
| 3001 | 500 | 方案生成失败 |
| 3002 | 404 | 方案不存在 |
| 3003 | 400 | 方案数量已达上限 |
| 4001 | 502 | 第三方服务异常 |
| 4002 | 504 | AI服务超时 |
| 5001 | 500 | 服务器内部错误 |

---

## 十、API 调用示例

### 10.1 Flutter 客户端调用示例

```dart
import 'package:dio/dio.dart';

class ApiClient {
  final Dio _dio;
  final DeviceIdService _deviceIdService;
  
  ApiClient(this._dio, this._deviceIdService);
  
  Future<Map<String, dynamic>> generateScheme({
    required int budget,
    bool regenerate = false,
  }) async {
    final deviceId = await _deviceIdService.getDeviceId();
    final platform = _deviceIdService.getPlatform();
    
    final response = await _dio.post(
      '/schemes/generate',
      data: {
        'budget': budget,
        'regenerate': regenerate,
      },
      options: Options(
        headers: {
          'X-Device-ID': deviceId,
          'X-Platform': platform,
        },
      ),
    );
    
    return response.data['data'];
  }
}
```

### 10.2 Web 客户端调用示例

```typescript
const apiClient = {
  async generateScheme(budget: number, regenerate = false) {
    const deviceId = await getDeviceFingerprint();
    
    const response = await fetch('https://api.smarthome.com/v1/schemes/generate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Device-ID': deviceId,
        'X-Platform': 'web',
      },
      body: JSON.stringify({ budget, regenerate }),
    });
    
    const data = await response.json();
    return data.data;
  }
};
```

---

## 十一、接口版本管理

### 11.1 版本策略

- API路径包含版本号: `/v1/`, `/v2/`
- 向后兼容的修改不增加版本号
- 破坏性修改需要增加版本号
- 旧版本API保留至少6个月

### 11.2 变更日志

| 版本 | 日期 | 变更内容 |
|------|------|---------|
| V1.2 | 2026-03-17 | 增加跨平台支持，X-Platform支持ios/web |
| V1.1 | 2026-03-15 | 移除微信登录，改用设备ID认证 |
| V1.0 | 2026-03-14 | 初始版本 |
