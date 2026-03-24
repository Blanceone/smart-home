# 智能家居系统部署分析报告

**部署时间**: 2026-03-24  
**版本**: v1.1.0  
**部署结果**: ✅ Success

---

## 一、部署概览

### 1.1 项目架构
| 组件 | 技术栈 | 版本 |
|------|--------|------|
| 后端框架 | FastAPI | 0.135.1 |
| 数据库 | MySQL | 8.0.45 |
| 缓存 | Redis | 7.0.15 |
| 任务队列 | Celery | 5.6.2 |
| 前端框架 | Flutter | 3.41.3 |
| 服务器 | 阿里云 ECS | Ubuntu 24.04 |

### 1.2 部署环境
- **服务器IP**: 8.137.174.58
- **操作系统**: Ubuntu 24.04 LTS
- **内存**: 1.6 GB
- **磁盘**: 40 GB

---

## 二、自动化部署

### 2.1 部署脚本

项目提供自动化部署脚本 `scripts/deploy.sh`，一键完成以下操作：

```bash
# 在项目根目录执行
bash scripts/deploy.sh
```

### 2.2 部署流程

| 步骤 | 操作 | 说明 |
|------|------|------|
| 1 | 代码同步 | 上传后端代码到服务器 |
| 2 | 配置修复 | 修正 Redis/MySQL 连接地址 |
| 3 | 重启 API | 重启 FastAPI 服务 |
| 4 | 重启 Celery | 重启 Celery Worker |
| 5 | 服务验证 | 检查所有服务状态 |
| 6 | 功能测试 | 测试方案生成 API |

### 2.3 需要同步的文件

```
backend/
├── .env                          # 环境配置
├── app/
│   ├── main.py                   # 应用入口
│   ├── celery_app.py             # Celery 配置
│   ├── core/config.py            # 配置类
│   └── modules/
│       ├── scheme/
│       │   ├── routes.py         # 方案路由
│       │   └── schemas.py        # 数据模型
│       └── house/
│           └── routes.py         # 户型路由
└── celery_tasks/
    └── generation.py             # 方案生成任务
```

---

## 三、服务配置

### 3.1 Systemd 服务

#### API 服务 (`/etc/systemd/system/smart-home-api.service`)
```ini
[Unit]
Description=Smart Home API Service
After=network.target mysql.service redis.service
Wants=mysql.service redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/smart-home/backend
Environment=PYTHONPATH=/opt/smart-home/backend
EnvironmentFile=/opt/smart-home/backend/.env
ExecStart=/opt/smart-home/backend/venv/bin/uvicorn app.main:app --host 127.0.0.1 --port 8000
Restart=always
RestartSec=10
StandardOutput=append:/opt/smart-home/logs/api.log
StandardError=append:/opt/smart-home/logs/api_error.log

[Install]
WantedBy=multi-user.target
```

#### Celery 服务 (`/etc/systemd/system/smart-home-celery.service`)
```ini
[Unit]
Description=Smart Home Celery Worker
After=network.target redis.service
Wants=redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/smart-home/backend
Environment=PYTHONPATH=/opt/smart-home/backend
EnvironmentFile=/opt/smart-home/backend/.env
ExecStart=/opt/smart-home/backend/venv/bin/celery -A app.celery_app:celery_app worker --loglevel=info --concurrency=4
Restart=always
RestartSec=10
StandardOutput=append:/opt/smart-home/logs/celery.log
StandardError=append:/opt/smart-home/logs/celery_error.log

[Install]
WantedBy=multi-user.target
```

### 3.2 环境变量配置

```bash
# 应用配置
APP_NAME=Smart Home API
APP_VERSION=1.0.0
DEBUG=False
ENVIRONMENT=production

# 数据库配置 (使用 localhost)
DATABASE_URL=mysql+pymysql://root:lxh107016!@127.0.0.1:3306/smart_home

# Redis配置 (使用 localhost)
REDIS_URL=redis://127.0.0.1:6379/0
CELERY_BROKER_URL=redis://127.0.0.1:6379/1
CELERY_RESULT_BACKEND=redis://127.0.0.1:6379/2

# 安全配置
SECRET_KEY=your-super-secret-key-min-32-chars
ALGORITHM=HS256

# CORS配置
ALLOWED_ORIGINS=*

# AI服务配置
DEEPSEEK_API_KEY=sk-9826531263474471be0c759c03366421
DEEPSEEK_API_URL=https://api.deepseek.com/v1
DEEPSEEK_MODEL=deepseek-chat
AI_GENERATION_TIMEOUT=120
AI_GENERATION_MAX_RETRIES=2
```

