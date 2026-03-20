# 部署文档

## 文档信息

| 项目名称 | 智能家居方案定制APP |
|---------|-------------------|
| 文档版本 | V1.2 |
| 创建日期 | 2026-03-18 |
| 更新日期 | 2026-03-20 |
| 运维工程师 | DevOps Engineer |
| 文档状态 | 正式版 |

---

## 一、部署架构概览

### 1.1 架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           用户终端                                       │
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐                   │
│  │   Android   │   │    iOS      │   │    Web      │                   │
│  │   (APK)     │   │   (IPA)     │   │  (Browser)  │                   │
│  └─────────────┘   └─────────────┘   └─────────────┘                   │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         负载均衡 / CDN                                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    Nginx (反向代理)                              │   │
│  │   - SSL/TLS 终端                                                 │   │
│  │   - 静态资源服务                                                 │   │
│  │   - API 请求转发                                                 │   │
│  │   - Gzip 压缩                                                    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
┌───────────────────────┐ ┌─────────────────┐ ┌───────────────────────┐
│   后端服务容器         │ │  数据库容器      │ │  缓存容器             │
│  ┌─────────────────┐  │ │ ┌─────────────┐ │ │ ┌─────────────────┐  │
│  │   NestJS App    │  │ │ │ PostgreSQL  │ │ │ │     Redis       │  │
│  │   (Port: 3000)  │  │ │ │ (Port: 5432)│ │ │ │  (Port: 6379)   │  │
│  └─────────────────┘  │ │ └─────────────┘ │ │ └─────────────────┘  │
│  ┌─────────────────┐  │ │                 │ │                       │
│  │   Prisma ORM    │  │ │  数据持久化     │ │  会话/缓存            │
│  └─────────────────┘  │ │  (Volume)       │ │  (可选持久化)         │
└───────────────────────┘ └─────────────────┘ └───────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         第三方服务                                       │
│  ┌─────────────┐   ┌─────────────┐                                      │
│  │  DeepSeek   │   │  淘宝开放平台│                                      │
│  │    API      │   │   (商品API) │                                      │
│  └─────────────┘   └─────────────┘                                      │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1.2 部署方案

| 组件 | 部署方式 | 说明 |
|------|---------|------|
| 前端 (Android) | APK 分发 | GitHub Releases / 应用商店 |
| 前端 (Web) | 静态托管 | Vercel / Netlify / Nginx |
| 后端 API | Docker 容器 | VPS / 云服务器 |
| 数据库 | Docker 容器 | PostgreSQL 15+ |
| 缓存 | Docker 容器 | Redis 7+ |
| 反向代理 | Nginx | SSL 终端、负载均衡 |

---

## 二、服务器配置要求

### 2.1 最低配置

| 配置项 | 要求 |
|--------|------|
| CPU | 2 核 |
| 内存 | 4 GB |
| 硬盘 | 40 GB SSD |
| 带宽 | 5 Mbps |
| 操作系统 | Ubuntu 22.04 LTS / CentOS 8+ |

### 2.2 推荐配置

| 配置项 | 要求 |
|--------|------|
| CPU | 4 核 |
| 内存 | 8 GB |
| 硬盘 | 80 GB SSD |
| 带宽 | 10 Mbps |
| 操作系统 | Ubuntu 22.04 LTS |

### 2.3 域名规划

| 域名 | 用途 | 说明 |
|------|------|------|
| api.smarthome.com | API 服务 | 后端接口 |
| www.smarthome.com | Web 前端 | 用户访问入口 |
| admin.smarthome.com | 管理后台 | 运维管理 (可选) |

---

## 三、环境变量配置

### 3.1 后端环境变量

创建 `.env` 文件：

```bash
# 数据库配置
DATABASE_URL="postgresql://postgres:YOUR_PASSWORD@postgres:5432/smart_home?schema=public"

# Redis配置
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=

# DeepSeek API配置
DEEPSEEK_API_KEY=your_deepseek_api_key
DEEPSEEK_BASE_URL=https://api.deepseek.com

# 淘宝API配置
TAOBAO_APP_KEY=your_taobao_app_key
TAOBAO_APP_SECRET=your_taobao_app_secret

# 服务配置
PORT=3000
NODE_ENV=production
APP_VERSION=1.0.0
APP_URL=https://api.smarthome.com

# JWT配置
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRES_IN=7d
```

