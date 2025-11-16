#!/bin/bash

# ========================================
# 环境检查脚本
# ========================================

echo "========================================="
echo "电力设备故障诊断系统 - 环境检查"
echo "========================================="

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SUCCESS=0
FAILED=0

# 检查函数
check_command() {
    local cmd=$1
    local name=$2
    local version_cmd=$3

    echo -e "\n${BLUE}检查 $name...${NC}"
    if command -v $cmd &> /dev/null; then
        version=$($version_cmd 2>&1 | head -n 1)
        echo -e "${GREEN}✓ 已安装: $version${NC}"
        ((SUCCESS++))
        return 0
    else
        echo -e "${RED}✗ 未安装${NC}"
        ((FAILED++))
        return 1
    fi
}

# 检查服务
check_service() {
    local name=$1
    local check_cmd=$2

    echo -e "\n${BLUE}检查 $name...${NC}"
    if eval $check_cmd &> /dev/null; then
        echo -e "${GREEN}✓ 服务正常${NC}"
        ((SUCCESS++))
        return 0
    else
        echo -e "${RED}✗ 服务未启动或连接失败${NC}"
        ((FAILED++))
        return 1
    fi
}

# 检查开发工���
echo -e "\n${YELLOW}========== 开发工具检查 ==========${NC}"

check_command "java" "Java" "java -version"
check_command "mvn" "Maven" "mvn -version"
check_command "node" "Node.js" "node -v"
check_command "npm" "npm" "npm -v"
check_command "git" "Git" "git --version"

# 检查数据库服务
echo -e "\n${YELLOW}========== 数据库服务检查 ==========${NC}"

check_service "MySQL" "mysql -h localhost -P 3306 -u root -proot -e 'SELECT 1'"
check_service "Neo4j" "curl -s http://localhost:7474"
check_service "Redis" "redis-cli ping"

# 检查端口占用
echo -e "\n${YELLOW}========== 端口占用检查 ==========${NC}"

PORTS=(3306 7474 7687 6379 8081 8082 3000)
PORT_NAMES=("MySQL" "Neo4j HTTP" "Neo4j Bolt" "Redis" "知识图谱服务" "用户服务" "前端服务")

for i in "${!PORTS[@]}"; do
    port=${PORTS[$i]}
    name=${PORT_NAMES[$i]}

    if netstat -tuln 2>/dev/null | grep -q ":$port "; then
        echo -e "${YELLOW}端口 $port ($name): 已被占用${NC}"
    else
        echo -e "${GREEN}端口 $port ($name): 可用${NC}"
    fi
done

# 检查项目文件
echo -e "\n${YELLOW}========== 项目文件检查 ==========${NC}"

PROJECT_ROOT="$(dirname "$0")/.."
FILES=(
    "$PROJECT_ROOT/backend/pom.xml"
    "$PROJECT_ROOT/frontend/package.json"
    "$PROJECT_ROOT/scripts/init-mysql.sql"
    "$PROJECT_ROOT/scripts/init-neo4j.cypher"
)
FILE_NAMES=("后端pom.xml" "前端package.json" "MySQL初始化脚本" "Neo4j初始化脚本")

for i in "${!FILES[@]}"; do
    file=${FILES[$i]}
    name=${FILE_NAMES[$i]}

    if [ -f "$file" ]; then
        echo -e "${GREEN}✓ $name 存在${NC}"
        ((SUCCESS++))
    else
        echo -e "${RED}✗ $name 不存在: $file${NC}"
        ((FAILED++))
    fi
done

# 总结
echo -e "\n${YELLOW}=========================================${NC}"
echo -e "${YELLOW}检查完成${NC}"
echo -e "${YELLOW}=========================================${NC}"
echo -e "成功: ${GREEN}$SUCCESS${NC}"
echo -e "失败: ${RED}$FAILED${NC}"

if [ $FAILED -eq 0 ]; then
    echo -e "\n${GREEN}✓ 环境检查全部通过，可以启动项目！${NC}"
    exit 0
else
    echo -e "\n${RED}✗ 发现 $FAILED 个问题，请先解决后再启动项目${NC}"
    exit 1
fi
