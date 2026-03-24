# 智能家居方案设计APP 服务器部署环境文档

| 版本 | 日期 | 作者 | 状态 |
|------|------|------|------|
| v1.0 | 2026-03-22 | 开发工程师 | 初稿 |

---

## 一、文档说明

### 1.1 文档目的

本文档定义智能家居方案设计APP的服务器部署环境要求，供部署维护人员参考执行。

### 1.2 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 系统架构 | [ARCHITECTURE.md](../../ARCH/ARCHITECTURE.md) | 技术选型说明 |
| 详细设计 | [DETAILED_DESIGN.md](./DETAILED_DESIGN.md) | 内部实现设计 |
| API规范 | [API.md](../../ARCH/API.md) | 接口定义 |

---

## 二、服务器要求

### 2.1 硬件配置

| 资源 | 最低配置 | 推荐配置 |
|------|----------|----------|
| CPU | 1核 | 2核+ |
| 内存 | 1 GB | 2 GB+ |
| 磁盘 | 20 GB | 40 GB+ |
| 带宽 | 1 Mbps | 5 Mbps+ |

### 2.2 操作系统

| 操作系统 | 版本 | 说明 |
|----------|------|------|
| **Ubuntu** | 22.04 LTS / 24.04 LTS | 推荐 |
| **CentOS** | 7+ | 支持 |
| **Debian** | 11+ | 支持 |

### 2.3 服务器信息

| 项目 | 值 |
|------|-----|
| 服务器IP | 8.137.174.58 |
| 域名 | www.smartifun.icu |
| 用户名 | root |
| 密码 | lxh107016! |

---

## 三、基础软件环境

### 3.1 必须安装的软件

| 软件 | 版本要求 | 安装命令 |
|------|----------|----------|
| Python | >= 3.10 | `apt install python3.10` |
| MySQL | >= 8.0 | `apt install mysql-server` |
| Redis | >= 6.0 | `apt install redis-server` |
| Nginx | >= 1.18 | `apt install nginx` |
| Git | 最新版 | `apt install git` |

### 3.2 Python 虚拟环境

```bash
# 创建项目目录
mkdir -p /opt/smart_home
cd /opt/smart_home

# 创建虚拟环境
python3 -m venv venv

# 激活虚拟环境
source venv/bin/activate

# 升级 pip
pip install --upgrade pip
```

---

## 四、端口规划

| 端口 | 服务 | 协议 | 说明 |
|------|------|------|------|
| 22 | SSH | TCP | 远程连接 |
| 80 | HTTP | TCP | Web服务/API |
| 443 | HTTPS | TCP | SSL加密（可选） |
| 3306 | MySQL | TCP | 数据库服务 |
| 6379 | Redis | TCP | 缓存/消息队列 |
| 8000 | FastAPI | TCP | API服务（内部） |

---

## 五、服务配置

### 5.1 MySQL 配置

#### 安装

```bash
apt update
apt install mysql-server
```

#### 创建数据库

```sql
-- 登录 MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE smart_home CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选，生产环境建议使用强密码）
CREATE USER 'smart_home'@'%' IDENTIFIED BY 'your_strong_password';
GRANT ALL PRIVILEGES ON smart_home.* TO 'smart_home'@'%';
FLUSH PRIVILEGES;

-- 退出
EXIT;
```

#### 允许远程连接

```bash
# 编辑 MySQL 配置
nano /etc/mysql/mysql.conf.d/mysqld.cnf

# 修改绑定地址为 0.0.0.0
bind-address = 0.0.0.0

# 重启服务
systemctl restart mysql
```

### 5.2 Redis 配置

#### 安装

```bash
apt install redis-server
```

#### 配置

```bash
# 编辑配置文件
nano /etc/redis/redis.conf

# 修改绑定地址
bind 0.0.0.0

# 关闭保护模式（仅内网环境）
protected-mode no

# 重启服务
systemctl restart redis-server
```

### 5.3 Nginx 配置

#### 安装

```bash
apt install nginx
```

#### 站点配置

创建配置文件 `/etc/nginx/sites-available/smart_home`:

