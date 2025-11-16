# 本地部署安装步骤

## 当前环境状态

✅ Java 21 - 已安装
❌ Maven - 需要安装
✅ Node.js 20.19.5 - 已安装
✅ npm 10.8.2 - 已安装
✅ MySQL 8.0.43 - 已安装
❌ Redis - 需要安装
❓ Neo4j - 需要安装

---

## 步骤1：安装缺失的软件

### 1.1 安装Maven

```bash
# 更新软件源
sudo apt update

# 安装Maven
sudo apt install -y maven

# 验证安装
mvn -version
```

### 1.2 安装Redis

```bash
# 安装Redis
sudo apt install -y redis-server

# 启动Redis
sudo systemctl start redis-server

# 设置开机自启
sudo systemctl enable redis-server

# 验证安装
redis-cli ping
# 应该返回: PONG
```

### 1.3 安装Neo4j

```bash
# 添加Neo4j仓库
wget -O - https://debian.neo4j.com/neotechnology.gpg.key | sudo apt-key add -
echo 'deb https://debian.neo4j.com stable latest' | sudo tee /etc/apt/sources.list.d/neo4j.list

# 更新软件源
sudo apt update

# 安装Neo4j Community Edition
sudo apt install -y neo4j

# 启动Neo4j
sudo systemctl start neo4j

# 设置开机自启
sudo systemctl enable neo4j

# 验证安装（等待约30秒后执行）
curl http://localhost:7474
```

---

## 步骤2：启动MySQL服务

```bash
# 启动MySQL
sudo systemctl start mysql

# 检查状态
sudo systemctl status mysql

# 如果未设置root密码，先设置密码
sudo mysql
```

在MySQL提示符中执行：
```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
EXIT;
```

---

## 步骤3：初始化MySQL数据库

```bash
# 登录MySQL
mysql -u root -p
# 输入密码: root
```

在MySQL提示符中执行：
```sql
-- 执行初始化脚本
source /mnt/e/Code/GraduationProject/scripts/init-mysql.sql;

-- 验证数据库创建
SHOW DATABASES;
USE power_diagnosis;
SHOW TABLES;
SELECT COUNT(*) FROM sys_user;

-- 退出MySQL
EXIT;
```

---

## 步骤4：初始化Neo4j

### 4.1 首次登录Neo4j

```bash
# 打开浏览器访问
http://localhost:7474

# 默认登录信息
用户名: neo4j
密码: neo4j

# 首次登录会要求修改密码，改为: password
```

### 4.2 执行初始化脚本

方法一：使用Neo4j浏览器（推荐）

1. 访问 http://localhost:7474
2. 登录（neo4j / password）
3. 复制下面的命令到查询框执行：

```bash
# 先查看初始化脚本内容
cat /mnt/e/Code/GraduationProject/scripts/init-neo4j.cypher
```

4. 将脚本内容复制到Neo4j浏览器中执行

方法二：使用命令行

```bash
# 使用cypher-shell执行
cat /mnt/e/Code/GraduationProject/scripts/init-neo4j.cypher | cypher-shell -u neo4j -p password
```

### 4.3 验证数据导入

在Neo4j浏览器中执行：
```cypher
// 查看节点数量
MATCH (n) RETURN count(n) as nodeCount;
// 应该返回 18

// 查看关系数量
MATCH ()-[r]->() RETURN count(r) as relationshipCount;
// 应该返回 14

// 查看设备列表
MATCH (e:Equipment) RETURN e.name, e.type;
```

---

## 步骤5：编译后端项目

```bash
# 进入后端目录
cd /mnt/e/Code/GraduationProject/backend

# 清理并编译项目
mvn clean install -DskipTests

# 如果编译失败，尝试：
mvn clean install -DskipTests -U
```

---

## 步骤6：启动后端服务

### 方法一：使用脚本启动（推荐）

```bash
cd /mnt/e/Code/GraduationProject/scripts
./start-backend.sh
```

### 方法二：手动启动

打开两个终端窗口：

