# 部署文档

## 文档信息

| 项目名称 | 智能家居方案定制APP |
|---------|-------------------|
| 文档版本 | V1.0 |
| 创建日期 | 2026-03-18 |
| 更新日期 | 2026-03-18 |
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

### 5.2 触发条件

- 推送到 `main` 分支
- 手动触发 workflow

### 5.3 部署流程

1. **代码检查**: ESLint + TypeScript 类型检查
2. **单元测试**: Jest 测试
3. **构建镜像**: Docker 镜像构建
4. **推送镜像**: 推送到 Docker Hub (可选)
5. **SSH 部署**: 连接服务器，拉取最新代码
6. **服务重启**: Docker Compose 重启服务
7. **健康检查**: 验证服务是否正常启动

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

## 十、联系方式

| 角色 | 联系方式 |
|------|---------|
| 运维工程师 | devops@example.com |
| 开发团队 | dev@example.com |
| 紧急联系 | +86-xxx-xxxx-xxxx |
