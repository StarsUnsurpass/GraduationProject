# 项目部署与运行指南

本文档详细介绍如何在本地开发环境和生产环境中部署和运行本项目。

---

## 目录

- [方式一：本地开发运行（推荐初学者）](#方式一本地开发运行)
- [方式二：Docker容器化部署（推荐生产环境）](#方式二docker容器化部署)
- [方式三：混合部署（开发调试）](#方式三混合部署)
- [验证系统运行](#验证系统运行)
- [故障排查](#故障排查)

---

## 方式一：本地开发运行

适合：开发调试、功能开发、学习研究

### 步骤1：检查环境

首先确认您的系统已安装所需软件：

```bash
# 检查Java版本（需要17+）
java -version

# 检查Maven版本（需要3.8+）
mvn -version

# 检查Node.js版本（需要16+）
node -v
npm -v

# 检查Git
git --version
```

如果缺少任何软件，请参考下方安装指南。

---

### 步骤2：安装数据库

#### 2.1 安装MySQL 8.0

**Windows系统：**

1. 下载MySQL安装包
   - 访问：https://dev.mysql.com/downloads/mysql/
   - 选择 "MySQL Installer for Windows"

2. 运行安装程序
   - 选择 "Custom" 自定义安装
   - 勾选 "MySQL Server 8.0" 和 "MySQL Workbench"
   - 设置root密码（建议：`root`，方便开发）

3. 启动MySQL服务
   ```cmd
   # 以管理员身份运行命令提示符
   net start mysql80
   ```

**Linux (Ubuntu/Debian)：**

```bash
# 更新软件源
sudo apt update

# 安装MySQL
sudo apt install mysql-server

# 启动MySQL服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 设置root密码
sudo mysql_secure_installation
```

**Mac系统：**

```bash
# 使用Homebrew安装
brew install mysql

# 启动MySQL服务
brew services start mysql

# 设置root密码
mysql_secure_installation
```

#### 2.2 初始化MySQL数据库

```bash
# 登录MySQL
mysql -u root -p
# 输入密码：root（或您设置的密码）

# 在MySQL命令行中执行：
source /mnt/e/Code/GraduationProject/scripts/init-mysql.sql;

# 验证数据库创建成功
SHOW DATABASES;
USE power_diagnosis;
SHOW TABLES;

# 退出MySQL
exit;
```

#### 2.3 安装Neo4j 4.4

**Windows系统：**

1. 下载Neo4j Community Edition
   - 访问：https://neo4j.com/download-center/#community
   - 下载ZIP包（不需要安装版）

2. 解压到目录（例如：`C:\neo4j`）

3. 启动Neo4j
   ```cmd
   cd C:\neo4j\bin
   neo4j.bat console
   ```

4. 首次访问和配置
   - 浏览器打开：http://localhost:7474
   - 默认用户名/密码：neo4j/neo4j
   - 首次登录需要修改密码，设置为：`password`

**Linux/Mac系统：**

```bash
# Linux
wget https://neo4j.com/artifact.php?name=neo4j-community-4.4.33-unix.tar.gz
tar -xf neo4j-community-4.4.33-unix.tar.gz
cd neo4j-community-4.4.33

# Mac (使用Homebrew)
brew install neo4j

# 启动Neo4j
./bin/neo4j console
# 或
brew services start neo4j
```

#### 2.4 初始化Neo4j知识图谱

方法一：使用Neo4j浏览器

1. 访问 http://localhost:7474
2. 登录（用户名：neo4j，密码：password）
3. 复制 `scripts/init-neo4j.cypher` 文件内容
4. 粘贴到查询框中，点击运行

方法二：使用命令行（推荐）

```bash
# Windows
cd C:\neo4j\bin
type C:\path\to\GraduationProject\scripts\init-neo4j.cypher | cypher-shell -u neo4j -p password

# Linux/Mac
cat /mnt/e/Code/GraduationProject/scripts/init-neo4j.cypher | cypher-shell -u neo4j -p password
```

验证数据导入：

```cypher
// 在Neo4j浏览器中执行
MATCH (n) RETURN count(n) as nodeCount;
// 应该返回 18 个节点

MATCH ()-[r]->() RETURN count(r) as relationshipCount;
// 应该返回 14 个关系
```

#### 2.5 安装Redis 7.0

**Windows系统（WSL方式）：**

1. 启用WSL（如果未安装）
   ```cmd
   wsl --install
   ```

2. 在WSL中安装Redis
   ```bash
   sudo apt update
   sudo apt install redis-server

   # 启动Redis
   sudo service redis-server start

   # 验证
   redis-cli ping
   # 返回：PONG
   ```

**Windows系统（原生方式）：**

1. 下载Redis for Windows
   - 访问：https://github.com/tporadowski/redis/releases
   - 下载最新版ZIP包

2. 解压并运行
   ```cmd
   cd C:\redis
   redis-server.exe
   ```

**Linux系统：**

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install redis-server

# 启动Redis
sudo systemctl start redis
sudo systemctl enable redis

# 验证
redis-cli ping
```

**Mac系统：**

```bash
# 使用Homebrew
brew install redis

# 启动Redis
brew services start redis

# 验证
redis-cli ping
```

---

### 步骤3：启动后端服务

#### 3.1 导入项目到IDEA（推荐）

1. **打开IntelliJ IDEA**

2. **导入Maven项目**
   - File → Open
   - 选择 `/mnt/e/Code/GraduationProject/backend` 目录
   - 等待Maven依赖下载完成（首次可能需要10-20分钟）

3. **配置数据库连接**

   修改配置文件，确保连接信息正确：

   **知识图谱服务配置**：
   ```yaml
   # backend/modules/knowledge-graph-service/src/main/resources/application.yml

   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/power_diagnosis?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
       username: root
       password: root  # 改为您的MySQL密码

     neo4j:
       uri: bolt://localhost:7687
       authentication:
         username: neo4j
         password: password  # 改为您的Neo4j密码

     redis:
       host: localhost
       port: 6379
   ```

   **用户服务配置**：
   ```yaml
   # backend/modules/user-service/src/main/resources/application.yml

   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/power_diagnosis?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
       username: root
       password: root  # 改为您的MySQL密码

     redis:
       host: localhost
       port: 6379
   ```

4. **启动服务**

   **方式A：使用IDEA运行（推荐）**

   a. 启动知识图谱服务
   - 找到 `backend/modules/knowledge-graph-service/src/main/java/com/power/diagnosis/knowledge/KnowledgeGraphApplication.java`
   - 右键 → Run 'KnowledgeGraphApplication'
   - 等待启动完成，看到 "知识图谱服务启动成功！"

   b. 启动用户服务
   - 找到 `backend/modules/user-service/src/main/java/com/power/diagnosis/user/UserServiceApplication.java`
   - 右键 → Run 'UserServiceApplication'
   - 等待启动完成，看到 "用户服务启动成功！"

   **方式B：使用Maven命令**

   打开两个终端窗口：

   ```bash
   # 终端1 - 启动知识图谱服务
   cd /mnt/e/Code/GraduationProject/backend/modules/knowledge-graph-service
   mvn spring-boot:run

   # 终端2 - 启动用户服务
   cd /mnt/e/Code/GraduationProject/backend/modules/user-service
   mvn spring-boot:run
   ```

5. **验证服务启动**

   访问以下URL确认服务正常：
   - 知识图谱服务：http://localhost:8081
   - 用户服务：http://localhost:8082

#### 3.2 常见启动问题

**问题1：Maven依赖下载失败**

解决方案：
```bash
# 清理Maven缓存
cd /mnt/e/Code/GraduationProject/backend
mvn clean

# 重新下载依赖
mvn install -U
```

**问题2：端口被占用**

```bash
# Windows - 查找占用8081端口的进程
netstat -ano | findstr :8081
# 结束进程
taskkill /PID [进程ID] /F

# Linux/Mac
lsof -i :8081
kill -9 [进程ID]
```

**问题3：数据库连接失败**

检查清单：
- [ ] MySQL服务是否启动
- [ ] 用户名密码是否正确
- [ ] 数据库 `power_diagnosis` 是否存在
- [ ] 防火墙是否阻止连接

---

### 步骤4：启动前端服务

#### 4.1 安装Node.js（如果未安装）

**Windows/Mac：**
- 访问：https://nodejs.org/
- 下载LTS版本并安装

**Linux：**
```bash
# 使用NodeSource仓库
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
```

#### 4.2 配置npm镜像（可选，加速下载）

```bash
# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 验证
npm config get registry
```

#### 4.3 安装依赖

```bash
# 进入前端目录
cd /mnt/e/Code/GraduationProject/frontend

# 安装依赖（首次运行，需要5-10分钟）
npm install
```

如果遇到错误，尝试：
```bash
# 清除缓存
npm cache clean --force

# 删除node_modules和锁文件
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

#### 4.4 启动开发服务器

```bash
# 在frontend目录下
npm run dev
```

启动成功后，浏览器会自动打开 http://localhost:3000

---

### 步骤5：访问系统

1. **打开浏览器**
   - 访问：http://localhost:3000

2. **登录系统**
   - 用户名：`admin`
   - 密码：`admin123`

3. **验证功能**
   - 检查各个菜单是否可以访问
   - 查看知识图谱数据
   - 测试故障诊断功能

---

## 方式二：Docker容器化部署

适合：生产环境、快速演示、持续集成

### 前置要求

安装Docker和Docker Compose：

**Windows：**
- 下载Docker Desktop：https://www.docker.com/products/docker-desktop/
- 安装并启动Docker Desktop

**Linux：**
```bash
# 安装Docker
curl -fsSL https://get.docker.com | sh
sudo systemctl start docker
sudo systemctl enable docker

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证
docker --version
docker-compose --version
```

**Mac：**
- Docker Desktop已包含Docker Compose

### 一键启动所有服务

```bash
# 进入docker目录
cd /mnt/e/Code/GraduationProject/docker

# 启动所有容器（首次启动需下载镜像，可能需要10-30分钟）
docker-compose up -d

# 查看启动日志
docker-compose logs -f

# 查看所有容器状态
docker-compose ps
```

### 等待服务就绪

```bash
# 检查MySQL是否就绪
docker-compose exec mysql mysql -uroot -proot -e "SELECT 1"

# 检查Neo4j是否就绪
curl http://localhost:7474

# 检查Redis是否就绪
docker-compose exec redis redis-cli ping
```

### 初始化数据

```bash
# MySQL数据已自动初始化（通过docker-compose.yml中的volumes配置）

# Neo4j需要手动初始化
# 方法1：访问 http://localhost:7474，登录后执行init-neo4j.cypher脚本

# 方法2：使用命令行
docker-compose exec neo4j cypher-shell -u neo4j -p password < /var/lib/neo4j/import/init.cypher
```

### 访问服务

- **前端应用**：http://localhost:80
- **知识图谱服务**：http://localhost:8081
- **用户服务**：http://localhost:8082
- **Neo4j浏览器**：http://localhost:7474
- **RabbitMQ管理界面**：http://localhost:15672

### Docker管理命令

```bash
# 停止所有服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f [service_name]

# 进入容器
docker-compose exec mysql bash
docker-compose exec neo4j bash

# 清理所有数据（危险操作！）
docker-compose down -v
```

---

## 方式三：混合部署

适合：调试后端代码，同时使用Docker数据库

### 步骤1：仅启动数据库服务

创建 `docker-compose-db.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: power-diagnosis-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: power_diagnosis
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ../scripts/init-mysql.sql:/docker-entrypoint-initdb.d/init.sql

  neo4j:
    image: neo4j:4.4
    container_name: power-diagnosis-neo4j
    restart: always
    environment:
      NEO4J_AUTH: neo4j/password
    ports:
      - "7474:7474"
      - "7687:7687"
    volumes:
      - neo4j-data:/data

  redis:
    image: redis:7-alpine
    container_name: power-diagnosis-redis
    restart: always
    ports:
      - "6379:6379"

volumes:
  mysql-data:
  neo4j-data:
```

启动：
```bash
docker-compose -f docker-compose-db.yml up -d
```

### 步骤2：本地启动后端和前端

按照"方式一"的步骤3和步骤4启动后端和前端。

---

## 验证系统运行

### 1. 检查所有端口

```bash
# Windows
netstat -ano | findstr "3306 7687 6379 8081 8082 3000"

# Linux/Mac
netstat -tuln | grep "3306\|7687\|6379\|8081\|8082\|3000"
```

应该看到以下端口都在监听：
- 3306 (MySQL)
- 7474, 7687 (Neo4j)
- 6379 (Redis)
- 8081 (知识图谱服务)
- 8082 (用户服务)
- 3000 (前端)

### 2. 测试数据库连接

**MySQL：**
```bash
mysql -h localhost -P 3306 -u root -p
# 输入密码后
USE power_diagnosis;
SELECT COUNT(*) FROM sys_user;
```

**Neo4j：**
访问 http://localhost:7474
```cypher
MATCH (n) RETURN count(n);
```

**Redis：**
```bash
redis-cli ping
# 返回: PONG
```

### 3. 测试后端API

```bash
# 知识图谱服务健康检查
curl http://localhost:8081/actuator/health

# 用户服务健康检查
curl http://localhost:8082/actuator/health
```

### 4. 访问前端

打开浏览器访问 http://localhost:3000

---

## 故障排查

### 常见问题1：Maven构建失败

**症状**：
```
Could not resolve dependencies
```

**解决方案**：
```bash
# 1. 检查网络连接
ping maven.aliyun.com

# 2. 清理并重新构建
mvn clean install -U

# 3. 删除本地仓库重新下载
rm -rf ~/.m2/repository
mvn install
```

### 常见问题2：MySQL连接被拒绝

**症状**：
```
Communications link failure
```

**解决方案**：
```bash
# 1. 检查MySQL是否运行
# Windows
net start mysql80

# Linux
sudo systemctl status mysql

# 2. 检查端口
netstat -ano | findstr :3306

# 3. 检查防火墙
# Windows防火墙允许3306端口

# 4. 重置MySQL密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```

### 常见问题3：Neo4j连接超时

**症状**：
```
Unable to connect to localhost:7687
```

**解决方案**：
```bash
# 1. 确认Neo4j正在运行
# 访问 http://localhost:7474

# 2. 检查密码是否正确
# 使用 cypher-shell 测试
cypher-shell -u neo4j -p password

# 3. 重启Neo4j
# Windows
neo4j.bat restart

# Linux
./bin/neo4j restart
```

### 常见问题4：前端npm install失败

**症状**：
```
npm ERR! code ERESOLVE
```

**解决方案**：
```bash
# 1. 使用旧版依赖解析
npm install --legacy-peer-deps

# 2. 清除缓存
npm cache clean --force
rm -rf node_modules package-lock.json
npm install

# 3. 使用yarn替代
npm install -g yarn
yarn install
```

### 常见问题5：端口被占用

**症状**：
```
Port 8081 is already in use
```

**解决方案**：
```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID [PID] /F

# Linux/Mac
lsof -i :8081
kill -9 [PID]

# 或者修改配置文件中的端口
server.port=8083
```

---

## 性能优化建议

### 1. Maven构建优化

```bash
# 使用并行构建
mvn clean install -T 4

# 跳过测试（开发阶段）
mvn clean install -DskipTests
```

### 2. 数据库连接池优化

```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
```

### 3. Neo4j性能优化

```
# neo4j.conf
dbms.memory.heap.initial_size=2G
dbms.memory.heap.max_size=4G
dbms.memory.pagecache.size=2G
```

### 4. 前端构建优化

```bash
# 生产环境构建
npm run build

# 使用CDN加速
# 在vite.config.js中配置
```

---

## 开发环境推荐配置

### IDEA配置

1. **安装插件**
   - Lombok
   - MyBatis Plus
   - Spring Boot Assistant

2. **代码格式化**
   - Settings → Editor → Code Style → Java
   - 导入阿里巴巴代码规范

3. **热部署**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

### VS Code配置

1. **安装扩展**
   - Vetur / Volar
   - ESLint
   - Prettier

2. **配置自动格式化**
   ```json
   {
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "esbenp.prettier-vscode"
   }
   ```

---

## 总结

### 快速启动检查清单

- [ ] Java 17+ 已安装
- [ ] Maven 3.8+ 已安装
- [ ] Node.js 16+ 已安装
- [ ] MySQL 8.0 已启动，数据库已初始化
- [ ] Neo4j 4.4 已启动，知识图谱已导入
- [ ] Redis 7.0 已启动
- [ ] 后端服务已启动（8081, 8082）
- [ ] 前端服务已启动（3000）
- [ ] 可以访问系统并登录

### 推荐开发流程

1. **首次启动**：使用Docker方式快速验证
2. **日常开发**：使用混合方式（Docker数据库 + 本地代码）
3. **生产部署**：使用Docker Compose完整部署

### 获取帮助

- 查看日志文件
- 查看QUICKSTART.md
- 检查各服务的actuator端点
- 使用浏览器开发者工具

---

**文档版本**：v1.0
**更新日期**：2025-11-02
**适用版本**：power-equipment-diagnosis v1.0.0-SNAPSHOT
