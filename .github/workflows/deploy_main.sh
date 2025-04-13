#!/bin/bash
 CURRENT_PID=$(pgrep -f .jar)
 echo "$CURRENT_PID"
 if [ -z $CURRENT_PID ]; then
         echo "no process"
 else
         echo "kill $CURRENT_PID"
         kill -9 $CURRENT_PID
         sleep 3
 fi

 JAR_PATH=$(ls -t /home/ubuntu/cicd/*.jar | grep -v 'plain.jar' | head -n 1)
 echo "jar path : $JAR_PATH"
 chmod +x $JAR_PATH
 nohup java -jar $JAR_PATH --spring.profiles.active=common,dev 1> /dev/null 2>&1 &
 echo "jar fild deploy success"
