#!/bin/bash

# ========================================
# 后端服务启动脚本（Linux/Mac）
# ========================================

echo "========================================="
echo "电力设备故障诊断系统 - 后端服务启动"
echo "========================================="

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查Java环境
echo -e "\n${YELLOW}[1/6] 检查Java环境...${NC}"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo -e "${GREEN}✓ Java版本: $JAVA_VERSION${NC}"
else
    echo -e "${RED}✗ 未检测到Java，请先安装JDK 17+${NC}"
    exit 1
fi

# 检查Maven环境
echo -e "\n${YELLOW}[2/6] 检查Maven环境...${NC}"
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo -e "${GREEN}✓ $MVN_VERSION${NC}"
else
    echo -e "${RED}✗ 未检测到Maven，请先安装Maven 3.8+${NC}"
    exit 1
fi

# 检查MySQL连接
echo -e "\n${YELLOW}[3/6] 检查MySQL连接...${NC}"
if mysql -h localhost -P 3306 -u root -proot -e "SELECT 1" &> /dev/null; then
    echo -e "${GREEN}✓ MySQL连接成功${NC}"
else
    echo -e "${RED}✗ MySQL连接失败，请检查：${NC}"
    echo "   1. MySQL服务是否启动"
    echo "   2. 用户名密码是否为 root/root"
    echo "   3. 数据库 power_diagnosis 是否存在"
    exit 1
fi

# 检查Neo4j连接
echo -e "\n${YELLOW}[4/6] 检查Neo4j连接...${NC}"
if curl -s http://localhost:7474 > /dev/null; then
    echo -e "${GREEN}✓ Neo4j连接成功${NC}"
else
    echo -e "${RED}✗ Neo4j连接失败，请启动Neo4j服务${NC}"
    exit 1
fi

# 检查Redis连接
echo -e "\n${YELLOW}[5/6] 检查Redis连接...${NC}"
if redis-cli ping &> /dev/null; then
    echo -e "${GREEN}✓ Redis连接成功${NC}"
else
    echo -e "${RED}✗ Redis连接失败，请启动Redis服务${NC}"
    exit 1
fi

# 进入后端目录
cd "$(dirname "$0")/../backend" || exit

# 编译项目
echo -e "\n${YELLOW}[6/6] 编译Maven项目...${NC}"
mvn clean install -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 项目编译成功${NC}"
else
    echo -e "${RED}✗ 项目编译失败${NC}"
    exit 1
fi

echo -e "\n${GREEN}=========================================${NC}"
echo -e "${GREEN}环境检查完成，准备启动服务...${NC}"
echo -e "${GREEN}=========================================${NC}"

# 启动知识图谱服务
echo -e "\n${YELLOW}启动知识图谱服务（端口8081）...${NC}"
cd modules/knowledge-graph-service
mvn spring-boot:run &
KG_PID=$!
echo "知识图谱服务 PID: $KG_PID"

# 等待5秒
sleep 5

# 启动用户服务
echo -e "\n${YELLOW}启动用户服务（端口8082）...${NC}"
cd ../user-service
mvn spring-boot:run &
USER_PID=$!
echo "用户服务 PID: $USER_PID"

echo -e "\n${GREEN}=========================================${NC}"
echo -e "${GREEN}后端服务启动完成！${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo "知识图谱服务: http://localhost:8081 (PID: $KG_PID)"
echo "用户服务:     http://localhost:8082 (PID: $USER_PID)"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待用户中断
trap "echo ''; echo '正在停止服务...'; kill $KG_PID $USER_PID; exit" INT
wait
