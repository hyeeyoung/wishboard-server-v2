#!/bin/bash

PM2_SCRIPT_FILE="v2-pm2-run-config.js"
PM2_APP_NAME="v2-api-server-$PHASE"

# pm2 list 출력에서 프로세스 이름 존재 여부 확인
if pm2 list | grep -q "$PM2_PROCESS_NAME"; then
  echo ">>> PM2 process [$PM2_PROCESS_NAME] exists. Reloading..."
  pm2 reload "$PM2_PROCESS_NAME"
else
  echo ">>> PM2 process [$PM2_PROCESS_NAME] not found. Starting..."
  pm2 start "$PM2_SCRIPT_FILE" --name "$PM2_PROCESS_NAME"
fi
