@echo off
setlocal enableextensions

rem --- Luôn chạy từ thư mục chứa script ---
cd /d "%~dp0"

rem --- Tùy chọn JVM ---
set "JAVA_OPTS=-server -XX:+UseParallelGC -Xms5g -Xmx5g -Xmn5g -Dfile.encoding=UTF-8"
rem Nếu đã cấp quyền Large Pages thì mở dòng sau:
rem set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseLargePages"

rem --- Log & heap dump khi OOM ---
if not exist "logs" mkdir "logs"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs\heapdump.hprof"

rem --- Tên JAR ---
set "JAR=dist\server.jar"

rem --- Tạo tên log theo thời gian ---
for /f "tokens=1-3 delims=/:. " %%a in ("%date% %time%") do (
  set "DS=%%c%%b%%a_%%d%%e%%f"
)
set "DS=%DS: =0%"

echo Launching DragonNgojc...
java %JAVA_OPTS% -jar "%JAR%"

set "ERR=%ERRORLEVEL%"
echo JVM exited with code %ERR%
if not "%ERR%"=="0" pause

endlocal
