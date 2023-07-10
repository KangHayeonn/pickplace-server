#!/bin/bash

REPOSITORY=/home/ec2-user/cicdproject
cd $REPOSITORY

APP_NAME=pickplace
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME


# =====================================
# 현재 구동 중인 application pid 확인
# =====================================
CURRENT_PID=$(pgrep -fl demo | grep java | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then
    echo "NOT RUNNING"
else
    echo "> kill -9 $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
fi

echo "> $JAR_PATH 배포 완료"
nohup java -jar -Dspring.profiles.active=dev $JAR_PATH > /dev/null 2> /dev/null < /dev/null &
# swagger , spring 버전 관련 오류 해결위한 dev application.properties 적용
