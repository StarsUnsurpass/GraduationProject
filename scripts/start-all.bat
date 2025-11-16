@echo off
chcp 65001 >nul
REM ========================================
REM 一键启动所有服务（Windows）
REM ========================================

echo =========================================
echo 电力设备故障诊断系统 - 一键启动
echo =========================================
echo.
echo 本脚本将依次启动：
echo   1. 后端服务（知识图谱服务 + 用户服务）
echo   2. 前端服务
echo.
echo 请确保已经启动：
echo   - MySQL (端口3306)
echo   - Neo4j (端口7474/7687)
echo   - Redis (端口6379)
echo.
pause

REM 启动后端服务
echo.
echo =========================================
echo 启动后端服务...
echo =========================================
call "%~dp0start-backend.bat"

REM 等待后端服务启动
echo.
echo 等待后端服务启动（10秒）...
timeout /t 10 /nobreak >nul

REM 启动前端服务
echo.
echo =========================================
echo 启动前端服务...
echo =========================================
start "前端服务" cmd /k "cd /d %~dp0 && start-frontend.bat"

echo.
echo =========================================
echo 所有服务启动完成！
echo =========================================
echo.
echo 后端服务:
echo   - 知识图谱服务: http://localhost:8081
echo   - 用户服务:     http://localhost:8082
echo.
echo 前端服务:
echo   - 访问地址:     http://localhost:3000
echo   - 默认账号:     admin / admin123
echo.
echo 按任意键退出（不会停止服务）
pause >nul
