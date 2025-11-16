@echo off
chcp 65001 >nul
REM ========================================
REM 前端服务启动脚本（Windows）
REM ========================================

echo =========================================
echo 电力设备故障诊断系统 - 前端服务启动
echo =========================================

REM 检查Node.js环境
echo.
echo [1/3] 检查Node.js环境...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Node.js，请先安装Node.js 16+
    echo 下载地址: https://nodejs.org/
    pause
    exit /b 1
)
node -v
echo [成功] Node.js环境检查通过

REM 检查npm环境
npm -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到npm
    pause
    exit /b 1
)
npm -v
echo [成功] npm环境检查通过

REM 进入前端目录
cd /d "%~dp0..\frontend"

REM 检查依赖是否已安装
echo.
echo [2/3] 检查依赖...
if not exist "node_modules" (
    echo [提示] 首次运行，正在安装依赖（可能���要5-10分钟）...
    echo [提示] 如果下载缓慢，可以按Ctrl+C中断，然后运行：
    echo          npm config set registry https://registry.npmmirror.com
    echo.
    call npm install
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        echo.
        echo 解决方案:
        echo 1. 使用淘宝镜像: npm config set registry https://registry.npmmirror.com
        echo 2. 清除缓存重试: npm cache clean --force
        echo 3. 删除node_modules后重试
        pause
        exit /b 1
    )
    echo [成功] 依赖安装成功
) else (
    echo [成功] 依赖已安装
)

REM 启动开发服务器
echo.
echo [3/3] 启动开发服务器...
echo =========================================
echo 前端服务启动中...
echo =========================================
echo.
echo 访问地址: http://localhost:3000
echo 按 Ctrl+C 停止服务
echo.

call npm run dev
