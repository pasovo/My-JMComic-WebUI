@echo off
set GRAALVM_HOME=D:\Program Files\java\graalvm-jdk-21.0.7+8.1
set JAVA_HOME=%GRAALVM_HOME%
set PATH=%GRAALVM_HOME%\bin;C:\Users\Jackxwb\.jbang\bin;%PATH%

mode con cols=300 lines=70

call "D:\VS\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat" > null

echo ===================
java -version
echo ===================
echo %GRAALVM_HOME%
echo ===================

:./mvnw package -Dnative -DskipTests=true -Dquarkus.native.target-platform="window" -Dquarkus.native.container-runtime-options="--platform=linux/amd64" -Dquarkus.native.additional-build-args="-march=x86-64"
mvnd -T 1C package -Dnative -DskipTests=true -Dquarkus.native.additional-build-args="-march=x86-64" -DskipCodeGeneration=true

@pause