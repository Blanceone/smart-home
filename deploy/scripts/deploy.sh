#!/bin/bash

PROJECT_DIR="/opt/smart-home"

echo "Starting deployment at $(date)"

cd $PROJECT_DIR

echo "Pulling latest code..."
git pull origin main

echo "Rebuilding and restarting services..."
docker-compose down
docker-compose up -d --build

echo "Waiting for services to start..."
sleep 30

echo "Running database migrations..."
docker-compose exec -T app npx prisma migrate deploy

echo "Checking service health..."
docker-compose ps

echo "Performing health check..."
curl -f http://localhost:3000/v1/health || {
    echo "Health check failed!"
    exit 1
}

echo "Deployment completed successfully at $(date)"