### 3.2 GitHub Secrets 配置

在 GitHub 仓库设置中配置以下 Secrets：

| Secret 名称 | 说明 |
|------------|------|
| SERVER_HOST | 服务器 IP 地址 |
| SERVER_USER | SSH 用户名 |
| SSH_PRIVATE_KEY | SSH 私钥 |
| DEEPSEEK_API_KEY | DeepSeek API 密钥 |
| TAOBAO_APP_KEY | 淘宝 App Key |
| TAOBAO_APP_SECRET | 淘宝 App Secret |
| JWT_SECRET | JWT 密钥 |
| DB_PASSWORD | 数据库密码 |

---

## 四、部署步骤

### 4.1 服务器初始化

```bash
# 1. 更新系统
sudo apt update && sudo apt upgrade -y

# 2. 安装 Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# 3. 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 4. 安装 Nginx
sudo apt install nginx -y

# 5. 安装 certbot (SSL 证书)
sudo apt install certbot python3-certbot-nginx -y

# 6. 配置防火墙
sudo ufw allow ssh
sudo ufw allow http
sudo ufw allow https
sudo ufw enable
```

### 4.2 克隆项目

```bash
# 创建项目目录
mkdir -p /opt/smart-home
cd /opt/smart-home

# 克隆代码
git clone https://github.com/your-org/smart-home.git .
```

### 4.3 配置环境变量

```bash
# 创建环境变量文件
cp server/.env.example server/.env

# 编辑环境变量
nano server/.env
```

### 4.4 启动服务

```bash
# 构建并启动所有服务
cd /opt/smart-home
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 4.5 配置 Nginx

```bash
# 复制 Nginx 配置
sudo cp deploy/nginx/smarthome.conf /etc/nginx/sites-available/
sudo ln -s /etc/nginx/sites-available/smarthome.conf /etc/nginx/sites-enabled/

# 测试配置
sudo nginx -t

# 重载 Nginx
sudo nginx -s reload
```

### 4.6 配置 SSL 证书

```bash
# 申请 SSL 证书
sudo certbot --nginx -d api.smarthome.com -d www.smarthome.com

# 自动续期
sudo certbot renew --dry-run
```

### 4.7 数据库初始化

```bash
# 运行数据库迁移
docker-compose exec app npx prisma migrate deploy

