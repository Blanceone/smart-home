#!/bin/bash

set -e

SERVER_IP="8.137.174.58"
SERVER_USER="root"
SSH_KEY="C:\\Users\\13979\\Desktop\\notes\\lxh的秘钥.pem"
REMOTE_DIR="/opt/smart-home/backend"
LOCAL_BACKEND="d:\\work\\ai\\smart_home_deg\\backend"

echo "=========================================="
echo "  智能家居系统自动化部署脚本"
echo "  时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="

echo ""
echo "[1/6] 同步后端代码到服务器..."
scp -i "$SSH_KEY" "$LOCAL_BACKEND\\.env" $SERVER_USER@$SERVER_IP:$REMOTE_DIR/.env
scp -i "$SSH_KEY" "$LOCAL_BACKEND\\app\\main.py" $SERVER_USER@$SERVER_IP:$REMOTE_DIR/app/main.py
scp -i "$SSH_KEY" "$LOCAL_BACKEND\\app\\celery_app.py" $SERVER_USER@$SERVER_IP:$REMOTE_DIR/app/celery_app.py
scp -i "$SSH_KEY" "$LOCAL_BACKEND\\celery_tasks\\generation.py" $SERVER_USER@$SERVER_IP:$REMOTE_DIR/celery_tasks/generation.py
scp -i "$SSH_KEY" "$LOCAL_BACKEND\\app\\modules\\scheme\\routes.py" $SERVER_USER@$SERVER_IP:$REMOTE_DIR/app/modules/scheme/routes.py
scp -i "$SSH_KEY" "$LOCAL_BACKEND\\app\\modules\\scheme\\schemas.py" $SERVER_USER@$SERVER_IP:$REMOTE_DIR/app/modules/scheme/schemas.py
scp -i "$SSH_KEY" "$LOCAL_BACKEND\\app\\modules\\house\\routes.py" $SERVER_USER@$SERVER_IP:$REMOTE_DIR/app/modules/house/routes.py
scp -i "$SSH_KEY" "$LOCAL_BACKEND\\app\\core\\config.py" $SERVER_USER@$SERVER_IP:$REMOTE_DIR/app/core/config.py
echo "  ✅ 代码同步完成"

echo ""
echo "[2/6] 修复配置文件中的连接地址..."
ssh -i "$SSH_KEY" $SERVER_USER@$SERVER_IP "sed -i 's/8.137.174.58/127.0.0.1/g' $REMOTE_DIR/.env"
echo "  ✅ 配置修复完成"

echo ""
echo "[3/6] 重启 FastAPI 服务..."
ssh -i "$SSH_KEY" $SERVER_USER@$SERVER_IP "systemctl restart smart-home-api"
echo "  ✅ FastAPI 服务已重启"

echo ""
echo "[4/6] 重启 Celery Worker..."
ssh -i "$SSH_KEY" $SERVER_USER@$SERVER_IP "pkill -f celery || true; sleep 2; cd $REMOTE_DIR && nohup /opt/smart-home/backend/venv/bin/celery -A app.celery_app:celery_app worker --loglevel=info --concurrency=4 >> /opt/smart-home/logs/celery.log 2>&1 &"
sleep 3
echo "  ✅ Celery Worker 已重启"

echo ""
echo "[5/6] 验证服务状态..."
ssh -i "$SSH_KEY" $SERVER_USER@$SERVER_IP "
    echo '  服务状态:'
    systemctl is-active mysql redis-server nginx smart-home-api
    echo ''
    echo '  Celery 进程:'
    ps aux | grep 'celery.*worker' | grep -v grep | wc -l
    echo ''
    echo '  API 健康检查:'
    curl -s http://127.0.0.1:8000/health
    echo ''
    echo '  任务注册验证:'
    cd $REMOTE_DIR && /opt/smart-home/backend/venv/bin/celery -A app.celery_app:celery_app inspect registered 2>&1 | grep -E 'generation|crawl' | head -5
"
echo "  ✅ 服务验证完成"

echo ""
echo "[6/6] 测试方案生成功能..."
ssh -i "$SSH_KEY" $SERVER_USER@$SERVER_IP "
    cd $REMOTE_DIR && /opt/smart-home/backend/venv/bin/python3 -c '
import requests
r = requests.post(\"http://127.0.0.1:8000/api/v1/schemes/generate\", json={
    \"house_layout\": {\"total_area\": 80, \"rooms\": [{\"room_name\": \"LR\", \"room_type\": \"living_room\", \"length\": 5, \"width\": 4}]},
    \"questionnaire\": {\"living_status\": \"owner\", \"resident_count\": 2, \"preferred_scenarios\": [\"lighting\"]},
    \"preferences\": {\"budget_min\": 1000, \"budget_max\": 10000}
})
print(\"  API 响应:\", r.status_code, r.json().get(\"message\", \"OK\")[:30])
'
"
echo "  ✅ 功能测试完成"

echo ""
echo "=========================================="
echo "  ✅ 部署完成!"
echo "  API: http://$SERVER_IP/api/v1/"
echo "  文档: http://$SERVER_IP/docs"
echo "=========================================="
