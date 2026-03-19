# UI 设计规范文档

## 文档信息

| 项目名称 | 智能家居方案定制APP |
|---------|-------------------|
| 文档版本 | V1.0 |
| 创建日期 | 2026-03-18 |
| 设计师 | UI/UX Designer AI |
| 文档状态 | 初稿完成 |

---

## 一、设计原则

### 1.1 核心设计原则

- **简洁现代**：采用简洁的设计语言，突出内容，减少视觉干扰
- **科技感**：通过渐变色和现代元素体现智能家居的科技属性
- **易用性**：确保各年龄段用户都能轻松使用
- **一致性**：所有页面遵循统一的设计规范

### 1.2 目标用户适配

- 年龄范围：18-35岁年轻用户
- 设计风格：现代、简约、科技感
- 交互方式：触摸为主，简洁直观

---

## 二、配色方案

### 2.1 主色调

| 颜色名称 | 色值 | 使用场景 |
|---------|------|----------|
| Primary (主色) | #4F46E5 | 主要按钮、链接、选中状态 |
| PrimaryLight | #818CF8 | 渐变高亮、hover状态 |
| PrimaryDark | #3730A3 | 深色背景、文字强调 |

### 2.2 辅助色

| 颜色名称 | 色值 | 使用场景 |
|---------|------|----------|
| Secondary (辅助色) | #10B981 | 成功状态、确认操作 |
| Accent (强调色) | #F59E0B | 警示、预算显示 |

### 2.3 中性色

| 颜色名称 | 色值 | 使用场景 |
|---------|------|----------|
| Background | #F8FAFC | 页面背景 |
| Surface | #FFFFFF | 卡片、弹窗背景 |
| TextPrimary | #1E293B | 主要文字 |
| TextSecondary | #64748B | 次要文字、描述 |
| TextTertiary | #94A3B8 | 占位符、禁用文字 |
| Divider | #E2E8F0 | 分割线 |
| Border | #CBD5E1 | 边框 |

### 2.4 状态色

| 颜色名称 | 色值 | 使用场景 |
|---------|------|----------|
| Success | #10B981 | 成功、操作完成 |
| Warning | #F59E0B | 警告、预算提醒 |
| Error | #EF4444 | 错误、删除操作 |
| Info | #3B82F6 | 信息提示 |

---

## 三、字体规范

### 3.1 字号层级

| 样式名称 | 字号 | 行高 | 字重 | 使用场景 |
|---------|------|------|------|----------|
| displayLarge | 32sp | 40sp | Bold | 页面大标题 |
| displayMedium | 28sp | 36sp | Bold | 区块标题 |
| displaySmall | 24sp | 32sp | SemiBold | 卡片标题 |
| headlineLarge | 22sp | 28sp | SemiBold | 章节标题 |
| headlineMedium | 20sp | 26sp | SemiBold | 模块标题 |
| headlineSmall | 18sp | 24sp | Medium | 子标题 |
| titleLarge | 16sp | 22sp | Medium | 列表标题 |
| titleMedium | 14sp | 20sp | Medium | 标签文字 |
| bodyLarge | 16sp | 24sp | Regular | 正文内容 |
| bodyMedium | 14sp | 20sp | Regular | 次要内容 |
| bodySmall | 12sp | 16sp | Regular | 辅助说明 |
| labelLarge | 14sp | 20sp | Medium | 按钮文字 |
| labelMedium | 12sp | 16sp | Medium | 辅助标签 |

### 3.2 字体颜色优先级