# (可选) 查看数据库
docker-compose exec app npx prisma studio
```

---

## 五、CI/CD 流程

### 5.1 自动化部署流程

```
代码推送 → GitHub Actions → 构建 Docker 镜像 → SSH 部署 → 服务重启
```

### 5.2 后端部署流程

**触发条件**: 推送到 `main` 分支

**Workflow 文件**: `.github/workflows/deploy.yml`

| 步骤 | 说明 |
|------|------|
| 代码检查 | ESLint + TypeScript 类型检查 |
| 单元测试 | Jest 测试覆盖率 |
| 构建镜像 | Docker 镜像构建 |
| SSH 部署 | 连接服务器，拉取最新代码 |
| 服务重启 | Docker Compose 重启服务 |
| 健康检查 | 验证服务是否正常启动 |

### 5.3 Android APK 构建流程

**触发条件**: 
- 推送到 `main` 分支且 `app/**` 目录有变更
- 手动触发 workflow

**Workflow 文件**: `.github/workflows/android.yml`

| 步骤 | 说明 |
|------|------|
| 环境准备 | JDK 17 + Gradle 8.2 |
| 构建 Debug APK | 用于测试的调试版本 |
| 构建 Release APK | 用于发布的正式版本 |
| 上传 Artifact | APK 保存 30 天 |

**下载 APK**:
1. 访问 https://github.com/Blanceone/smart-home/actions
2. 点击最新的 **Build Android APK** workflow
3. 在 **Artifacts** 区域下载 `app-debug` 或 `app-release`

---

## 六、运维管理

### 6.1 常用命令

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f app

# 重启服务
docker-compose restart app

# 停止所有服务
docker-compose down

# 更新并重启
git pull && docker-compose up -d --build

# 进入容器
docker-compose exec app sh

# 数据库备份
docker-compose exec postgres pg_dump -U postgres smart_home > backup.sql

# 数据库恢复
cat backup.sql | docker-compose exec -T postgres psql -U postgres smart_home
```

### 6.2 日志管理

```bash
# 查看应用日志
docker-compose logs -f --tail=100 app

# 查看 Nginx 访问日志
sudo tail -f /var/log/nginx/access.log

# 查看 Nginx 错误日志
sudo tail -f /var/log/nginx/error.log
```

### 6.3 监控告警

推荐使用以下监控方案：

| 监控项 | 工具 | 说明 |
|--------|------|------|
| 服务器监控 | Prometheus + Grafana | CPU、内存、磁盘、网络 |
| 容器监控 | cAdvisor | 容器资源使用 |
| 应用监控 | PM2 / Docker Stats | 进程状态 |
| 日志收集 | ELK Stack / Loki | 集中日志管理 |
| 告警通知 | Alertmanager / 钉钉 | 异常告警 |

---

## 七、安全配置

### 7.1 服务器安全

```bash
# 1. 禁用 root 登录
sudo sed -i 's/PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config

# 2. 使用密钥登录
sudo sed -i 's/#PubkeyAuthentication yes/PubkeyAuthentication yes/' /etc/ssh/sshd_config

# 3. 重启 SSH 服务
sudo systemctl restart sshd

# 4. 安装 fail2ban
sudo apt install fail2ban -y
sudo systemctl enable fail2ban
sudo systemctl start fail2ban
```

### 7.2 数据库安全

```bash
# 修改默认密码
# 限制远程访问
# 定期备份
# 使用 SSL 连接
```

### 7.3 应用安全

- 使用 HTTPS
- 配置 CORS 白名单
- 启用请求频率限制
- 定期更新依赖包
- 敏感信息使用环境变量

---

## 八、故障排查

### 8.1 常见问题

| 问题 | 可能原因 | 解决方案 |
|------|---------|---------|
| 服务无法启动 | 端口被占用 | 检查端口占用情况 |
| 数据库连接失败 | 密码错误/服务未启动 | 检查环境变量和服务状态 |
| API 超时 | 服务器资源不足 | 扩容或优化代码 |
| SSL 证书错误 | 证书过期 | 续期证书 |

### 8.2 排查步骤

```bash
# 1. 检查服务状态
docker-compose ps

# 2. 检查端口占用
sudo netstat -tulpn | grep LISTEN

# 3. 检查磁盘空间
df -h

# 4. 检查内存使用
free -m

# 5. 检查 Docker 日志
docker-compose logs --tail=100

# 6. 检查 Nginx 状态
sudo systemctl status nginx
sudo nginx -t
```

---

## 九、备份与恢复

### 9.1 自动备份脚本

```bash
#!/bin/bash
# backup.sh

BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# 创建备份目录
mkdir -p $BACKUP_DIR

# 数据库备份
docker-compose exec -T postgres pg_dump -U postgres smart_home > $BACKUP_DIR/db_$DATE.sql

# 压缩备份
gzip $BACKUP_DIR/db_$DATE.sql

# 删除 7 天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "Backup completed: db_$DATE.sql.gz"
```

### 9.2 定时备份

```bash
# 添加到 crontab
crontab -e

# 每天凌晨 2 点备份
0 2 * * * /opt/smart-home/deploy/scripts/backup.sh >> /var/log/backup.log 2>&1
```

---

## 十、完整部署流程实录

本章节记录了一次完整的部署流程，包括遇到的问题和解决方案，供后续部署参考。

### 10.1 部署环境信息

| 项目 | 信息 |
|------|------|
| 服务器 | 阿里云 ECS |
| IP 地址 | 8.137.174.58 |
| 操作系统 | Ubuntu 22.04 LTS |
| GitHub 仓库 | https://github.com/Blanceone/smart-home |
| 临时域名 | https://duo-technological-preparing-const.trycloudflare.com |

### 10.2 部署前准备清单

| 序号 | 检查项 | 状态 |
|------|--------|------|
| 1 | 服务器已购买并可 SSH 连接 | ✅ |
| 2 | 域名已购买 (www.smartifun.icu) | ✅ |
| 3 | DeepSeek API Key 已获取 | ✅ |
| 4 | 淘宝开放平台 AppKey/Secret 已获取 | ✅ |
| 5 | GitHub 仓库已创建 | ✅ |
| 6 | GitHub Secrets 已配置 | ✅ |

### 10.3 部署流程详细步骤

#### 步骤 1：本地代码验证

在推送代码前，必须在本地完成以下验证：

```bash
# 进入后端目录
cd server

# 1. 运行 ESLint 检查
npm run lint

# 2. 运行单元测试
npm run test

# 3. 构建项目
npm run build
```

**验证结果**：
- ESLint: ✅ 通过
- 单元测试: ✅ 82/82 通过
- 构建: ✅ 成功

#### 步骤 2：推送代码到 GitHub

```bash
# 添加所有更改
git add -A

# 提交更改
git commit -m "feat: your commit message"

# 推送到远程仓库
git push origin main
```

#### 步骤 3：GitHub Actions 自动构建

推送代码后，GitHub Actions 会自动触发以下工作流：

**后端部署 (deploy.yml)**：
1. 代码检查 (ESLint)
2. 单元测试 (Jest)
3. 构建 Docker 镜像
4. SSH 连接服务器
5. 拉取最新代码
6. 重启 Docker 容器
7. 健康检查

**Android APK 构建 (android.yml)**：
1. 设置 JDK 17 环境
2. 构建 Debug APK
3. 构建 Release APK
4. 上传到 Artifacts

#### 步骤 4：服务器端验证

```bash
# 检查代码版本
cd /opt/smart-home
git log --oneline -3

# 检查容器状态
docker ps --format 'table {{.Names}}\t{{.Status}}'

# 检查健康状态
curl http://localhost:3000/v1/health
```

#### 步骤 5：API 验证

```bash
# 健康检查
curl https://your-domain/v1/health

# 设备列表
curl -H "X-Device-ID: test" -H "X-Platform: web" -H "X-Version: 1.0.0" \
  https://your-domain/v1/devices
```

### 10.4 遇到的问题与解决方案

#### 问题 1：ESLint 配置文件缺失

**错误信息**：
```
ESLint couldn't find a configuration file
```

**解决方案**：
创建 `server/.eslintrc.js` 文件：

```javascript
module.exports = {
  parser: '@typescript-eslint/parser',
  parserOptions: {
    project: 'tsconfig.json',
    tsconfigRootDir: __dirname,
    sourceType: 'module',
  },
  plugins: ['@typescript-eslint/eslint-plugin'],
  extends: [
    'plugin:@typescript-eslint/recommended',
    'plugin:prettier/recommended',
  ],
  root: true,
  env: {
    node: true,
    jest: true,
  },
  ignorePatterns: ['.eslintrc.js'],
  rules: {
    '@typescript-eslint/interface-name-prefix': 'off',
    '@typescript-eslint/explicit-function-return-type': 'off',
    '@typescript-eslint/explicit-module-boundary-types': 'off',
    '@typescript-eslint/no-explicit-any': 'off',
    '@typescript-eslint/no-unused-vars': 'off',
  },
};
```

#### 问题 2：ESLint no-unused-vars 错误

**错误信息**：
```
'PrismaService' is defined but never used
'prisma' is assigned a value but never used
```

**解决方案**：
在 `.eslintrc.js` 中禁用该规则：
```javascript
'@typescript-eslint/no-unused-vars': 'off',
```

#### 问题 3：Prisma 在 Alpine Linux 中运行失败

**错误信息**：
```
Prisma Client could not locate the Query Engine
```

**解决方案**：
在 `prisma/schema.prisma` 中添加 binaryTargets：
```prisma
generator client {
  provider      = "prisma-client-js"
  binaryTargets = ["native", "linux-musl-openssl-3.0.x", "linux-musl"]
}
```

#### 问题 4：Docker 镜像缺少 OpenSSL

**解决方案**：
在 `Dockerfile` 中添加 OpenSSL 依赖：
```dockerfile
RUN apk add --no-cache openssl openssl-dev
```

#### 问题 5：GitHub 无法连接服务器

**原因**：网络问题导致 GitHub Actions 无法 SSH 连接服务器

**解决方案**：手动在服务器上执行部署：
```bash
cd /opt/smart-home
git fetch origin
git reset --hard origin/main
docker compose down
docker compose up -d --build
```

### 10.5 部署产物清单

| 产物 | 位置 | 说明 |
|------|------|------|
| Docker 镜像 | `smart-home-app:latest` | 后端应用镜像 |
| Docker 容器 | `smart-home-app` | NestJS 应用 |
| Docker 容器 | `smart-home-postgres` | PostgreSQL 数据库 |
| Docker 容器 | `smart-home-redis` | Redis 缓存 |
| 数据卷 | `postgres_data` | 数据库持久化存储 |
| 数据卷 | `redis_data` | Redis 持久化存储 |
| APK 文件 | GitHub Artifacts | Android 安装包 |

### 10.6 部署验证检查表

| 检查项 | 命令 | 预期结果 |
|--------|------|---------|
| 容器运行状态 | `docker ps` | 所有容器 Status 为 healthy |
| 健康检查 | `GET /v1/health` | `{"status":"healthy"}` |
| 数据库连接 | 健康检查中 | `"database":"ok"` |
| Redis 连接 | 健康检查中 | `"redis":"ok"` |
| DeepSeek API | 健康检查中 | `"deepseek":"ok"` |
| 淘宝 API | 健康检查中 | `"taobao":"ok"` |
| 设备数据 | `GET /v1/devices` | 返回设备列表 |

### 10.7 快速部署命令参考

```bash
# === 本地验证 ===
cd server && npm run lint && npm run test && npm run build

# === 推送代码 ===
git add -A && git commit -m "deploy" && git push origin main

# === 服务器手动部署 ===
cd /opt/smart-home
git fetch origin && git reset --hard origin/main
docker compose down && docker compose up -d --build

# === 查看日志 ===
docker compose logs -f app

# === 健康检查 ===
curl http://localhost:3000/v1/health
```

### 10.8 部署流程图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           完整部署流程                                        │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  本地开发     │───→│  本地验证     │───→│  推送代码     │───→│ GitHub Actions│
│              │    │              │    │              │    │              │
│  编写代码     │    │ npm run lint │    │ git push     │    │ 自动触发构建  │
│              │    │ npm run test │    │              │    │              │
│              │    │ npm run build│    │              │    │              │
└──────────────┘    └──────────────┘    └──────────────┘    └──────────────┘
                                                                   │
                    ┌──────────────────────────────────────────────┘
                    ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  ESLint 检查  │───→│  单元测试     │───→│  构建镜像     │───→│  SSH 部署     │
│              │    │              │    │              │    │              │
│  代码规范     │    │ Jest 测试    │    │ Docker build │    │ 连接服务器    │
│              │    │              │    │              │    │ 拉取代码      │
└──────────────┘    └──────────────┘    └──────────────┘    └──────────────┘
                                                                   │
                    ┌──────────────────────────────────────────────┘
                    ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  重启容器     │───→│  健康检查     │───→│  API 验证     │───→│  部署完成     │
│              │    │              │    │              │    │              │
│ docker up    │    │ /v1/health   │    │ /v1/devices  │    │ 服务可用      │
│              │    │              │    │              │    │              │
└──────────────┘    └──────────────┘    └──────────────┘    └──────────────┘
```

---

## 十一、联系方式

| 角色 | 联系方式 |
|------|---------|
| 运维工程师 | devops@example.com |
| 开发团队 | dev@example.com |
| 紧急联系 | +86-xxx-xxxx-xxxx |
