#!/bin/bash

rm -r logs/iptv-spark.log

spark-submit \
     --verbose \
     --master yarn \
     --class com.qbao.search.engine.MsServer \
     --executor-cores 4 \
     --num-executors 3 \
     --executor-memory 5G \
     --driver-memory 5G  \
     --conf spark.driver.extraJavaOptions="-Xss100M -XX:PermSize=256m -XX:MaxPermSize=256m" \
     iptv-spark-jar-with-dependencies.jar  -server  $* 1>"logs/iptv-spark.log" 2>&1 & 

tailf logs/iptv-spark.log