1. **高优先级**：TextPrimary (#1E293B) - 重要信息
2. **中优先级**：TextSecondary (#64748B) - 说明文字
3. **低优先级**：TextTertiary (#94A3B8) - 占位符、禁用状态

---

## 四、间距系统

### 4.1 基础间距

| 名称 | 数值 | 使用场景 |
|------|------|----------|
| spacingXs | 4dp | 紧凑元素间距 |
| spacingSm | 8dp | 组件内部间距 |
| spacingMd | 16dp | 组件间标准间距 |
| spacingLg | 24dp | 区块间间距 |
| spacingXl | 32dp | 页面分区间距 |
| spacingXxl | 48dp | 大区块分隔 |

### 4.2 页面边距

| 名称 | 数值 | 使用场景 |
|------|------|----------|
| horizontalPadding | 20dp | 页面左右内边距 |
| verticalPadding | 16dp | 页面上下内边距 |

### 4.3 圆角规范

| 名称 | 数值 | 使用场景 |
|------|------|----------|
| cornerSmall | 8dp | 小元素、标签 |
| cornerMedium | 12dp | 按钮、输入框 |
| cornerLarge | 16dp | 卡片、弹窗 |
| cornerXLarge | 24dp | 大面积圆角 |

---

## 五、组件设计

### 5.1 按钮 (Button)

#### 主要按钮 (Primary)

```
高度: 48dp (Medium), 56dp (Large), 36dp (Small)
圆角: 12dp
背景: Primary (#4F46E5)
文字: White, 14sp, Medium
```

#### 次要按钮 (Secondary)

```
高度: 48dp
圆角: 12dp
背景: Secondary (#10B981)
文字: White, 14sp, Medium
```

#### 轮廓按钮 (Outline)

```
高度: 48dp
圆角: 12dp
边框: 1.5dp, Border (#CBD5E1)
背景: Transparent
文字: TextPrimary, 14sp, Medium
```

#### 文字按钮 (Text)

```
高度: 36dp
背景: Transparent
文字: Primary, 14sp, Medium
```

### 5.2 输入框 (InputField)

```
高度: 56dp
圆角: 12dp
边框: 1dp, Border (#CBD5E1)
聚焦边框: 2dp, Primary (#4F46E1)
填充: Surface (#FFFFFF)
标签: 12sp, TextSecondary
输入文字: 16sp, TextPrimary
占位符: 16sp, TextTertiary
错误状态: 边框 Error (#EF4444)
```

### 5.3 卡片 (Card)

```
圆角: 16dp
背景: Surface (#FFFFFF)
阴影: elevation 2dp
内边距: 16dp
悬停阴影: elevation 4dp
```

### 5.4 标签 (Chip)

```
高度: 32dp
圆角: 8dp (Small), 16dp (Full)
背景: SurfaceVariant (#F1F5F9)
选中背景: Primary (#4F46E5)
文字: 14sp, TextSecondary
选中文字: White
```

### 5.5 底部导航 (BottomNavigation)

```
高度: 80dp (含安全区)
背景: Surface (#FFFFFF)
图标大小: 24dp
选中图标: Primary (#4F46E5)
未选图标: TextTertiary (#94A3B8)
标签: 12sp
```

### 5.6 顶部导航栏 (TopAppBar)

```
高度: 56dp
背景: Surface (#FFFFFF)
标题: 18sp, TextPrimary, SemiBold
返回图标: 24dp, TextPrimary
```

---

## 六、页面布局规范

### 6.1 通用页面结构

```
┌─────────────────────────────┐
│        StatusBar           │ <- 状态栏
├─────────────────────────────┤
│     TopAppBar (56dp)       │ <- 顶部导航
├─────────────────────────────┤
│                             │
│     Content Area           │ <- 内容区域
│     (可滚动)               │
│                             │
├─────────────────────────────┤
│     BottomNav (80dp)       │ <- 底部导航
└─────────────────────────────┘
```

### 6.2 列表页布局

```
┌─────────────────────────────┐
│ TopAppBar: 标题 + 操作     │
├─────────────────────────────┤
│ SearchBar (可选)           │ <- 搜索栏
├─────────────────────────────┤
│ FilterChips                │ <- 筛选标签
├─────────────────────────────┤
│ ┌─────────────────────────┐│
│ │ Card: Item 1            ││ <- 列表项
│ └─────────────────────────┘│
│ ┌─────────────────────────┐│
│ │ Card: Item 2            ││
│ └─────────────────────────┘│
│ ┌─────────────────────────┐│
│ │ Card: Item 3            ││
│ └─────────────────────────┘│
│           ...              │
├─────────────────────────────┤
│ EmptyState (可选)          │ <- 空状态
└─────────────────────────────┘
```

### 6.3 表单页布局

```
┌─────────────────────────────┐
│ TopAppBar: 返回 + 标题      │
├─────────────────────────────┤
│ StepIndicator              │ <- 步骤指示器
├─────────────────────────────┤
│ ┌─────────────────────────┐│
│ │ Section Title           ││ <- 区块标题
│ ├─────────────────────────┤│
│ │ InputField / ChipGroup  ││ <- 输入组件
│ └─────────────────────────┘│
│ ┌─────────────────────────┐│
│ │ Section Title           ││
│ ├─────────────────────────┤│
│ │ InputField / ChipGroup  ││
│ └─────────────────────────┘│
│           ...              │
├─────────────────────────────┤
│ Button: 下一步 / 提交       │ <- 操作按钮
└─────────────────────────────┘
```

### 6.4 详情页布局

```
┌─────────────────────────────┐
│ TopAppBar: 返回 + 标题      │
│ + 分享 / 更多操作          │
├─────────────────────────────┤
│ Header Image / Preview     │ <- 头部预览
├─────────────────────────────┤
│ Title + Price              │ <- 标题价格
├─────────────────────────────┤
│ TabLayout                  │ <- 标签切换
│ (概览 / 详情 / 评价)       │
├─────────────────────────────┤
│                            │
│ TabContent                 │ <- 标签内容
│                            │
├─────────────────────────────┤
│ ActionBar                  │ <- 操作栏
│ (保存 / 购买 / 评价)       │
└─────────────────────────────┘
```

---

## 七、交互规范

### 7.1 手势操作

| 操作 | 触发条件 | 反馈 |
|------|----------|------|
| 点击 | 单指轻触 | 波纹效果、状态变化 |
| 长按 | 长按500ms | 震动反馈、上下文菜单 |
| 滑动 | 水平/垂直滑动 | 页面切换、列表滚动 |
| 下拉 | 顶部下拉 | 刷新动画 |
| 上拉 | 底部上拉 | 加载更多 |

### 7.2 动画规范

| 动画类型 | 时长 | 缓动函数 |
|----------|------|----------|
| 页面切换 | 300ms | easeInOut |
| 元素显现 | 200ms | easeOut |
| 按钮点击 | 100ms | easeIn |
| 加载动画 | 持续 | linear |
| 骨架屏 | 1500ms | linear (循环) |

### 7.3 加载状态

- **骨架屏**: 灰色渐变动画，内容区域占位
- **loading**: 圆形旋转指示器
- **空状态**: 插画 + 提示文字 + 操作按钮

---

## 八、无障碍规范

### 8.1 触摸区域

- 最小触摸区域: 48dp × 48dp
- 按钮内边距: 至少12dp

### 8.2 对比度

- 文字与背景对比度: 至少4.5:1
- 大号文字对比度: 至少3:1

### 8.3 内容描述

- 图片需提供contentDescription
- 图标按钮需提供contentDescription

---

## 九、组件清单

### 9.1 基础组件

| 组件名称 | 文件路径 | 状态 |
|----------|----------|------|
| SmartButton | common/components/Button.kt | ✅ 已实现 |
| SmartTextField | common/components/InputField.kt | ✅ 已实现 |
| SmartCard | common/components/Card.kt | ✅ 已实现 |
| SmartChip | common/components/Chip.kt | ✅ 已实现 |
| SmartDialog | common/components/Dialog.kt | ✅ 已实现 |
| LoadingIndicator | common/components/Loading.kt | ✅ 已实现 |
| EmptyState | common/components/EmptyState.kt | ✅ 已实现 |
| SmartImage | common/components/Image.kt | ✅ 已实现 |
| StepIndicator | common/components/StepIndicator.kt | ✅ 已实现 |
| BottomNavBar | common/components/Navigation.kt | ✅ 已实现 |

### 9.2 页面组件

| 组件名称 | 文件路径 | 状态 |
|----------|----------|------|
| SplashScreen | splash/SplashScreen.kt | ✅ 已实现 |
| HomeScreen | home/HomeScreen.kt | ✅ 已实现 |
| LoginScreen | login/LoginScreen.kt | ✅ 已实现 |
| InfoCollectionScreen | info/InfoCollectionScreen.kt | ✅ 已实现 |
| BasicInfoScreen | info/BasicInfoScreen.kt | ✅ 已实现 |
| LifestyleScreen | info/LifestyleScreen.kt | ✅ 已实现 |
| DeviceExperienceScreen | info/DeviceExperienceScreen.kt | ✅ 已实现 |
| AestheticPreferenceScreen | info/AestheticPreferenceScreen.kt | ✅ 已实现 |
| BrandPreferenceScreen | info/BrandPreferenceScreen.kt | ✅ 已实现 |
| HouseLayoutScreen | info/HouseLayoutScreen.kt | ✅ 已实现 |
| BudgetScreen | info/BudgetScreen.kt | ✅ 已实现 |
| GeneratingScreen | scheme/GeneratingScreen.kt | ✅ 已实现 |
| SchemeDetailScreen | scheme/SchemeDetailScreen.kt | ✅ 已实现 |
| MySchemesScreen | myschemes/MySchemesScreen.kt | ✅ 已实现 |
| DeviceDetailScreen | device/DeviceDetailScreen.kt | ✅ 已实现 |
| FeedbackScreen | feedback/FeedbackScreen.kt | ✅ 已实现 |
| ProfileScreen | profile/ProfileScreen.kt | ✅ 已实现 |

---

## 十、设计资源

### 10.1 图标库

- Material Icons (主要)
- 自定义智能家居相关图标

### 10.2 图片资源

- 占位图: 灰色渐变骨架屏
- 空状态插画: 简约线条风格
- 设备图片: 来自API返回

### 10.3 渐变方案

```kotlin
val GradientPrimary = listOf(Primary, PrimaryLight)
val GradientSecondary = listOf(Secondary, SecondaryLight)
val GradientAccent = listOf(Accent, AccentLight)
val GradientBackground = listOf(Background, BackgroundDark)
```