```nginx
server {
    listen 80;
    server_name 8.137.174.58;

    # API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 健康检查
    location /health {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
    }

    # 前端静态文件
    location / {
        root /opt/smart_home/frontend/build/web;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 文件下载
    location /downloads/ {
        alias /opt/smart_home/downloads/;
        autoindex on;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        root /opt/smart_home/frontend/build/web;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

#### 启用站点

```bash
# 启用配置
ln -s /etc/nginx/sites-available/smart_home /etc/nginx/sites-enabled/

# 测试配置
nginx -t

# 重载 Nginx
systemctl reload nginx
```

---

## 六、应用部署

### 6.1 目录结构

```
/opt/smart_home/
├── backend/                      # 后端代码
│   ├── app/
│   │   ├── modules/
│   │   ├── core/
│   │   ├── shared/
│   │   ├── celery_tasks/
│   │   └── main.py
│   ├── tests/
│   ├── requirements.txt
│   ├── .env
│   └── celery_app.py
├── frontend/                     # 前端代码
│   └── build/web/               # 构建产物
├── downloads/                     # APK下载目录
├── venv/                         # Python虚拟环境
└── logs/                         # 日志目录
```

### 6.2 后端环境变量

**文件**: `/opt/smart_home/backend/.env`

```bash
# 应用配置
APP_NAME=Smart Home API
APP_VERSION=1.0.0
DEBUG=False
ENVIRONMENT=production

# 数据库配置
DATABASE_URL=mysql+pymysql://smart_home:your_password@127.0.0.1:3306/smart_home

# Redis配置
REDIS_URL=redis://127.0.0.1:6379/0
CELERY_BROKER_URL=redis://127.0.0.1:6379/1
CELERY_RESULT_BACKEND=redis://127.0.0.1:6379/2

# 安全配置（生产环境必须修改）
SECRET_KEY=your-super-secret-key-min-32-chars
ALGORITHM=HS256

# CORS配置（根据实际域名修改）
ALLOWED_ORIGINS=https://www.smartifun.icu,http://8.137.174.58

# AI服务配置（可选，不配置则使用Mock数据）
DEEPSEEK_API_KEY=sk-9826531263474471be0c759c03366421
DEEPSEEK_API_URL=https://api.deepseek.com/v1
DEEPSEEK_MODEL=deepseek-chat
AI_GENERATION_TIMEOUT=120

# 淘宝开放平台（可选）
TAOBAO_APP_KEY=35283021
TAOBAO_APP_SECRET=074d34bf28b06346f95b40480c93383c
TAOBAO_API_URL=https://eco.taobao.com/router/rest
```

### 6.3 Python 依赖安装

```bash
cd /opt/smart_home/backend
source ../venv/bin/activate
pip install -r requirements.txt
```

### 6.4 数据库初始化

```bash
cd /opt/smart_home/backend
source ../venv/bin/activate
python -m scripts.init_db
```

---

## 七、进程管理

### 7.1 Systemd 服务配置

#### API 服务

**文件**: `/etc/systemd/system/smart-home-api.service`

```ini
[Unit]
Description=Smart Home API Service
After=network.target mysql.service redis.service
Wants=mysql.service redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/smart_home/backend
Environment="PATH=/opt/smart_home/venv/bin"
ExecStart=/opt/smart_home/venv/bin/uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 2
Restart=always
RestartSec=10
StandardOutput=append:/opt/smart_home/logs/api.log
StandardError=append:/opt/smart_home/logs/api_error.log

[Install]
WantedBy=multi-user.target
```

#### Celery Worker 服务

**文件**: `/etc/systemd/system/smart-home-celery.service`

```ini
[Unit]
Description=Smart Home Celery Worker
After=network.target redis.service
Wants=redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/smart_home/backend
Environment="PATH=/opt/smart_home/venv/bin"
ExecStart=/opt/smart_home/venv/bin/celery -A celery_app worker --loglevel=info --concurrency=4
Restart=always
RestartSec=10
StandardOutput=append:/opt/smart_home/logs/celery.log
StandardError=append:/opt/smart_home/logs/celery_error.log

[Install]
WantedBy=multi-user.target
```

### 7.2 服务管理命令

```bash
# 重载 systemd 配置
systemctl daemon-reload

# 启用服务（开机自启）
systemctl enable smart-home-api
systemctl enable smart-home-celery

# 启动服务
systemctl start smart-home-api
systemctl start smart-home-celery

