#!/bin/bash

cd /d/work/ai/smart_home_deg/backend

echo "Starting backend server..."
echo "API documentation available at: http://localhost:8000/docs"

uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
