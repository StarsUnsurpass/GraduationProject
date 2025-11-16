# 🚀 快速参考指南

这是一份快速参考指南，帮助您在5分钟内启动项目。

---

## 📋 启动前检查清单

```bash
# 1. 运行环境检查脚本
cd scripts

# Windows
check-environment.bat

# Linux/Mac
./check-environment.sh
```

---

## ⚡ 三种启动方式

### 方式一：一键启动（Windows - 最简单）

```cmd
1. 启动数据库（必需）
   - MySQL:  net start mysql80
   - Neo4j:  进入bin目录运行 neo4j.bat console
   - Redis:  wsl -e sudo service redis-server start

2. 双击运行
   scripts/start-all.bat

3. 访问系统
   http://localhost:3000
   用户名: admin
   密码: admin123
```

### 方式二：分步启动（所有系统）

#### Windows:
```cmd
# 终端1 - 启动后端
cd scripts
start-backend.bat

# 终端2 - 启动前端
cd scripts
start-frontend.bat
```

#### Linux/Mac:
```bash
# 终端1 - 启动后端
cd scripts
./start-backend.sh

# 终端2 - 启动前端
./start-frontend.sh
```

### 方式三：Docker部署（生产环境）

```bash
cd docker
docker-compose up -d

# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

---

## 🔧 数据库快速配置

### MySQL

```bash
# 1. 登录MySQL
mysql -u root -p

# 2. 执行初始化脚本
source /mnt/e/Code/GraduationProject/scripts/init-mysql.sql;

# 3. 验证
SHOW DATABASES;
USE power_diagnosis;
SHOW TABLES;
```

### Neo4j

```bash
# 1. 访问浏览器
http://localhost:7474

# 2. 登录
用户名: neo4j
密码: password（首次登录需设置）

# 3. 执行初始化脚本
复制 scripts/init-neo4j.cypher 内容并执行

# 4. 验证
MATCH (n) RETURN count(n);
```

### Redis

```bash
# Windows (WSL)
wsl -e sudo service redis-server start

# Linux
sudo systemctl start redis

# Mac
brew services start redis

# 验证
redis-cli ping
# 应返回: PONG
```

---

## 🌐 访问地址

| 服务 | 地址 | 默认账号 |
|------|------|----------|
| **前端系统** | http://localhost:3000 | admin / admin123 |
| 知识图谱服务 | http://localhost:8081 | - |
| 用户服务 | http://localhost:8082 | - |
| Neo4j浏览器 | http://localhost:7474 | neo4j / password |

---

## 🔍 常用命令

### 检查服务状态

```bash
# 检查端口
# Windows
netstat -ano | findstr "3306 7687 6379 8081 8082 3000"

# Linux/Mac
netstat -tuln | grep "3306\|7687\|6379\|8081\|8082\|3000"

# 测试MySQL
mysql -h localhost -u root -p

# 测试Neo4j
curl http://localhost:7474

# 测试Redis
redis-cli ping
```

### 后端操作

```bash
# 编译项目
cd backend
mvn clean install

# 跳过测试编译
mvn clean install -DskipTests

# 启动单个服务
cd modules/knowledge-graph-service
mvn spring-boot:run
```

### 前端操作

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 生产构建
npm run build

# 使用淘宝镜像（加速）
npm config set registry https://registry.npmmirror.com
```

---

## ❗ 常见问题速查

### 问题1: Maven依赖下载失败

```bash
# 清理并重新下载
mvn clean install -U
```

### 问题2: MySQL连接失败

```bash
# 检查服务
net start mysql80  # Windows
sudo systemctl status mysql  # Linux

# 重置密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
```

### 问题3: Neo4j连接超时

```bash
# 重启Neo4j
neo4j restart

# 检查密码
cypher-shell -u neo4j -p password
```

### 问题4: 端口被占用

```bash
# Windows - 结束进程
netstat -ano | findstr :8081
taskkill /PID [进程ID] /F

# Linux/Mac - 结束进程
lsof -i :8081
kill -9 [进程ID]
```

### 问题5: npm install失败

```bash
# 清除缓存
npm cache clean --force

# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 删除后重试
rm -rf node_modules package-lock.json
npm install
```

---

## 📁 项目结构速览

