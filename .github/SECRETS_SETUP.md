# GitHub Actions CI/CD 配置指南

## 流水线说明

本项目包含以下 GitHub Actions 工作流：

| 工作流 | 触发条件 | 功能 |
|--------|----------|------|
| `build-mobile.yml` | push 到 main/master、tag、手动触发 | 构建 Android APK 和 iOS IPA |
| `deploy-backend.yml` | push 到 main/master (backend 目录变更) | 部署后端服务 |

---

## 必需的 GitHub Secrets 配置

### 服务器部署 Secrets (必需)

在 GitHub 仓库设置中配置：`Settings` → `Secrets and variables` → `Actions` → `New repository secret`

| Secret 名称 | 说明 | 示例值 |
|-------------|------|--------|
| `SERVER_HOST` | 服务器 IP 地址 | `8.137.174.58` |
| `SERVER_USER` | SSH 用户名 | `root` |
| `SERVER_SSH_KEY` | SSH 私钥内容 | 见下方说明 |

#### 获取 SSH 私钥内容

```bash
# Windows PowerShell
Get-Content "C:\Users\13979\Desktop\notes\lxh的秘钥.pem" | Set-Clipboard

# 或直接查看
cat "C:\Users\13979\Desktop\notes\lxh的秘钥.pem"
```

将完整内容（包括 `-----BEGIN OPENSSH PRIVATE KEY-----` 和 `-----END OPENSSH PRIVATE KEY-----`）粘贴到 `SERVER_SSH_KEY`。

---

### iOS 签名 Secrets (可选，iOS 构建必需)

如果要构建 iOS IPA，需要配置以下 Secrets：

| Secret 名称 | 说明 |
|-------------|------|
| `IOS_CERTIFICATE_BASE64` | Apple 开发者证书 (.p12) 的 Base64 编码 |
| `IOS_CERTIFICATE_PASSWORD` | 证书密码 |
| `IOS_PROVISIONING_PROFILE_BASE64` | Provisioning Profile (.mobileprovision) 的 Base64 编码 |
| `KEYCHAIN_PASSWORD` | 临时钥匙串密码（可随机生成） |

#### 生成证书 Base64

```bash
# 证书
base64 -i YourCertificate.p12 | pbcopy

# Provisioning Profile
base64 -i YourProfile.mobileprovision | pbcopy
```

---

## 触发构建

### 自动触发

- **推送到 main/master 分支**：自动构建并部署
- **创建 tag (v*)**：自动构建并创建 GitHub Release

### 手动触发

1. 进入 GitHub 仓库
2. 点击 `Actions` 标签
3. 选择工作流
4. 点击 `Run workflow`

---

## 产物下载

### 服务器下载

- Android APK: `http://8.137.174.58/downloads/smart-home.apk`
- iOS IPA: `http://8.137.174.58/downloads/smart-home.ipa` (需配置 iOS 签名)

### GitHub Release

创建 tag 后，产物会自动上传到 GitHub Release：
```
git tag v1.0.0
git push origin v1.0.0
```

---

## 工作流文件位置

```
.github/
└── workflows/
    ├── build-mobile.yml    # 移动端构建
    └── deploy-backend.yml  # 后端部署
```

---

## 注意事项

1. **iOS 构建**：需要 Apple Developer 账号（$99/年）
2. **Android 签名**：当前使用 debug 签名，生产环境建议配置正式签名
3. **服务器权限**：确保 SSH 用户有 `/var/www/downloads` 目录的写入权限
