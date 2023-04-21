#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf

SERVER_NAME=`sed '/^spring.application.name/!d;s/.*=//' conf/application.properties | tr -d '\r'`


if [ -z "$SERVER_NAME" ]; then
    SERVER_NAME=`hostname`
fi

PIDS=`ps -ef | grep java | grep "$CONF_DIR" | awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME does not started!"
    exit 1
fi

if [ "$1" != "skip" ]; then
    $BIN_DIR/dump.sh
fi

echo -e "Stopping the $SERVER_NAME ...\c"
for PID in $PIDS ; do
    kill $PID > /dev/null 2>&1
done

COUNT=0
#最大检测次数，如果超过这个次数，则强制kill进程
MAX_COUNT=10
CUR_COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $PIDS ; do
        PID_EXIST=`ps -p $PID | grep java | grep -v grep`
        if [ -n "$PID_EXIST" ]; then
            CUR_COUNT=$[$CUR_COUNT+1]
            COUNT=0
            if [[ $CUR_COUNT -gt $MAX_COUNT ]]; then
                kill -9 $PID
                CUR_COUNT=0
            fi
            break
        fi
    done
done

echo "OK!"
echo "PID: $PIDS"
