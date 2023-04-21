#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf


FULL_PARAMS=$*



SERVER_PORT=`sed '/^server.port/!d;s/.*=//' conf/application.properties | tr -d '\r'`
SERVER_NAME=`sed '/^spring.application.name/!d;s/.*=//' conf/application.properties | tr -d '\r'`

echo "服务名称：${SERVER_NAME}"
echo "服务端口：${SERVER_PORT}"


PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [ -n "$SERVER_PORT" ]; then
    SERVER_PORT_COUNT=`netstat -tln | grep ":$SERVER_PORT " | wc -l`
    if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
        exit 1
    fi
fi


LOGS_DIR=$DEPLOY_DIR/logs

if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/stdout.log

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR | grep .jar | awk '{print "'$LIB_DIR'/"$0}' | tr "\n" ":"`

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_DEBUG_OPTS=""

JAVA_MEM_OPTS=" -server -Xmx2g -Xms256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xss256k -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -XX:+UseParallelGC -XX:ParallelGCThreads=8"


echo -e "Starting the $SERVER_NAME ...\c"
nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -classpath $CONF_DIR:$LIB_JARS cn.com.yitong.ares.AresSpringCloudApplication ${FULL_PARAMS} > $STDOUT_FILE 2>&1 &

COUNT=0
#最大检测次数，如果超过这个次数，跳过检测
MAX_COUNT=30
CUR_COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1 
    if [ -n "$SERVER_PORT" ]; then
     COUNT=`netstat -tan | grep $SERVER_PORT | wc -l`
    fi
    if [ $COUNT -gt 0 ]; then
        break
    fi
    CUR_COUNT=$[$CUR_COUNT+1]
    if [ $CUR_COUNT -gt $MAX_COUNT ]; then
        echo ""
        echo "check start status timeout , break check , please check the final status"
        break
    fi
done

PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
