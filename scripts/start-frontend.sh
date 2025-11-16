#!/bin/bash

# ========================================
# 前端服务启动脚本（Linux/Mac）
# ========================================

echo "========================================="
echo "电力设备故障诊断系统 - 前端服务启动"
echo "========================================="

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 检查Node.js环境
echo -e "\n${YELLOW}[1/3] 检查Node.js环境...${NC}"
if command -v node &> /dev/null; then
    NODE_VERSION=$(node -v)
    echo -e "${GREEN}✓ Node.js版本: $NODE_VERSION${NC}"
else
    echo -e "${RED}✗ 未检测到Node.js，请先安装Node.js 16+${NC}"
    exit 1
fi

# 检查npm环境
if command -v npm &> /dev/null; then
    NPM_VERSION=$(npm -v)
    echo -e "${GREEN}✓ npm版本: $NPM_VERSION${NC}"
else
    echo -e "${RED}✗ 未检测到npm${NC}"
    exit 1
fi

# 进入前端目录
cd "$(dirname "$0")/../frontend" || exit

# 检查依赖是否已安装
echo -e "\n${YELLOW}[2/3] 检查依赖...${NC}"
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}首次运行，正在安装依赖...${NC}"
    npm install
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ 依赖安装成功${NC}"
    else
        echo -e "${RED}✗ 依赖安装失败${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✓ 依赖已安装${NC}"
fi

# 启动开发服务器
echo -e "\n${YELLOW}[3/3] 启动开发服务器...${NC}"
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}前端服务启动中...${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo "访问地址: http://localhost:3000"
echo "按 Ctrl+C 停止服务"
echo ""

npm run dev