# 查看服务状态
systemctl status smart-home-api
systemctl status smart-home-celery

# 重启服务
systemctl restart smart-home-api
systemctl restart smart-home-celery

# 停止服务
systemctl stop smart-home-api
systemctl stop smart-home-celery

# 查看日志
journalctl -u smart-home-api -f
journalctl -u smart-home-celery -f
```

---

## 八、API 文档

部署完成后，API 文档访问地址：

| 文档 | 地址 |
|------|------|
| Swagger UI | http://8.137.174.58/docs |
| ReDoc | http://8.137.174.58/redoc |
| 健康检查 | http://8.137.174.58/health |

### 主要 API 端点

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /health | 健康检查 | 否 |
| GET | / | 根路径 | 否 |
| GET | /api/v1/houses | 户型列表 | 否 |
| POST | /api/v1/houses | 创建户型 | 否 |
| GET | /api/v1/products | 商品列表 | 否 |
| GET | /api/v1/products/{id} | 商品详情 | 否 |
| POST | /api/v1/products/match | 商品匹配 | 否 |
| GET | /api/v1/brands | 品牌列表 | 否 |
| GET | /api/v1/categories | 分类列表 | 否 |
| POST | /api/v1/schemes/generate | 生成方案 | 否 |
| GET | /api/v1/schemes/tasks/{id} | 方案生成状态 | 否 |

---

## 九、安全配置

### 9.1 防火墙配置

```bash
# 使用 ufw (Ubuntu)
ufw default deny incoming
ufw default allow outgoing
ufw allow 22/tcp    # SSH
ufw allow 80/tcp    # HTTP
ufw allow 443/tcp   # HTTPS
ufw enable
```

### 9.2 数据库密码建议

```sql
-- 修改 MySQL root 密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_new_strong_password';
```

### 9.3 Redis 密码（可选）

```bash
# 设置 Redis 密码
redis-cli CONFIG SET requirepass "your_redis_password"

# 持久化配置
redis-cli CONFIG SAVE
```

---

## 十、监控告警

### 10.1 建议的监控指标

| 指标 | 阈值 | 告警方式 |
|------|------|----------|
| CPU 使用率 | > 80% | 邮件/短信 |
| 内存使用率 | > 85% | 邮件/短信 |
| 磁盘使用率 | > 80% | 邮件/短信 |
| API 响应时间 | > 1s | 邮件 |
| 服务存活 | 宕机 | 电话 |

### 10.2 日志管理

```bash
# 创建日志目录
mkdir -p /opt/smart_home/logs

# 配置日志轮转 /etc/logrotate.d/smart_home
/opt/smart_home/logs/*.log {
    daily
    missingok
    rotate 14
    compress
    delaycompress
    notifempty
    create 0640 root root
}
```

---

## 十一、故障排查

### 11.1 常见问题

#### API 服务无法启动

```bash
# 检查端口占用
lsof -i :8000

# 检查配置语法
python -c "from app.main import app; print('OK')"

# 查看日志
tail -50 /opt/smart_home/logs/api_error.log
```

#### 数据库连接失败

```bash
# 测试 MySQL 连接
mysql -u smart_home -p -h 127.0.0.1 smart_home

# 检查 MySQL 服务
systemctl status mysql

# 查看 MySQL 日志
tail -50 /var/log/mysql/error.log
```

#### Redis 连接失败

```bash
# 测试 Redis 连接
redis-cli ping

# 检查 Redis 服务
systemctl status redis-server
```

#### Celery 任务不执行

```bash
# 检查 Celery 日志
tail -50 /opt/smart_home/logs/celery.log

# 查看活跃任务
celery -A celery_app inspect active

# 查看已注册任务
celery -A celery_app inspect registered
```

### 11.2 服务重启顺序

```bash
# 正确的启动顺序
systemctl start mysql
systemctl start redis-server
systemctl start smart-home-api
systemctl start smart-home-celery

# 正确的停止顺序
systemctl stop smart-home-celery
systemctl stop smart-home-api
systemctl stop redis-server
systemctl stop mysql
```

---

## 十二、联系方式

如有技术问题，请联系开发团队。

---

*文档状态：待确认*
*请部署维护人员确认环境配置后执行部署。*
