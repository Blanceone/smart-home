# 智能家居方案设计APP 服务器环境配置文档

| 版本 | 日期 | 作者 |
|------|------|------|
| v1.0 | 2026-03-22 | 开发团队 |

---

## 一、系统要求

### 1.1 硬件要求

| 资源 | 最低配置 | 推荐配置 |
|------|----------|----------|
| CPU | 1核 | 2核+ |
| 内存 | 1 GB | 2 GB+ |
| 磁盘 | 20 GB | 40 GB+ |
| 带宽 | 1 Mbps | 5 Mbps+ |

### 1.2 操作系统

- **推荐**: Ubuntu 22.04 LTS / Ubuntu 24.04 LTS
- **支持**: CentOS 7+ / Debian 11+

---

## 二、基础软件环境

### 2.1 必须安装的软件

| 软件 | 版本要求 | 用途 |
|------|----------|------|
| Python | >= 3.10 | 后端运行环境 |
| MySQL | >= 8.0 | 主数据库 |
| Redis | >= 6.0 | 缓存 / Celery消息队列 |
| Nginx | >= 1.18 | 反向代理 / 静态文件服务 |
| Git | 最新版 | 代码管理 |

### 2.2 Python 虚拟环境

```bash
# 创建虚拟环境
python3 -m venv /opt/smart_home/venv

# 激活虚拟环境
source /opt/smart_home/venv/bin/activate
```

---

## 三、服务器端口规划

| 端口 | 服务 | 说明 |
|------|------|------|
| 22 | SSH | 远程连接 |
| 80 | HTTP | Web服务 / API |
| 443 | HTTPS | SSL加密（可选） |
| 3306 | MySQL | 数据库服务 |
| 6379 | Redis | 缓存/消息队列 |
| 8000 | FastAPI | API服务（内部） |

---

## 四、服务配置

### 4.1 MySQL 配置

**安装命令 (Ubuntu)**:
```bash
apt update
apt install mysql-server
```

**创建数据库和用户**:
```sql
CREATE DATABASE smart_home CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'smart_home'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON smart_home.* TO 'smart_home'@'%';
FLUSH PRIVILEGES;
```

**远程访问配置**:
```bash
# 编辑 MySQL 配置
nano /etc/mysql/mysql.conf.d/mysqld.cnf

# 修改绑定地址
bind-address = 0.0.0.0

# 重启服务
systemctl restart mysql
```

### 4.2 Redis 配置

**安装命令 (Ubuntu)**:
```bash
apt install redis-server
```

**配置远程访问**:
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

**安全建议**（可选）:
```bash
# 设置密码认证
redis-cli CONFIG SET requirepass "your_redis_password"
```

### 4.3 Nginx 配置

**安装命令**:
```bash
apt install nginx
```

**站点配置** `/etc/nginx/sites-available/smart_home`:
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
}
```

**启用站点**:
```bash
ln -s /etc/nginx/sites-available/smart_home /etc/nginx/sites-enabled/
nginx -t
systemctl reload nginx
```

---

## 五、应用配置

### 5.1 目录结构

```
/opt/smart_home/
├── backend/              # 后端代码
│   ├── app/
│   ├── celery_tasks/
│   ├── requirements.txt
│   └── .env
├── frontend/             # 前端代码
│   └── build/web/       # 构建产物
├── downloads/            # APK下载目录
├── venv/                # Python虚拟环境
└── logs/                # 日志目录
```

### 5.2 环境变量配置

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
ACCESS_TOKEN_EXPIRE_MINUTES=120
REFRESH_TOKEN_EXPIRE_DAYS=7

# CORS配置（根据实际域名修改）
ALLOWED_ORIGINS=https://www.smartifun.icu,http://8.137.174.58

# AI服务配置（可选）
DEEPSEEK_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxx
DEEPSEEK_API_URL=https://api.deepseek.com/v1
DEEPSEEK_MODEL=deepseek-chat
AI_GENERATION_TIMEOUT=30
```

### 5.3 Python 依赖安装

```bash
cd /opt/smart_home/backend
source ../venv/bin/activate
pip install -r requirements.txt
```

---

## 六、进程管理

### 6.1 Systemd 服务配置

**API 服务** - `/etc/systemd/system/smart-home-api.service`:
```ini
[Unit]
Description=Smart Home API Service
After=network.target mysql.service redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/smart_home/backend
Environment="PATH=/opt/smart_home/venv/bin"
ExecStart=/opt/smart_home/venv/bin/uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 2
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Celery Worker 服务** - `/etc/systemd/system/smart-home-celery.service`:
```ini
[Unit]
Description=Smart Home Celery Worker
After=network.target redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/smart_home/backend
Environment="PATH=/opt/smart_home/venv/bin"
ExecStart=/opt/smart_home/venv/bin/celery -A celery_app worker --loglevel=info --concurrency=4
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**启用服务**:
```bash
systemctl daemon-reload
systemctl enable smart-home-api
systemctl enable smart-home-celery
systemctl start smart-home-api
systemctl start smart-home-celery
```

### 6.2 服务管理命令

```bash
# 查看服务状态
systemctl status smart-home-api
systemctl status smart-home-celery

# 重启服务
systemctl restart smart-home-api
systemctl restart smart-home-celery

# 查看日志
journalctl -u smart-home-api -f
journalctl -u smart-home-celery -f
```

---

## 七、API文档

部署完成后，API文档访问地址：
- Swagger UI: `http://8.137.174.58/docs`
- ReDoc: `http://8.137.174.58/redoc`

### 主要API端点

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /health | 健康检查 | 否 |
| GET | /api/v1/houses | 户型列表 | 否 |
| POST | /api/v1/houses | 创建户型 | 否 |
| GET | /api/v1/houses/{id} | 户型详情 | 否 |
| GET | /api/v1/products | 商品列表 | 否 |
| GET | /api/v1/brands | 品牌列表 | 否 |
| GET | /api/v1/categories | 分类列表 | 否 |
| POST | /api/v1/schemes/generate | 生成方案 | 否 |
| GET | /api/v1/schemes/tasks/{id} | 方案生成状态 | 否 |

---

## 八、安全建议

### 8.1 防火墙配置

```bash
# 使用 ufw (Ubuntu)
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable
```

### 8.2 定期备份

```bash
# 数据库备份脚本 /opt/smart_home/scripts/backup.sh
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u smart_home -p smart_home > /opt/smart_home/backups/smart_home_$DATE.sql
find /opt/smart_home/backups -mtime +7 -delete
```

### 8.3 SSL证书（可选）

使用 Let's Encrypt 免费证书：
```bash
apt install certbot python3-certbot-nginx
certbot --nginx -d www.smartifun.icu
```

---

## 九、故障排查

### 常见问题

**1. API服务无法启动**
```bash
# 检查端口占用
lsof -i :8000

# 检查日志
journalctl -u smart-home-api -n 50
```

**2. 数据库连接失败**
```bash
# 测试MySQL连接
mysql -u smart_home -p -h 127.0.0.1 smart_home

# 测试Redis连接
redis-cli ping
```

**3. Celery任务不执行**
```bash
# 检查Celery日志
journalctl -u smart-home-celery -n 50

# 手动触发任务测试
celery -A celery_app inspect active
```

---

## 十、联系信息

如有技术问题，请联系开发团队。

---

*文档版本: v1.0*
*最后更新: 2026-03-22*