```
GraduationProject/
├── backend/                    # 后端（Spring Boot）
│   ├── modules/
│   │   ├── common/             # 公共模块 ✅
│   │   ├── knowledge-graph-service/  # 知识图谱服务 ✅ :8081
│   │   └── user-service/       # 用户服务 ✅ :8082
│   └── pom.xml
├── frontend/                   # 前端（Vue 3）
│   ├── src/
│   └── package.json
├── scripts/                    # 脚本
│   ├── start-all.bat           # Windows一键启动 🔥
│   ├── start-backend.bat/sh    # 启动后端
│   ├── start-frontend.bat/sh   # 启动前端
│   ├── check-environment.bat/sh # 环境检查 ✅
│   ├── init-mysql.sql          # MySQL初始化
│   └── init-neo4j.cypher       # Neo4j初始化
├── docker/
│   └── docker-compose.yml      # Docker部署
└── docs/
    ├── DEPLOYMENT_GUIDE.md     # 详细部署指南 📖
    └── QUICKSTART.md           # 快速开始
```

---

## 🎯 开发流程建议

### 首次使用

1. ✅ 运行环境检查脚本
2. ✅ 安装并启动数据库（MySQL, Neo4j, Redis）
3. ✅ 初始化数据库（执行SQL和Cypher脚本）
4. ✅ 启动后端服务
5. ✅ 启动前端服务
6. ✅ 访问系统验证

### 日常开发

```bash
# 早上启动
1. 启动数据库（如果未启动）
2. 双击 scripts/start-all.bat

# 开发中
- 后端修改自动重启（需配置devtools）
- 前端修改自动热更新

# 晚上下班
关闭服务窗口即可
```

---

## 📚 文档索引

| 文档 | 说明 | 路径 |
|------|------|------|
| **快速参考** | 本文档 | QUICK_REFERENCE.md |
| README | 项目详细介绍 | README.md |
| 部署指南 | 详细部署步骤 | docs/DEPLOYMENT_GUIDE.md |
| 快速开始 | 新手入门 | docs/QUICKSTART.md |
| 项目总结 | 构建总结 | docs/PROJECT_SUMMARY.md |
| 脚本说明 | 启动脚本使用 | scripts/README.md |

---

## 💡 开发技巧

### IDEA快捷键

- `Ctrl + F9`: 编译项目
- `Shift + F10`: 运行
- `Shift + F9`: 调试
- `Ctrl + Alt + L`: 格式化代码

### VS Code快捷键

- `Ctrl + Shift + P`: 命令面板
- `Ctrl + `: 打开终端
- `Alt + Shift + F`: 格式化代码
- `F5`: 调试

### Git操作

```bash
# 初始化仓库
git init
git add .
git commit -m "Initial commit"

# 推送到远程
git remote add origin <your-repo-url>
git push -u origin main
```

---

## 🎓 学习资源

### 官方文档

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue 3](https://vuejs.org/)
- [Neo4j](https://neo4j.com/docs/)
- [Element Plus](https://element-plus.org/)

### 视频教程

- B站搜索: "Spring Boot微服务"
- B站搜索: "Vue 3从入门到精通"
- B站搜索: "Neo4j图数据库"

---

## 📞 获取帮助

### 问题排查顺序

1. ✅ 查看终端错误信息
2. ✅ 运行环境检查脚本
3. ✅ 查看常见问题部分
4. ✅ 阅读详细部署指南
5. ✅ 查���服务日志

### 推荐工具

- **数据库管理**: Navicat, DataGrip
- **API测试**: Postman, Apifox
- **Git客户端**: GitKraken, SourceTree
- **终端工具**: Windows Terminal, iTerm2

---

## ✅ 启动验证清单

```
□ 环境检查脚本通过
□ MySQL服务运行中（3306端口）
□ Neo4j服务运行中（7474, 7687端口）
□ Redis服务运行中（6379端口）
□ 数据库已初始化
□ 后端服务启动成功（8081, 8082端口）
□ 前端服务启动成功（3000端口）
□ 可以访问 http://localhost:3000
□ 可以使用 admin/admin123 登录
```

---

**🎉 祝您开发顺利！**

如有问题，请查阅 `docs/DEPLOYMENT_GUIDE.md` 获取详细帮助。

---

**版本**: v1.0.0
**更新**: 2025-11-02
