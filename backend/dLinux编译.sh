#!/bin/bash

export GRAALVM_HOME=/home/Jackxwb/tmp/graalvm-21
export JAVA_HOME=$GRAALVM_HOME
export PATH=$JAVA_HOME/bin:$PATH

# mvnd
export PATH=/home/Jackxwb/tmp/mvnd/bin/bin:$PATH

java -version

cd /home/Jackxwb/tmp/MyJmcomicManage/

./mvnw package -Dnative -DskipTests=true -Dquarkus.native.target-platform="linux" -Dquarkus.native.container-runtime-options="--platform=linux/amd64" -Dquarkus.native.additional-build-args="-march=x86-64"

upx /home/Jackxwb/tmp/MyJmcomicManage/target/my_jmcomic_manage-1.0-SNAPSHOT-runner

md5sum /home/Jackxwb/tmp/MyJmcomicManage/target/my_jmcomic_manage-1.0-SNAPSHOT-runner