# 构建问题报告

**构建时间**: 2026-03-24  
**构建 ID**: 23493439939  
**构建状态**: ❌ Android 失败

---

## 一、构建环境

| 项目 | 值 |
|------|-----|
| 工作流 | build-mobile.yml |
| Flutter 版本 | 3.24.0 |
| Java 版本 | 17 |
| 运行环境 | ubuntu-latest |

---

## 二、构建结果

### 2.1 Android 构建

**状态**: ❌ 失败  
**构建时间**: 2分7秒  

**失败原因**:
```
lib/core/services/logger_service.dart:227:50: Error: Member not found: 'MultipartFile.fromFile'.
```

**问题分析**:
- `http.MultipartFile.fromFile` 方法在 `http` 包中不存在
- 需要使用 `http.MultipartFile.fromPath` 或其他方式上传文件

**修复建议**:
将 `http.MultipartFile.fromFile` 改为 `http.MultipartFile.fromPath`

---

## 三、历史构建记录

| 构建 ID | 时间 | 状态 | 失败原因 |
|---------|------|------|----------|
| 23493439939 | 2026-03-24 | ❌ | MultipartFile.fromFile 方法不存在 |
| 23479379752 | 2026-03-24 | ✅ Android 成功 | iOS 未配置（无需部署） |
| 23478725914 | 2026-03-24 | ❌ | getDefaultQuestions 方法不存在 |
| 23477914403 | 2026-03-24 | ❌ | double.tryParse 类型不匹配 |
| 23476535967 | 2026-03-24 | ❌ | withValues 方法不存在 |
| 23476423456 | 2026-03-24 | ❌ | Brand.name 属性不存在 |

---

## 四、构建日志链接

- 最新构建: https://github.com/Blanceone/smart-home/actions/runs/23493439939

---

*报告更新时间: 2026-03-24*
