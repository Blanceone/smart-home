#!/bin/bash

BACKUP_DIR="/opt/backups"
PROJECT_DIR="/opt/smart-home"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=7

mkdir -p $BACKUP_DIR

echo "Starting backup at $(date)"

echo "Backing up database..."
docker-compose -f $PROJECT_DIR/docker-compose.yml exec -T postgres pg_dump -U postgres smart_home > $BACKUP_DIR/db_$DATE.sql

echo "Compressing backup..."
gzip $BACKUP_DIR/db_$DATE.sql

echo "Backing up environment files..."
cp $PROJECT_DIR/server/.env $BACKUP_DIR/env_$DATE.bak 2>/dev/null || echo "No .env file found"

echo "Cleaning up old backups..."
find $BACKUP_DIR -name "*.sql.gz" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -name "*.bak" -mtime +$RETENTION_DAYS -delete

echo "Backup completed at $(date)"
echo "Backup files:"
ls -lh $BACKUP_DIR
