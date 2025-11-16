# 启动脚本使用说明

本目录包含了快速启动项目的脚本文件。

## 脚本列表

### Windows系统

| 脚本文件 | 功能说明 |
|---------|---------|
| `start-all.bat` | 一键启动所有服务（后端+前端） |
| `start-backend.bat` | 仅启动后端服务 |
| `start-frontend.bat` | 仅启动前端服务 |

### Linux/Mac系统

| 脚本文件 | 功能说明 |
|---------|---------|
| `start-backend.sh` | 启动后端服务 |
| `start-frontend.sh` | 启动前端服务 |

### 数据库初始化

| 脚本文件 | 功能说明 |
|---------|---------|
| `init-mysql.sql` | MySQL数据库初始化脚本 |
| `init-neo4j.cypher` | Neo4j知识图谱初始化脚本 |

---

## 使用步骤

### 方式一：一键启动（Windows）

**适合：快速演示、功能验证**

1. **确保数据库已启动**
   ```cmd
   REM 启动MySQL
   net start mysql80

   REM 启动Neo4j（进入Neo4j安装目录的bin文件夹）
   neo4j.bat console

   REM 启动Redis（WSL方式）
   wsl -e sudo service redis-server start
   ```

2. **双击运行**
   ```
   双击 start-all.bat
   ```

3. **等待启动完成**
   - 后端服务会在新窗口中启动
   - 前端服务会在新窗口中启动
   - 浏览器自动打开 http://localhost:3000

### 方式二：分步启动（Windows）

**适合：开发调试**

1. **启动后端**
   ```cmd
   双击 start-backend.bat
   ```

2. **启动前端**（新开命令行窗口）
   ```cmd
   双击 start-frontend.bat
   ```

### 方式三：命令行启动（Linux/Mac）

1. **赋予执行权限**
   ```bash
   chmod +x *.sh
   ```

2. **启动后端**
   ```bash
   ./start-backend.sh
   ```

3. **启动前端**（新开终端）
   ```bash
   ./start-frontend.sh
   ```

---

## 前置条件

### 必须启动的服务

在运行启动脚本之前，请确保以下服务已经运行：

#### 1. MySQL (端口3306)

**Windows:**
```cmd
net start mysql80
```

**Linux:**
```bash
sudo systemctl start mysql
```

**Mac:**
```bash
brew services start mysql
```

**验证:**
```bash
mysql -u root -p
```

#### 2. Neo4j (端口7474, 7687)

**Windows:**
```cmd
cd C:\neo4j\bin
neo4j.bat console
```

**Linux/Mac:**
```bash
./bin/neo4j console
```

**验证:**
访问 http://localhost:7474

#### 3. Redis (端口6379)

**Windows (WSL):**
```cmd
wsl -e sudo service redis-server start
```

**Linux:**
```bash
sudo systemctl start redis
```

**Mac:**
```bash
brew services start redis
```

**验证:**
```bash
redis-cli ping
# 返回: PONG
```

---

## 数据库初始化

### 首次运行必须执行

#### 初始化MySQL

```bash
# 方式1：命令行
mysql -u root -p < init-mysql.sql

# 方式2：MySQL Workbench
# 打开 init-mysql.sql 文件并执行
```

#### 初始化Neo4j

```bash
# 方式1：Neo4j浏览器（推荐）
# 1. 访问 http://localhost:7474
# 2. 登录（neo4j/password）
# 3. 复制 init-neo4j.cypher 内容并执行

# 方式2：命令行
cat init-neo4j.cypher | cypher-shell -u neo4j -p password
```

---

## 端口占用检查

如果启动失败，可能是端口被占用。

### Windows

```cmd
REM 查看端口占用
netstat -ano | findstr "3306 7687 6379 8081 8082 3000"

REM 结束占用端口的进程
taskkill /PID [进程ID] /F
```

### Linux/Mac

