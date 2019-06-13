#!/bin/bash
appl_name="MoneyCalcServer"
pid_file="${appl_name}.pid"
jmx_port=8085
ip_address=127.0.0.1

moneycalc_home=$( cd "$(dirname "${BASH_SOURCE}")" ; cd .. ; pwd -P )

jar_name=$( ls $moneycalc_home/lib/ | grep ${appl_name}.*.jar )

pid_file=$moneycalc_home/$pid_file
logback_file=$moneycalc_home/conf/logback.xml

JAVA_OPTS="-Xms256m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$jmx_port -Dcom.sun.management.jmxremote.rmi.port=$jmx_port -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=$ip_address"
APP_OPTS="-jar $moneycalc_home/lib/$jar_name --logging.config=$logback_file --spring.config.location=file:$moneycalc_home/conf/"

if [ "$2" == "any" ]
then
  any="true"
else
  any="false"
fi

start() {
    pid;
    if [ -z "$pid" ]
    then
        echo "Starting ${appl_name}..."
        if [ ! -d "$moneycalc_home/log" ]; then
            mkdir $moneycalc_home/log >/dev/null 2>/dev/null
        fi

        nohup java ${JAVA_OPTS} ${APP_OPTS} > /dev/null 2>/dev/null & echo $! > $pid_file
        pid=`cat $pid_file`
        echo $pid
    else
        echo "$appl_name is started already [$pid]"
    fi
}

status() {
    pid;
    if [ -z "$pid" ]
    then
        echo "$appl_name is not running..."
    else
        echo "$appl_name is running now [$pid]..."
    fi
}

stop() {
    pid;
    if [ -z "$pid" ]
    then
        echo "$appl_name is not running..."
    else
        echo "Stopping $appl_name [$pid]..."
        kill -9 $pid
    fi
    rm $pid_file >/dev/null 2>/dev/null
}

restart() {
    stop;
    start;
}

pid() {
    if [ "$any" == "true" ]
    then
        pid="moneyCalcServer"
    else
        if [ -f $pid_file ]
        then
          pid=`cat $pid_file`
        else
          pid=NO-CNC-PID
        fi
    fi
    pid=`ps -ef|grep $jar_name|grep $pid|grep -v grep|awk {'print $2'}`
}

case "$1" in
    start)   start ;;
    stop)    stop ;;
    restart) restart ;;
    status)  status ;;
    *) echo "usage: $0 start|stop|restart|status [any]" >&2
       exit 1
       ;;
esac