---

## 四、服务管理命令

### 4.1 服务状态
```bash
# 查看所有服务状态
systemctl status mysql redis-server nginx smart-home-api smart-home-celery

# 查看服务是否开机自启
systemctl is-enabled mysql redis-server nginx smart-home-api smart-home-celery
```

### 4.2 服务重启
```bash
# 重启 API 服务
systemctl restart smart-home-api

# 重启 Celery 服务
systemctl restart smart-home-celery

# 或者手动重启 Celery
pkill -f celery
cd /opt/smart-home/backend
celery -A app.celery_app:celery_app worker --loglevel=info --concurrency=4 &
```

### 4.3 日志查看
```bash
# API 日志
tail -f /opt/smart-home/logs/api.log
tail -f /opt/smart-home/logs/api_error.log

# Celery 日志
tail -f /opt/smart-home/logs/celery.log
tail -f /opt/smart-home/logs/celery_error.log
```

---

## 五、Celery 任务管理

### 5.1 验证任务注册
```bash
cd /opt/smart-home/backend
/opt/smart-home/backend/venv/bin/celery -A app.celery_app:celery_app inspect registered
```

### 5.2 已注册任务
```
✅ celery_tasks.generation.generate_scheme_task    # 方案生成
✅ celery_tasks.crawl.crawl_products_task          # 商品爬取
✅ celery_tasks.crawl.sync_product_prices_task     # 价格同步
✅ celery_tasks.crawl.cleanup_expired_tasks_task   # 清理过期任务
```

### 5.3 查看活跃任务
```bash
/opt/smart-home/backend/venv/bin/celery -A app.celery_app:celery_app inspect active
```

---

## 六、API 测试

### 6.1 健康检查
```bash
curl http://8.137.174.58/health
# 响应: {"status":"healthy"}
```

### 6.2 方案生成测试
```bash
curl -X POST http://8.137.174.58/api/v1/schemes/generate \
  -H "Content-Type: application/json" \
  -d '{
    "house_layout": {"total_area": 80, "rooms": [{"room_name": "客厅", "room_type": "living_room", "length": 5, "width": 4}]},
    "questionnaire": {"living_status": "owner", "resident_count": 2, "preferred_scenarios": ["lighting"]},
    "preferences": {"budget_min": 1000, "budget_max": 10000}
  }'
```

### 6.3 查询任务状态
```bash
curl http://8.137.174.58/api/v1/schemes/tasks/{task_id}
```

---

## 七、故障排查

### 7.1 API 服务无法启动
```bash
# 检查端口占用
lsof -i :8000

# 检查配置语法
cd /opt/smart-home/backend
/opt/smart-home/backend/venv/bin/python -c "from app.main import app; print('OK')"

# 查看错误日志
tail -50 /opt/smart-home/logs/api_error.log
```

### 7.2 Celery 任务不执行
```bash
# 检查 Celery 进程
ps aux | grep celery

# 检查 Redis 连接
redis-cli ping

# 查看错误日志
tail -50 /opt/smart-home/logs/celery_error.log
```

### 7.3 数据库连接失败
```bash
# 测试 MySQL 连接
mysql -u root -p -h 127.0.0.1 smart_home

# 检查 MySQL 服务
systemctl status mysql
```

---

## 八、访问地址

| 服务 | 地址 |
|------|------|
| API 文档 | http://8.137.174.58/docs |
| ReDoc | http://8.137.174.58/redoc |
| 健康检查 | http://8.137.174.58/health |
| 品牌列表 | http://8.137.174.58/api/v1/brands |
| 商品列表 | http://8.137.174.58/api/v1/products |
| 方案生成 | http://8.137.174.58/api/v1/schemes/generate |

---

## 九、部署历史

| 日期 | 版本 | 变更内容 |
|------|------|----------|
| 2026-03-24 | v1.1.0 | 添加自动化部署脚本，修复 Celery 配置 |
| 2026-03-23 | v1.0.1 | 修复 Redis/MySQL 连接地址，增加 AI 超时时间 |
| 2026-03-22 | v1.0.0 | 初始部署 |

---

*报告更新时间: 2026-03-24*
