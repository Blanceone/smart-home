# 构建问题报告

**构建时间**: 2026-03-24  
**构建 ID**: 23476535967  
**构建状态**: ❌ 失败

---

## 一、构建环境

| 项目 | 值 |
|------|-----|
| 工作流 | build-mobile.yml |
| Flutter 版本 | 3.24.0 |
| Java 版本 | 17 |
| 运行环境 | ubuntu-latest (Android) / macos-latest (iOS) |

---

## 二、构建失败原因

### 2.1 Android 构建错误

**错误类型**: Dart 编译错误  
**错误文件**: `lib/modules/questionnaire/questionnaire_page.dart`

**错误详情**:

```
lib/modules/questionnaire/questionnaire_page.dart:462:37: Error: The method 'withValues' isn't defined for the class 'Color'.
 - 'Color' is from 'dart:ui'.
Try correcting the name to the name of an existing method, or defining a method named 'withValues'.

selectedColor: AppColors.error.withValues(alpha: 0.2),
                            ^^^^^^^^^^

color: isSelected ? color.withValues(alpha: 0.1) : Colors.white,
                        ^^^^^^^^^^
```

### 2.2 iOS 构建错误

**错误类型**: 配置缺失  
**错误详情**:

```
Expected ios/Runner.xcodeproj but this file is missing.
Application not configured for iOS
```

---

## 三、问题分析

### 3.1 Android 问题分析

**根本原因**: `Color.withValues()` 方法在 Flutter 3.24.0 中不存在

**影响范围**:
- 文件: `lib/modules/questionnaire/questionnaire_page.dart`
- 行号: 462
- 影响功能: 品牌选择 UI

**版本兼容性**: `withValues()` 方法是在 Flutter 3.27+ 中引入的新 API

### 3.2 iOS 问题分析

**根本原因**: 项目未配置 iOS 平台支持

**影响范围**: iOS 构建完全失败

---

## 四、修复建议

### 4.1 Android 修复方案

**方案 1: 使用 withOpacity 替代 withValues（推荐）**

```dart
// 修改前
selectedColor: AppColors.error.withValues(alpha: 0.2),
color: isSelected ? color.withValues(alpha: 0.1) : Colors.white,

// 修改后
selectedColor: AppColors.error.withOpacity(0.2),
color: isSelected ? color.withOpacity(0.1) : Colors.white,
```

**方案 2: 升级 Flutter 版本到 3.27+**

修改 `.github/workflows/build-mobile.yml`:
```yaml
env:
  FLUTTER_VERSION: '3.27.0'
```

### 4.2 iOS 修复方案

**需要在项目中初始化 iOS 平台支持**:

```bash
cd frontend
flutter create --platforms=ios .
```

或者暂时禁用 iOS 构建:
```yaml
# 在工作流中设置 build_ios=false
gh workflow run build-mobile.yml -f build_android=true -f build_ios=false
```

---

## 五、历史构建记录

| 构建 ID | 时间 | 状态 | 失败原因 |
|---------|------|------|----------|
| 23476535967 | 2026-03-24 | ❌ | withValues 方法不存在 |
| 23476423456 | 2026-03-24 | ❌ | Brand.name 属性不存在 |
| 23475744005 | 2026-03-24 | ❌ | Brand.name 属性不存在 |

---

## 六、构建日志链接

- 最新构建: https://github.com/Blanceone/smart-home/actions/runs/23476535967

---

*报告更新时间: 2026-03-24*
