# 快速启动指南

本文档���指导您如何快速启动"基于知识图谱的电力设备故障分析与辅助诊断系统"。

## 目录

- [环境准备](#环境准备)
- [数据库安装配置](#数据库安装配置)
- [后端启动](#后端启动)
- [前端启动](#前端启动)
- [Docker部署](#docker部署)
- [常见问题](#常见问题)

---

## 环境准备

### 必需软件

请确保您的开发环境已安装以下软件：

1. **JDK 17+**
   ```bash
   # 检查Java版本
   java -version
   ```

2. **Maven 3.8+**
   ```bash
   # 检查Maven版本
   mvn -version
   ```

3. **Node.js 16+**
   ```bash
   # 检查Node.js版本
   node -v
   npm -v
   ```

4. **MySQL 8.0+**
   - 下载地址：https://dev.mysql.com/downloads/mysql/

5. **Neo4j 4.4+**
   - 下载地址：https://neo4j.com/download/

6. **Redis 7.0+**
   - 下载地址：https://redis.io/download/

### 可选软件

- **Docker** & **Docker Compose**（用于容器化部署）
- **Git**（用于版本控制）
- **IntelliJ IDEA**（推荐的Java IDE）
- **VS Code**（推荐的前端编辑器）

---

## 数据库安装配置

### 1. MySQL配置

#### Windows系统

1. 下载并安装MySQL 8.0
2. 启动MySQL服务
   ```cmd
   net start mysql
   ```

3. 登录MySQL并执行初始化脚本
   ```bash
   mysql -u root -p
   ```

4. 执行初始化脚本
   ```sql
   source /path/to/GraduationProject/scripts/init-mysql.sql
   ```

#### Linux/Mac系统

```bash
# 启动MySQL
sudo systemctl start mysql

# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source /mnt/e/Code/GraduationProject/scripts/init-mysql.sql
```

### 2. Neo4j配置

#### 启动Neo4j

```bash
# Windows
neo4j.bat console

# Linux/Mac
./bin/neo4j console
```

#### 访问Neo4j浏览器

1. 打开浏览器访问：http://localhost:7474
2. 默认用户名/密码：neo4j/neo4j
3. 首次登录需要修改密码（建议设置为：password）

#### 执行初始化脚本

在Neo4j浏览器中执行以下命令：

```cypher
// 复制 scripts/init-neo4j.cypher 文件内容并执行
```

或使用命令行：

```bash
# 使用cypher-shell执行脚本
cat /mnt/e/Code/GraduationProject/scripts/init-neo4j.cypher | cypher-shell -u neo4j -p password
```

### 3. Redis配置

#### 启动Redis

```bash
# Windows（如果使用WSL）
redis-server

# Linux
sudo systemctl start redis

# Mac
brew services start redis
```

#### 验证Redis

```bash
redis-cli ping
# 返回 PONG 表示成功
```

---

## 后端启动

### 方式一：IDEA启动（推荐开发使用）

1. **打开项目**
   - 使用IntelliJ IDEA打开 `backend` 目录
   - 等待Maven依赖下载完成

2. **配置数据库连接**

   修改各服务的 `application.yml` 文件，确保数据库连接信息正确：

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/power_diagnosis
       username: root
       password: root  # 修改为你的MySQL密码

     neo4j:
       uri: bolt://localhost:7687
       authentication:
         username: neo4j
         password: password  # 修改为你的Neo4j密码

     redis:
       host: localhost
       port: 6379
   ```

3. **启动服务**

   按顺序启动以下服务：

   - **知识图谱服务**
     - 运行 `KnowledgeGraphApplication.java`
     - 启动端口：8081

   - **用户服务**
     - 运行 `UserServiceApplication.java`
     - 启动端口：8082

   查看控制台输出，确认服务启动成功。

### 方式二：Maven命令启动

```bash
# 进入后端目录
cd backend

# 编译整个项目
mvn clean install

# 启动知识图谱服务
cd modules/knowledge-graph-service
mvn spring-boot:run

# 新开终端，启动用户服务
cd backend/modules/user-service
mvn spring-boot:run
```

### 验证后端服务

访问以下URL验证服务是否正常：

- 知识图谱服务：http://localhost:8081
- 用户服务：http://localhost:8082

---

## 前端启动

### 1. 安装依赖

```bash
# 进入前端目录
cd frontend

# 安装依赖（首次运行或package.json变更后执行）
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

启动成功后，浏览器会自动打开 http://localhost:3000

### 3. 登录系统

使用默认管理员账号登录：
- 用户名：`admin`
- 密码：`admin123`

---

## Docker部署

### 使用Docker Compose一键部署

#### 1. 启动所有服务

```bash
# 进入docker目录
cd docker

# 启动所有容器
docker-compose up -d
```

#### 2. 查看服务状态

```bash
# 查看所有容器状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

#### 3. 访问服务

- **MySQL**: localhost:3306
- **Neo4j浏览器**: http://localhost:7474
- **Redis**: localhost:6379
- **知识图谱服务**: http://localhost:8081
- **用户服务**: http://localhost:8082
- **前端应用**: http://localhost:80

#### 4. 停止服务

```bash
# 停止所有容器
docker-compose down

# 停止并删除数据卷（慎用）
docker-compose down -v
```

---

## 项目目录结构

```
GraduationProject/
├── backend/                    # 后端项目
│   ├── gateway/                # API网关
│   ├── modules/
│   │   ├── common/             # 公共模块
│   │   ├── knowledge-graph-service/  # 知识图谱服务
│   │   ├── user-service/       # 用户服务
│   │   ├── diagnosis-service/  # 诊断服务（待实现）
│   │   └── case-service/       # 案例服务（待实现）
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/              # 页面
│   │   ├── components/         # 组件
│   │   ├── router/             # 路由
│   │   └── store/              # 状态管理
│   └── package.json
├── docs/                       # 文档
├── scripts/                    # 脚本
│   ├── init-mysql.sql          # MySQL初始化脚本
│   └── init-neo4j.cypher       # Neo4j初始化脚本
├── docker/                     # Docker配置
│   └── docker-compose.yml
└── README.md
```

---

## 开发建议

### 后端开发

1. **代码规范**
   - 遵循阿里巴巴Java开发手册
   - 使用Lombok简化代码
   - 添加适当的注释

2. **日志记录**
   - 使用SLF4J + Logback
   - 区分不同级别的日志（DEBUG, INFO, WARN, ERROR）

3. **异常处理**
   - 使用统一异常处理器
   - 自定义业务异常

4. **接口文档**
   - 使用Swagger/Knife4j生成API文档

### 前端开发

1. **代码规范**
   - 使用ESLint + Prettier
   - 组件命名使用PascalCase
   - 文件命名使用kebab-case

2. **状态管理**
   - 使用Pinia管理全局状态
   - 模块化组织Store

3. **API请求**
   - 统一封装Axios
   - 使用请求拦截器添加Token

---

## 常见问题

### Q1: Maven依赖下载失败

**解决方案**：
1. 检查网络连接
2. 配置Maven使用阿里云镜像（已在pom.xml中配置）
3. 清理本地仓库：`mvn clean`

### Q2: Neo4j连接失败

**解决方案**：
1. 确认Neo4j服务已启动
2. 检查端口7687是否被占用
3. 确认用户名密码正确
4. 查看application.yml中的Neo4j配置

### Q3: 前端npm install失败

**解决方案**：
1. 使用淘宝镜像：`npm config set registry https://registry.npmmirror.com`
2. 清除缓存：`npm cache clean --force`
3. 删除node_modules和package-lock.json重新安装

### Q4: 端口被占用

**解决方案**：

Windows:
```cmd
# 查找占用端口的进程
netstat -ano | findstr :8081

# 结束进程
taskkill /PID [进程ID] /F
```

Linux/Mac:
```bash
# 查找占用端口的进程
lsof -i :8081

# 结束进程
kill -9 [进程ID]
```

### Q5: MySQL中文乱码

**解决方案**：
确保MySQL配置文件（my.ini/my.cnf）中设置：
```ini
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci

[client]
default-character-set=utf8mb4
```

---

## 技术支持

如遇到其他问题，请：

1. 查看项目README.md
2. 查看各服务的日志文件
3. 在项目仓库提Issue

---

## 下一步

- 阅读 [README.md](../README.md) 了解项目详情
- 查看 [API文档](./api/) 了解接口定义
- 查看 [设计文档](./design/) 了解系统设计

祝您使用愉快！🎉
