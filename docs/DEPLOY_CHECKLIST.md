# 部署准备清单

## 当前状态

| 项目 | 状态 | 说明 |
|------|------|------|
| 代码 | ✅ 已完成 | 功能验收通过 |
| 部署配置 | ✅ 已完成 | Docker、CI/CD、Nginx 配置已生成 |
| Git | ❌ 未安装 | 需要安装 Git |
| GitHub 仓库 | ❌ 未创建 | 需要创建仓库并推送代码 |
| 服务器 | ❌ 未购买 | 需要购买云服务器 |
| API 密钥 | ⚠️ 部分获取 | 需要确认并补充 |
| 域名 | ⚠️ 已购买 | 需要配置解析 |

---

## 第一步：安装必要工具

### 1. 安装 Git

**Windows 系统：**
1. 访问 https://git-scm.com/download/win
2. 下载并安装 Git
3. 安装完成后重启终端

**验证安装：**
```bash
git --version
```

### 2. 配置 Git 用户信息
```bash
git config --global user.name "您的名字"
git config --global user.email "您的邮箱"
```

---

## 第二步：创建 GitHub 仓库

### 1. 创建新仓库
1. 登录 GitHub (https://github.com)
2. 点击右上角 "+" → "New repository"
3. 填写信息：
   - Repository name: `smart-home`
   - Description: `智能家居方案定制APP`
   - 选择 **Private** (私有仓库)
4. 点击 "Create repository"

### 2. 推送代码到 GitHub
```bash
# 在项目目录执行
cd d:\work\ai\smart_home_deg

# 初始化 Git
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: 智能家居方案定制APP"

# 添加远程仓库（替换为您的用户名）
git remote add origin https://github.com/YOUR_USERNAME/smart-home.git

# 推送到 GitHub
git branch -M main
git push -u origin main
```

---

## 第三步：购买云服务器

### 推荐配置

| 配置项 | 最低要求 | 推荐配置 |
|--------|---------|---------|
| CPU | 2核 | 4核 |
| 内存 | 4GB | 8GB |
| 硬盘 | 40GB SSD | 80GB SSD |
| 带宽 | 5Mbps | 10Mbps |
| 系统 | Ubuntu 22.04 LTS | Ubuntu 22.04 LTS |

### 国内云服务商推荐

| 服务商 | 入口链接 | 特点 |
|--------|---------|------|
| 阿里云 | https://www.aliyun.com | 国内最大，稳定可靠 |
| 腾讯云 | https://cloud.tencent.com | 性价比较高 |
| 华为云 | https://www.huaweicloud.com | 企业级服务 |

### 购买步骤（以阿里云为例）

1. 访问阿里云官网并登录
2. 进入 "云服务器 ECS" 产品页
3. 选择配置：
   - 地域：选择离用户最近的区域
   - 实例规格：2核4GB 或更高
   - 镜像：Ubuntu 22.04 64位
   - 存储：40GB 高效云盘
   - 网络：按使用流量计费，带宽 5Mbps
4. 设置密码（记住这个密码，用于 SSH 登录）
5. 确认订单并支付

### 购买后获取信息

记录以下信息：
- [ ] 服务器公网 IP：`_______________`
- [ ] SSH 用户名：`root` 或 `ubuntu`
- [ ] SSH 密码：`_______________`

---

## 第四步：申请 API 密钥

### 1. DeepSeek API（AI 服务）

**申请步骤：**
1. 访问 https://platform.deepseek.com
2. 注册并登录账号
3. 进入 "API Keys" 页面
4. 点击 "Create API Key"
5. 复制并保存 API Key

**记录信息：**
- [ ] DeepSeek API Key：`sk-________________________________`

### 2. 淘宝开放平台（商品 API）

**申请步骤：**
1. 访问 https://open.taobao.com
2. 使用淘宝账号登录
3. 进入 "控制台" → "应用管理"
4. 创建应用并获取：
   - App Key
   - App Secret

**记录信息：**
- [ ] 淘宝 App Key：`_______________`
- [ ] 淘宝 App Secret：`_______________`

---

## 第五步：配置域名解析

### 1. 登录域名管理控制台
（以阿里云为例）
1. 登录阿里云
2. 进入 "域名" → "解析设置"

### 2. 添加解析记录

| 记录类型 | 主机记录 | 记录值 | 说明 |
|---------|---------|--------|------|
| A | api | 服务器IP | API 服务 |
| A | www | 服务器IP | Web 前端 |
| A | @ | 服务器IP | 主域名 |

### 3. 等待生效
- DNS 解析通常需要 10分钟-2小时 生效
- 可用 `ping api.您的域名.com` 测试是否生效

---

## 第六步：配置 GitHub Secrets

在 GitHub 仓库中配置以下 Secrets：

1. 进入仓库 → Settings → Secrets and variables → Actions
2. 点击 "New repository secret" 添加以下内容：

| Secret 名称 | 值 | 说明 |
|------------|-----|------|
| SERVER_HOST | 您的服务器IP | 如：123.45.67.89 |
| SERVER_USER | SSH用户名 | root 或 ubuntu |
| SSH_PRIVATE_KEY | SSH私钥内容 | 见下方生成方法 |
| DEEPSEEK_API_KEY | DeepSeek密钥 | sk-xxx |
| TAOBAO_APP_KEY | 淘宝App Key | 数字 |
| TAOBAO_APP_SECRET | 淘宝App Secret | 字符串 |
| JWT_SECRET | JWT密钥 | 随机字符串（32位以上） |
| DB_PASSWORD | 数据库密码 | 自定义强密码 |

### 生成 SSH 密钥对

**在本地执行：**
```bash
# 生成密钥对
ssh-keygen -t rsa -b 4096 -f smart-home-key

# 会生成两个文件：
# smart-home-key (私钥) - 添加到 GitHub Secrets
# smart-home-key.pub (公钥) - 添加到服务器
```

**将公钥添加到服务器：**
```bash
# 在服务器上执行
echo "你的公钥内容" >> ~/.ssh/authorized_keys
```

**生成 JWT_SECRET：**
```bash
# 随机生成 32 位密钥
openssl rand -base64 32
```

---

## 完成检查清单

在开始部署前，请确认：

- [ ] Git 已安装并配置
- [ ] 代码已推送到 GitHub 仓库
- [ ] 云服务器已购买
- [ ] 服务器 IP 已获取
- [ ] DeepSeek API Key 已获取
- [ ] 淘宝 App Key 和 Secret 已获取
- [ ] 域名解析已配置
- [ ] SSH 密钥对已生成
- [ ] GitHub Secrets 已配置

---

## 下一步

完成以上准备后，告诉我，我将帮您：
1. 初始化服务器环境
2. 部署应用到服务器
3. 配置 HTTPS 证书
4. 验证服务是否正常运行
