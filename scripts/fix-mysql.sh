#!/bin/bash

# ========================================
# MySQL问题修复脚本
# ========================================

echo "========================================="
echo "MySQL 问题诊断与修复"
echo "========================================="

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 检查是否为root
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}请使用sudo运行此脚本${NC}"
    echo "用法: sudo ./fix-mysql.sh"
    exit 1
fi

echo -e "\n${BLUE}[步骤1/6] 检查MySQL服务状态...${NC}"
systemctl status mysql --no-pager | head -10

echo -e "\n${BLUE}[步骤2/6] 停止MySQL服务...${NC}"
systemctl stop mysql
sleep 2
echo -e "${GREEN}✓ MySQL服务已停止${NC}"

echo -e "\n${BLUE}[步骤3/6] 检查MySQL错误日志...${NC}"
if [ -f /var/log/mysql/error.log ]; then
    echo "最近的错误信息："
    tail -20 /var/log/mysql/error.log
fi

echo -e "\n${BLUE}[步骤4/6] 检查磁盘空间...${NC}"
df -h /var/lib/mysql
DISK_USAGE=$(df /var/lib/mysql | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 90 ]; then
    echo -e "${RED}⚠ 警告: 磁盘使用率过高 ($DISK_USAGE%)${NC}"
else
    echo -e "${GREEN}✓ 磁盘空间充足${NC}"
fi

echo -e "\n${BLUE}[步骤5/6] 检查MySQL数据目录权限...${NC}"
ls -la /var/lib/mysql/ | head -10
chown -R mysql:mysql /var/lib/mysql
chmod -R 755 /var/lib/mysql
echo -e "${GREEN}✓ 权限已修复${NC}"

echo -e "\n${BLUE}[步骤6/6] 重新启动MySQL...${NC}"
systemctl start mysql

# 等待MySQL启动
echo "等待MySQL启动（最多30秒）..."
for i in {1..30}; do
    if systemctl is-active --quiet mysql; then
        echo -e "${GREEN}✓ MySQL启动成功！${NC}"
        break
    fi
    echo -n "."
    sleep 1
done

echo ""
echo -e "\n${BLUE}检查最终状态...${NC}"
systemctl status mysql --no-pager | head -10

# 测试连接
echo -e "\n${BLUE}测试MySQL连接...${NC}"
if mysql -u root -e "SELECT 1" 2>/dev/null; then
    echo -e "${GREEN}✓ MySQL连接成功！${NC}"
    mysql -u root -e "SELECT VERSION();"
else
    echo -e "${YELLOW}⚠ 无法使用root用户无密码登录${NC}"
    echo "这是正常的，如果您已设置了密码。"
    echo "请尝试: mysql -u root -p"
fi

echo -e "\n${GREEN}=========================================${NC}"
echo -e "${GREEN}MySQL修复完成！${NC}"
echo -e "${GREEN}=========================================${NC}"

echo -e "\n${BLUE}下一步操作:${NC}"
echo "1. 测试连接: mysql -u root -p"
echo "2. 如果需要重置密码，运行: sudo mysql"
echo "   然后执行: ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';"
echo "3. 初始化数据库: mysql -u root -p < scripts/init-mysql.sql"
