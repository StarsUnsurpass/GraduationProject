@echo off
chcp 65001 >nul
REM ========================================
REM 环境检查脚本（Windows）
REM ========================================

echo =========================================
echo 电力设备故障诊断系统 - 环境检查
echo =========================================

set SUCCESS=0
set FAILED=0

REM ========== 开发工具检查 ==========
echo.
echo ========== 开发工具检查 ==========

echo.
echo 检查 Java...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [成功] Java 已安装
    java -version 2>&1 | findstr "version"
    set /a SUCCESS+=1
) else (
    echo [失败] Java 未安装
    set /a FAILED+=1
)

echo.
echo 检查 Maven...
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [成功] Maven 已安装
    mvn -version 2>&1 | findstr "Apache Maven"
    set /a SUCCESS+=1
) else (
    echo [失败] Maven 未安装
    set /a FAILED+=1
)

echo.
echo 检查 Node.js...
node -v >nul 2>&1
if %errorlevel% equ 0 (
    echo [成功] Node.js 已安装
    node -v
    set /a SUCCESS+=1
) else (
    echo [失败] Node.js 未安装
    set /a FAILED+=1
)

echo.
echo 检查 npm...
npm -v >nul 2>&1
if %errorlevel% equ 0 (
    echo [成功] npm 已安装
    npm -v
    set /a SUCCESS+=1
) else (
    echo [失败] npm 未安装
    set /a FAILED+=1
)

echo.
echo 检查 Git...
git --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [成功] Git 已安装
    git --version
    set /a SUCCESS+=1
) else (
    echo [失败] Git 未安装
    set /a FAILED+=1
)

REM ========== 数据库服务检查 ==========
echo.
echo ========== 数据库服务检查 ==========

echo.
echo 检查 MySQL...
mysql -h localhost -P 3306 -u root -proot -e "SELECT 1" >nul 2>&1
if %errorlevel% equ 0 (
    echo [成功] MySQL 服务正常
    set /a SUCCESS+=1
) else (
    echo [失败] MySQL 服务未启动或连接失败
    echo   提示: net start mysql80
    set /a FAILED+=1
)

echo.
echo 检查 Neo4j...
curl -s http://localhost:7474 >nul 2>&1
if %errorlevel% equ 0 (
    echo [成功] Neo4j 服务正常
    set /a SUCCESS+=1
) else (
    echo [失败] Neo4j 服务未启动
    echo   提示: neo4j.bat console
    set /a FAILED+=1
)

echo.
echo 检查 Redis...
redis-cli ping >nul 2>&1
if %errorlevel% equ 0 (
    echo [成功] Redis 服务正常
    set /a SUCCESS+=1
) else (
    echo [失败] Redis 服务未启动
    echo   提示: wsl -e sudo service redis-server start
    set /a FAILED+=1
)

REM ========== 端口占用检查 ==========
echo.
echo ========== 端口占用检查 ==========

echo.
echo 检查端口占用情况...
netstat -ano | findstr ":3306 :7474 :7687 :6379 :8081 :8082 :3000" >nul 2>&1
if %errorlevel% equ 0 (
    echo 以下端口正在被占用:
    netstat -ano | findstr ":3306 :7474 :7687 :6379 :8081 :8082 :3000"
) else (
    echo [成功] 所有端口都可用
)

REM ========== 项目文件检查 ==========
echo.
echo ========== 项目文件检查 ==========

echo.
echo 检查 后端pom.xml...
if exist "%~dp0..\backend\pom.xml" (
    echo [成功] 文件存在
    set /a SUCCESS+=1
) else (
    echo [失败] 文件不存在
    set /a FAILED+=1
)

echo.
echo 检查 前端package.json...
if exist "%~dp0..\frontend\package.json" (
    echo [成功] 文件存在
    set /a SUCCESS+=1
) else (
    echo [失败] 文件不存在
    set /a FAILED+=1
)

echo.
echo 检查 MySQL初始化脚本...
if exist "%~dp0init-mysql.sql" (
    echo [成功] 文件存在
    set /a SUCCESS+=1
) else (
    echo [失败] 文件不存在
    set /a FAILED+=1
)

echo.
echo 检查 Neo4j初始化脚本...
if exist "%~dp0init-neo4j.cypher" (
    echo [成功] 文件存在
    set /a SUCCESS+=1
) else (
    echo [失败] 文件不存在
    set /a FAILED+=1
)

REM ========== 总结 ==========
echo.
echo =========================================
echo 检查完成
echo =========================================
echo 成功: %SUCCESS%
echo 失败: %FAILED%
echo.

if %FAILED% equ 0 (
    echo [成功] 环境检查全部通过，可以启动项目！
) else (
    echo [失败] 发现 %FAILED% 个问题，请先解决后再启动项目
)

echo.
pause
