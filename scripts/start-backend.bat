@echo off
chcp 65001 >nul
REM ========================================
REM 后端服务启动脚本（Windows）
REM ========================================

echo =========================================
echo 电力设备故障诊断系统 - 后端服务启动
echo =========================================

REM 检查Java环境
echo.
echo [1/6] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java，请先安装JDK 17+
    pause
    exit /b 1
)
java -version
echo [成功] Java环境检查通过

REM 检查Maven环境
echo.
echo [2/6] 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Maven，请先安装Maven 3.8+
    pause
    exit /b 1
)
mvn -version | findstr "Apache Maven"
echo [成功] Maven环境检查通过

REM 检查MySQL连接
echo.
echo [3/6] 检查MySQL连接...
mysql -h localhost -P 3306 -u root -proot -e "SELECT 1" >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] MySQL连接失败，请检查：
    echo   1. MySQL服务是否启动（net start mysql80）
    echo   2. 用户名密码是否为 root/root
    echo   3. 数据库 power_diagnosis 是否存在
    pause
    exit /b 1
)
echo [成功] MySQL连接成功

REM 检查Neo4j连接
echo.
echo [4/6] 检查Neo4j连接...
curl -s http://localhost:7474 >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] Neo4j连接失败，请先启动Neo4j服务
    echo   运行: neo4j.bat console
    pause
    exit /b 1
)
echo [成功] Neo4j连接成功

REM 检查Redis连接
echo.
echo [5/6] 检查Redis连接...
redis-cli ping >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] Redis连接失败，请启动Redis服务
    echo   如使用WSL: wsl -e sudo service redis-server start
    echo   如使用Windows版: redis-server.exe
    REM 不强制退出，继续运行
)
echo [成功] Redis连接成功

REM 进入后端目录
cd /d "%~dp0..\backend"

REM 编译项目
echo.
echo [6/6] 编译Maven项目...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo [错误] 项目编译失败
    pause
    exit /b 1
)
echo [成功] 项目编译成功

echo.
echo =========================================
echo 环境检查完成，准备启动服务...
echo =========================================

REM 启动知识图谱服务（新窗口）
echo.
echo 启动知识图谱服务（端口8081）...
start "知识图谱服务" cmd /k "cd modules\knowledge-graph-service && mvn spring-boot:run"

REM 等待5秒
timeout /t 5 /nobreak >nul

REM 启动用户服务（新窗口）
echo.
echo 启动用户服务（端口8082）...
start "用户服务" cmd /k "cd modules\user-service && mvn spring-boot:run"

echo.
echo =========================================
echo 后端服务启动完成！
echo =========================================
echo.
echo 知识图谱服务: http://localhost:8081
echo 用户服务:     http://localhost:8082
echo.
echo 服务将在新窗口中运行
echo 关闭窗口即可停止对应服务
echo.
pause
