#!/bin/bash

BUILD_ZIP_NAME=wishboard-v2-build
S3_BUCKET=$1
PHASE=$2

ZIP_NAME="$BUILD_ZIP_NAME-$PHASE"
DEPLOY_DIR="./$PHASE/current"

PM2_CONFIG_SOURCE="v2-pm2-run-$PHASE.js"
PM2_CONFIG_LINK="ecosystem.config.js"

PM2_APP_NAMES=(
  "wishboard-v2-api-server-$PHASE"
  "wishboard-v2-parsing-api-server-$PHASE"
  "wishboard-v2-push-scheduler-$PHASE"
)

echo ">> zip file name: $ZIP_NAME"
echo ">> deploy dir name: $DEPLOY_DIR"
echo ">> pm2 script name: $PM2_CONFIG_SOURCE"
echo ">> pm2 apps: ${PM2_APP_NAMES[*]}"

echo "********  Download new build from S3 ******** "
echo ">> aws s3 cp s3://$S3_BUCKET/$ZIP_NAME $DEPLOY_DIR --recursive"
aws s3 cp s3://$S3_BUCKET/$ZIP_NAME $DEPLOY_DIR --recursive

echo "******** Check download files ********"
cd $DEPLOY_DIR || exit 1

ls

echo "******** PM2 script start ********"
# PM2 config symlink 구성
rm -f "$PM2_CONFIG_LINK"
ln -s "$PM2_CONFIG_SOURCE" "$PM2_CONFIG_LINK"

# 파일 확인
ls -l "$PM2_CONFIG_LINK"

# pm2 process 존재 여부에 따라 start or reload
if pm2 list | grep -q "$PHASE"; then
  echo ">>> PM2 processes found for phase [$PHASE]. Reloading all..."
  pm2 reload "$PM2_CONFIG_LINK"
else
  echo ">>> PM2 processes not found for phase [$PHASE]. Starting fresh..."
  pm2 start "$PM2_CONFIG_LINK"
fi

# 상태 확인
pm2 list
