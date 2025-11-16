#!/bin/bash

# ========================================
# 一键安装所有依赖
# ========================================

echo "========================================="
echo "电力设备故障诊断系统 - 依赖安装脚本"
echo "========================================="

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}请使用sudo运行此脚本${NC}"
    echo "用法: sudo ./install-dependencies.sh"
    exit 1
fi

echo -e "\n${BLUE}此脚本将安装以下软件:${NC}"
echo "- Maven"
echo "- Redis"
echo "- Neo4j"
echo ""
read -p "按Enter继续，或Ctrl+C取消..."

# 更新软件源
echo -e "\n${YELLOW}[1/4] 更新软件源...${NC}"
apt update
echo -e "${GREEN}✓ 软件源更新完成${NC}"

# 安装Maven
echo -e "\n${YELLOW}[2/4] 安装Maven...${NC}"
if command -v mvn &> /dev/null; then
    echo -e "${GREEN}�� Maven已安装${NC}"
    mvn -version | head -1
else
    apt install -y maven
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Maven安装成功${NC}"
        mvn -version | head -1
    else
        echo -e "${RED}✗ Maven安装失败${NC}"
        exit 1
    fi
fi

# 安装Redis
echo -e "\n${YELLOW}[3/4] 安装Redis...${NC}"
if command -v redis-server &> /dev/null; then
    echo -e "${GREEN}✓ Redis已安装${NC}"
    redis-server --version
else
    apt install -y redis-server
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Redis安装成功${NC}"
        systemctl start redis-server
        systemctl enable redis-server
        echo -e "${GREEN}✓ Redis服务已启动${NC}"
        redis-server --version
    else
        echo -e "${RED}✗ Redis安装失败${NC}"
        exit 1
    fi
fi

# 安装Neo4j
echo -e "\n${YELLOW}[4/4] 安装Neo4j...${NC}"
if command -v neo4j &> /dev/null; then
    echo -e "${GREEN}✓ Neo4j已安装${NC}"
    neo4j version
else
    echo "安装Neo4j依赖..."
    apt install -y wget curl gnupg software-properties-common

    # 添加Neo4j仓库
    wget -O - https://debian.neo4j.com/neotechnology.gpg.key 2>/dev/null | apt-key add -
    echo 'deb https://debian.neo4j.com stable latest' > /etc/apt/sources.list.d/neo4j.list

    # 更新并安装
    apt update
    apt install -y neo4j

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Neo4j安装成功${NC}"

        # 配置Neo4j
        echo "配置Neo4j..."
        sed -i 's/#dbms.default_listen_address=0.0.0.0/dbms.default_listen_address=0.0.0.0/' /etc/neo4j/neo4j.conf

        # 启动Neo4j
        systemctl start neo4j
        systemctl enable neo4j
        echo -e "${GREEN}✓ Neo4j服务��启动${NC}"
        neo4j version
    else
        echo -e "${RED}✗ Neo4j安装失败${NC}"
        echo -e "${YELLOW}您可以稍后手动安装Neo4j${NC}"
    fi
fi

# 总结
echo -e "\n${GREEN}=========================================${NC}"
echo -e "${GREEN}依赖安装完成！${NC}"
echo -e "${GREEN}=========================================${NC}"

echo -e "\n${BLUE}已安装的软件:${NC}"
echo -n "Maven: "
if command -v mvn &> /dev/null; then
    echo -e "${GREEN}✓$(NC}"
else
    echo -e "${RED}✗${NC}"
fi

echo -n "Redis: "
if command -v redis-server &> /dev/null; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC}"
fi

echo -n "Neo4j: "
if command -v neo4j &> /dev/null; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC}"
fi

echo -e "\n${BLUE}服务状态:${NC}"
echo -n "MySQL: "
systemctl is-active --quiet mysql && echo -e "${GREEN}运行中${NC}" || echo -e "${YELLOW}未运行${NC}"

echo -n "Redis: "
systemctl is-active --quiet redis-server && echo -e "${GREEN}运行中${NC}" || echo -e "${YELLOW}未运行${NC}"

echo -n "Neo4j: "
systemctl is-active --quiet neo4j && echo -e "${GREEN}运行中${NC}" || echo -e "${YELLOW}未运行${NC}"

echo -e "\n${YELLOW}下一步:${NC}"
echo "1. 等待约30秒，让Neo4j完全启动"
echo "2. 访问 http://localhost:7474 设置Neo4j密码（改为: password）"
echo "3. 初始化数据库："
echo "   mysql -u root -p < scripts/init-mysql.sql"
echo "4. 编译并启动项目："
echo "   cd scripts && ./start-backend.sh"

echo -e "\n${GREEN}安装完成！${NC}"
