@echo off & setlocal enabledelayedexpansion

set LIB_JARS=""
cd ../

for %%i in (lib/*.jar) do set LIB_JARS=!LIB_JARS!;lib\%%i

java -Xms64m -Xmx1024m -classpath conf;%LIB_JARS%; cn.com.yitong.ares.AresSpringCloudApplication
