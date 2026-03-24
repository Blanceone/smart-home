# 构建问题报告

**构建时间**: 2026-03-24  
**构建 ID**: 23475744005  
**构建状态**: ❌ 失败

---

## 一、构建环境

| 项目 | 值 |
|------|-----|
| 工作流 | build-mobile.yml |
| Flutter 版本 | 3.24.0 |
| Java 版本 | 17 |
| 运行环境 | ubuntu-latest |

---

## 二、构建失败原因

### 2.1 编译错误

**错误类型**: Dart 编译错误  
**错误文件**: `lib/modules/questionnaire/questionnaire_page.dart`

**错误详情**:

```
lib/modules/questionnaire/questionnaire_page.dart:383:58: Error: The getter 'name' isn't defined for the class 'Brand'.
 - 'Brand' is from 'package:smart_home/shared/models/product.dart' ('lib/shared/models/product.dart').
Try correcting the name to the name of an existing getter, or defining a getter or field named 'name'.

final isSelected = _preferences.excludedBrands?.contains(brand.name) ?? false;
                                                            ^^^^

lib/modules/questionnaire/questionnaire_page.dart:387:37: Error: The getter 'name' isn't defined for the class 'Brand'.
 - 'Brand' is from 'package:smart_home/shared/models/product.dart' ('lib/shared/models/product.dart').
Try correcting the name to the name of an existing getter, or defining a getter or field named 'name'.

label: Text(brand.name),
                  ^^^^

lib/modules/questionnaire/questionnaire_page.dart:393:64: Error: The getter 'name' isn't defined for the class 'Brand'.
 - 'Brand' is from 'package:smart_home/shared/models/product.dart' ('lib/shared/models/product.dart').
Try correcting the name to the name of an existing getter, or defining a getter or field named 'name'.

_preferences.excludedBrands!.add(brand.name);
                                          ^^^^

lib/modules/questionnaire/questionnaire_page.dart:395:67: Error: The getter 'name' isn't defined for the class 'Brand'.
 - 'Brand' is from 'package:smart_home/shared/models/product.dart' ('lib/shared/models/product.dart').
Try correcting the name to the name of an existing getter, or defining a getter or field named 'name'.

_preferences.excludedBrands!.remove(brand.name);
                                             ^^^^
```

---

## 三、问题分析

### 3.1 根本原因

在 `lib/shared/models/product.dart` 中定义的 `Brand` 类缺少 `name` 属性，但在 `questionnaire_page.dart` 中尝试访问 `brand.name`。

### 3.2 影响范围

- 文件: `lib/modules/questionnaire/questionnaire_page.dart`
- 行号: 383, 387, 393, 395
- 影响功能: 品牌选择功能

---

## 四、修复建议

### 方案 1: 在 Brand 类中添加 name 属性

**文件**: `lib/shared/models/product.dart`

```dart
class Brand {
  final int id;
  final String name;  // 确保此属性存在
  final String? logoUrl;
  final String? description;

  Brand({
    required this.id,
    required this.name,
    this.logoUrl,
    this.description,
  });

  factory Brand.fromJson(Map<String, dynamic> json) {
    return Brand(
      id: json['id'],
      name: json['name'] ?? '',  // 确保 name 被正确解析
      logoUrl: json['logo_url'],
      description: json['description'],
    );
  }
}
```

### 方案 2: 检查 Brand 类的实际属性名

如果 Brand 类使用的是其他属性名（如 `brandName`），则需要修改 `questionnaire_page.dart` 中的引用。

---

## 五、待开发确认

1. **Brand 类的正确属性名是什么？**
   - 需要确认 `Brand` 类是否应该有 `name` 属性
   - 或者应该使用其他属性名（如 `brandName`、`title` 等）

2. **API 返回的品牌数据格式是什么？**
   - 需要确认后端 API 返回的品牌数据字段名

---

## 六、构建日志链接

- GitHub Actions: https://github.com/Blanceone/smart-home/actions/runs/23475744005

---

*报告生成时间: 2026-03-24*
