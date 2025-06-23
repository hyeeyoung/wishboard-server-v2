#!/bin/bash

BUILD_ZIP_NAME=wishboard-v2-build
S3_BUCKET=$1
PHASE=$2

ZIP_NAME="$BUILD_ZIP_NAME-$PHASE.zip"
DEPLOY_DIR="/home/wishboard-v2/$PHASE/current"

PM2_SCRIPT_FILE="v2-pm2-run-config.js"
PM2_APP_NAME="v2-api-server-$PHASE"

echo ">> zip file name: $ZIP_NAME"
echo ">> deploy dir name: $DEPLOY_DIR"
echo ">> pm2 app name: $PM2_APP_NAME"

echo "********  Download new build from S3 ******** "
aws s3 cp s3://$S3_BUCKET/$ZIP_NAME $DEPLOY_DIR --recursive

echo "******** PM2 script start ********"
cd $DEPLOY_DIR

# pm2 list 출력에서 프로세스 이름 존재 여부 확인
if pm2 list | grep -q "$PM2_PROCESS_NAME"; then
  echo ">>> PM2 process [$PM2_PROCESS_NAME] exists. Reloading..."
  pm2 reload "$PM2_PROCESS_NAME"
else
  echo ">>> PM2 process [$PM2_PROCESS_NAME] not found. Starting..."
  pm2 start "$PM2_SCRIPT_FILE" --name "$PM2_PROCESS_NAME"
fi