**终端1 - 启动知识图谱服务：**
```bash
cd /mnt/e/Code/GraduationProject/backend/modules/knowledge-graph-service
mvn spring-boot:run
```

**终端2 - 启动用户服务：**
```bash
cd /mnt/e/Code/GraduationProject/backend/modules/user-service
mvn spring-boot:run
```

等待看到以下输出表示启动成功：
```
========================================
知识图谱服务启动成功！
========================================
```

---

## 步骤7：安装前端依赖

```bash
# 进入前端目录
cd /mnt/e/Code/GraduationProject/frontend

# 安装依赖
npm install

# 如果速度慢，使用淘宝镜像
npm config set registry https://registry.npmmirror.com
npm install
```

---

## 步骤8：启动前端服务

### 方法一：使用脚本启动

```bash
cd /mnt/e/Code/GraduationProject/scripts
./start-frontend.sh
```

### 方法二：手动启动

```bash
cd /mnt/e/Code/GraduationProject/frontend
npm run dev
```

浏览器会自动打开 http://localhost:3000

---

## 步骤9：验证系统运行

### 9.1 检查所有服务

```bash
# 检查端口占用
netstat -tuln | grep "3306\|7474\|7687\|6379\|8081\|8082\|3000"
```

应该看到所有端口都在监听：
- 3306 - MySQL
- 7474, 7687 - Neo4j
- 6379 - Redis
- 8081 - 知识图谱服务
- 8082 - 用户服务
- 3000 - 前端服务

### 9.2 访问系统

1. 打开浏览器访问：http://localhost:3000
2. 使用默认账号登录：
   - 用户名：`admin`
   - 密码：`admin123`

### 9.3 测试功能

- [ ] 能够正常登录
- [ ] 可以看到仪表盘
- [ ] 各个菜单可以访问
- [ ] 知识图谱数据正常显示

---

## 快速命令参考

### 启动所有数据库服务

```bash
# 启动MySQL
sudo systemctl start mysql

# 启动Redis
sudo systemctl start redis-server

# 启动Neo4j
sudo systemctl start neo4j

# 检查所有服务状态
sudo systemctl status mysql redis-server neo4j
```

### 停止所有服务

```bash
# 停止后端（在运行的终端按 Ctrl+C）

# 停止前端（在运行的终端按 Ctrl+C）

# 停止数据库（可选）
sudo systemctl stop mysql redis-server neo4j
```

### 查看日志

```bash
# 后端日志（在运行的终端查看）

# 前端日志（在运行的终端查看）

# MySQL日志
sudo tail -f /var/log/mysql/error.log

# Neo4j日志
sudo tail -f /var/log/neo4j/neo4j.log

# Redis日志
sudo tail -f /var/log/redis/redis-server.log
```

---

## 常见问题解决

### 问题1：Maven编译失败

```bash
# 清除Maven缓存
rm -rf ~/.m2/repository

# 重新编译
cd /mnt/e/Code/GraduationProject/backend
mvn clean install -U
```

### 问题2：MySQL连接失败

```bash
# 检查MySQL服务
sudo systemctl status mysql

# 重启MySQL
sudo systemctl restart mysql

# 检查密码
mysql -u root -p
```

### 问题3：Neo4j连接失败

```bash
# 检查Neo4j服务
sudo systemctl status neo4j

# 重启Neo4j
sudo systemctl restart neo4j

# 等待30秒后访问
curl http://localhost:7474
```

### 问题4：端口被占用

```bash
# 查找占用端口的进程
sudo lsof -i :8081

# 结束进程
sudo kill -9 [PID]
```

### 问题5：npm install很慢

```bash
# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 清除缓存重试
npm cache clean --force
npm install
```

---

## 下一步

部署完成后，您可以：

1. ✅ 查看项目文档：`README.md`
2. ✅ 学习系统架构：`docs/PROJECT_SUMMARY.md`
3. ✅ 开始功能开发
4. ✅ 编写毕业论文

---

**祝您部署顺利！** 🎉

如有问题，请参考：
- 详细部署指南：`docs/DEPLOYMENT_GUIDE.md`
- 快速参考：`QUICK_REFERENCE.md`
