#!/usr/bin/env bash

if [[ -v "{hostname}" ]]; then
   export HOSTNAME=$(hostname)
fi

if [[ -z "$MEMORY" ]]; then
   export MEMORY=4096
fi

if [[ -z "$COMMAND" ]]; then
   export COMMAND=server
fi

echo "Using memory " $MEMORY

if [[ -f /mongo/ca.pem ]]; then
  echo "Adding Mongo CA file to truststore"
  keytool -importcert -trustcacerts -file /mongo/ca.pem -keystore keystore.jks -storepass password -noprompt -alias mongoca
fi

if [[ -f /mongo/client.pem ]]; then
  echo "Adding Mongo Client pem file to truststore"
  keytool -importcert -trustcacerts -file /mongo/client.pem -keystore keystore.jks -storepass password -noprompt -alias mongoclient
fi

if [[ -z "$CAPSULE_JAR" ]]; then
   export CAPSULE_JAR=/opt/harness/rest-capsule.jar
fi

if [[ -z "$NEWRELIC_ENV" ]]; then
   export NEWRELIC_ENV=dev
fi

if [[ "${ENABLE_G1GC}" == "true" ]]; then
    export GC_PARAMS=" -XX:+UseG1GC -XX:InitiatingHeapOccupancyPercent=40 -XX:MaxGCPauseMillis=1000 -Dfile.encoding=UTF-8"
else
    if [[ "${ENABLE_SERIALGC}" == "true" ]]; then
        export GC_PARAMS=" -XX:+UseSerialGC -Dfile.encoding=UTF-8"
    else
        export GC_PARAMS=" -XX:+UseParallelGC -XX:MaxGCPauseMillis=500 -Dfile.encoding=UTF-8"
    fi
fi

export JAVA_OPTS="-Xmx${MEMORY}m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/heapdump -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:mygclogfilename.gc $GC_PARAMS $JAVA_ADVANCED_FLAGS"


if [[ "${ENABLE_APPDYNAMICS}" == "true" ]] && [[ "${DISABLE_NEW_RELIC}" == "true" ]]; then
    mkdir /opt/harness/AppServerAgent-20.8.0.30686 && unzip AppServerAgent-20.8.0.30686.zip -d /opt/harness/AppServerAgent-20.8.0.30686
    node_name="-Dappdynamics.agent.nodeName=$(hostname)"
    JAVA_OPTS=$JAVA_OPTS" -javaagent:/opt/harness/AppServerAgent-20.8.0.30686/javaagent.jar -Dappdynamics.jvm.shutdown.mark.node.as.historical=true"
    JAVA_OPTS="$JAVA_OPTS $node_name"
    echo "Using Appdynamics java agent"
fi

if [[ "${DISABLE_NEW_RELIC}" != "true" ]]; then
    JAVA_OPTS=$JAVA_OPTS" -Dnewrelic.environment=$NEWRELIC_ENV"
    echo "Using new relic env " $NEWRELIC_ENV
fi

if [[ "${ENABLE_OVEROPS}" == "true" ]] ; then
    echo "OverOps is enabled"
    JAVA_OPTS=$JAVA_OPTS" -agentpath:/opt/harness/takipi/lib/libTakipiAgent.so -Dtakipi.etl -Dtakipi.application.name=${OVEROPS_APPLICATION_NAME}"
    echo "Using Overops Java Agent"
fi

JAVA_OPTS=$JAVA_OPTS" -Xbootclasspath/p:/opt/harness/alpn-boot-8.1.13.v20181017.jar"

if [[ "${DEPLOY_MODE}" == "KUBERNETES" || "${DEPLOY_MODE}" == "KUBERNETES_ONPREM" || "${DEPLOY_VERSION}" == "COMMUNITY" ]]; then
    java $JAVA_OPTS -jar $CAPSULE_JAR $COMMAND /opt/harness/config.yml
else
    if [[ "${ROLLING_FILE_LOGGING_ENABLED}" == "true" ]]; then
        java $JAVA_OPTS -jar $CAPSULE_JAR $COMMAND /opt/harness/config.yml
    else
        java $JAVA_OPTS -jar $CAPSULE_JAR $COMMAND /opt/harness/config.yml > /opt/harness/logs/portal.log 2>&1
    fi
fi