```bash
# 查看端口占用
netstat -tuln | grep "3306\|7687\|6379\|8081\|8082\|3000"

# 结束占用端口的进程
kill -9 [进程ID]
```

---

## 停止服务

### Windows

- **后端服务**: 关闭运行后端的命令行窗口
- **前端服务**: 关闭运行前端的命令行窗口
- **或者**: 在窗口中按 `Ctrl + C`

### Linux/Mac

在终端中按 `Ctrl + C`

---

## 常见问题

### 1. Maven下载依赖很慢

**解决方案:**
```bash
# 使用阿里云镜像（已在pom.xml中配置）
mvn clean install -U
```

### 2. npm install 很慢

**解决方案:**
```bash
# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com
npm install
```

### 3. MySQL连接失败

**检查清单:**
- [ ] MySQL服务是否运行
- [ ] 端口3306是否被占用
- [ ] 用户名密码是否正确（root/root）
- [ ] 数据库power_diagnosis是否存在

**解决方案:**
```sql
-- 重新创建数据库
DROP DATABASE IF EXISTS power_diagnosis;
source init-mysql.sql;
```

### 4. Neo4j连接失败

**检查清单:**
- [ ] Neo4j服务是否运行
- [ ] 访问 http://localhost:7474 是否正常
- [ ] 密码是否正确（password）

**解决方案:**
```bash
# 重启Neo4j
neo4j restart
```

### 5. Redis连接失败

**检查清单:**
- [ ] Redis服务是否运行
- [ ] redis-cli ping 是否返回PONG

**解决方案:**
```bash
# Windows (WSL)
wsl -e sudo service redis-server restart

# Linux
sudo systemctl restart redis

# Mac
brew services restart redis
```

---

## 开发模式 vs 生产模式

### 开发模式（当前脚本）

- 使用 `mvn spring-boot:run` 启动
- 使用 `npm run dev` 启动
- 支持热重载
- 显示详细日志
- 适合本地开发调试

### 生产模式（Docker）

```bash
# 使用Docker Compose
cd ../docker
docker-compose up -d
```

- 使用容器化部署
- 自动重启
- 资源隔离
- 适合生产环境

---

## 访问地址

启动成功后，可以通过以下地址访问：

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端应用 | http://localhost:3000 | 主应用界面 |
| 知识图谱服务 | http://localhost:8081 | 后端API |
| 用户服务 | http://localhost:8082 | 后端API |
| Neo4j浏览器 | http://localhost:7474 | 图数据库管理 |
| MySQL | localhost:3306 | 关系数据库 |
| Redis | localhost:6379 | 缓存服务 |

### 默认登录账号

- **用户名**: admin
- **密码**: admin123

---

## 脚本说明

### start-backend.bat/sh

**功能:**
- 检查Java、Maven环境
- 检查MySQL、Neo4j、Redis连接
- 编译Maven项目
- 启动知识图谱服务（8081）
- 启动用户服务（8082）

**日志:**
- Windows: 在新窗口中显示
- Linux/Mac: 在当前终端显示

### start-frontend.bat/sh

**功能:**
- 检查Node.js、npm环境
- 检查并安装依赖（首次运行）
- 启动Vite开发服务器（3000）

**特性:**
- 自动打开浏览器
- 支持热重载（HMR）

---

## 性能优化

### Maven构建加速

```bash
# 并行构建
mvn clean install -T 4

# 跳过测试
mvn clean install -DskipTests
```

### npm安装加速

```bash
# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 或使用yarn
npm install -g yarn
yarn install
```

---

## 获取帮助

如果遇到问题：

1. 查看终端/命令行的错误信息
2. 查看 `../docs/DEPLOYMENT_GUIDE.md`
3. 查看 `../docs/QUICKSTART.md`
4. 检查服务日志

---

**更新日期**: 2025-11-02
**适用版本**: v1.0.0-SNAPSHOT
