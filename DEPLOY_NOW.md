# 🚀 立即部署 - 完整执行清单

按照以下步骤，一步步完成项目部署。

---

## ✅ 当前环境状态

- ✅ Java 21 已安装
- ✅ Node.js 20.19.5 已安装
- ✅ npm 10.8.2 已安装
- ✅ MySQL 8.0.43 已安装
- ❌ Maven 需要安装
- ❌ Redis 需要安装
- ❌ Neo4j 需要安装

---

## 📝 部署步骤

### 第一步：安装缺失的软件（2-5分钟）

打开终端，执行以下命令：

```bash
# 进入项目目录
cd /mnt/e/Code/GraduationProject

# 运行一键安装脚本
sudo ./scripts/install-dependencies.sh
```

这个脚本会自动安装：
- Maven
- Redis
- Neo4j

**等待安装完成**，看到 "安装完成！" 提示。

---

### 第二步：启动MySQL（1分钟）

```bash
# 启动MySQL服务
sudo systemctl start mysql

# 检查状态
sudo systemctl status mysql
# 看到 "active (running)" 表示成功

# 如果MySQL未设置密码，需要先设置
sudo mysql
```

在MySQL提示符中执行：
```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
EXIT;
```

---

### 第三步：初始化MySQL数据库（2分钟）

```bash
# 登录MySQL
mysql -u root -p
# 输入密码: root
```

在MySQL提示符中：
```sql
-- 执行初始化脚本
source /mnt/e/Code/GraduationProject/scripts/init-mysql.sql;

-- 验证
SHOW DATABASES;
USE power_diagnosis;
SHOW TABLES;
-- 应该看到6张表

-- 查看用户表
SELECT * FROM sys_user;
-- 应该看到admin用户

EXIT;
```

✅ 完成后，MySQL数据库就准备好了！

---

### 第四步：配置Neo4j（3分钟）

#### 4.1 首次访问Neo4j

等待30秒让Neo4j完全启动，然后：

```bash
# 检查Neo4j是否运行
sudo systemctl status neo4j

# 如果未运行，启动它
sudo systemctl start neo4j

# 等待30秒...
sleep 30

# 测试访问
curl http://localhost:7474
```

#### 4.2 设置密码

1. 打开浏览器访问：**http://localhost:7474**

2. 首次登录信息：
   - 用户名：`neo4j`
   - 密码：`neo4j`

3. 系统会要求您修改密码，**改为：`password`**

#### 4.3 初始化知识图谱

在Neo4j浏览器中：

1. 点击左上角的数据库图标
2. 复制并执行以下命令查看脚本内容：

```bash
# 在Linux终端执行，查看脚本
cat /mnt/e/Code/GraduationProject/scripts/init-neo4j.cypher
```

3. 将整个脚本内容复制到Neo4j浏览器的查询框中
4. 点击运行（或按Ctrl+Enter）

#### 4.4 验证数据

在Neo4j浏览器中执行：
```cypher
// 查看节点数量
MATCH (n) RETURN count(n) as nodeCount;
// 应该返回 18

// 查看设备
MATCH (e:Equipment) RETURN e.name, e.type;
// 应该看到4个设备
```

✅ 完成后，Neo4j知识图谱就准备好了！

---

### 第五步：验证Redis（1分钟）

```bash
# 检查Redis状态
sudo systemctl status redis-server

# 如果未运行，启动它
sudo systemctl start redis-server

# 测试连接
redis-cli ping
# 应该返回: PONG
```

✅ Redis准备好了！

---

### 第六步：编译后端项目（3-10分钟）

```bash
# 进入后端目录
cd /mnt/e/Code/GraduationProject/backend

# 清理并编译（首次可能需要5-10分钟下载依赖）
mvn clean install -DskipTests

# 等待编译完成，看到 "BUILD SUCCESS"
```

**如果编译很慢**：这是正常的，Maven首次需要下载很多依赖包。

**如果编译失败**：
```bash
# 清除缓存重试
rm -rf ~/.m2/repository
mvn clean install -DskipTests -U
```

✅ 编译成功后继续！

---

### 第七步：启动后端服务（2分钟）

打开**两个终端窗口**：

#### 终端1 - 启动知识图谱服务

```bash
cd /mnt/e/Code/GraduationProject/backend/modules/knowledge-graph-service
mvn spring-boot:run
```

等待看到：
```
========================================
知识图谱服务启动成功！
========================================
```

#### 终端2 - 启动用户服务

```bash
cd /mnt/e/Code/GraduationProject/backend/modules/user-service
mvn spring-boot:run
```

等待看到：
```
========================================
用户服务启动成功！
========================================
```

**验证后端服务**：
```bash
# 新开一个终端测试
curl http://localhost:8081
curl http://localhost:8082
```

✅ 两个服务都启动成功！

---

### 第八步：安装前端依赖（3-8分钟）

打开**第三个终端窗口**：

```bash
# 进入前端目录
cd /mnt/e/Code/GraduationProject/frontend

# 配置淘宝镜像（加速下载）
npm config set registry https://registry.npmmirror.com

# 安装依赖
npm install

# 等待安装完成...
```

✅ 前端依赖安装完成！

---

### 第九步：启动前端服务（1分钟）

```bash
# 在同一个终端中
npm run dev
```

等待启动，会看到：
```
  VITE v5.0.8  ready in XXX ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
  ➜  press h to show help
```

✅ 前端服务启动成功！

---

### 第十步：访问系统 🎉

1. **打开浏览器**，访问：**http://localhost:3000**

2. **登录系统**：
   - 用户名：`admin`
   - 密码：`admin123`

3. **验证功能**：
   - ✅ 能看到登录页面
   - ✅ 能成功登录
   - ✅ 能看到主界面
   - ✅ 各个菜单可以点击

---

## 🎯 验证清单

完成部署后，请检查：

```bash
# 新开一个终端，检查所有端口
netstat -tuln | grep "3306\|7474\|7687\|6379\|8081\|8082\|3000"
```

应该看到：
- ✅ 3306 - MySQL
- ✅ 7474 - Neo4j HTTP
- ✅ 7687 - Neo4j Bolt
- ✅ 6379 - Redis
- ✅ 8081 - 知识图谱服务
- ✅ 8082 - 用户服务
- ✅ 3000 - 前端服务

**所有端口都在监听，部署成功！** 🎉

---

## 🔧 运行中的终端

您现在应该有3个终端在运行：

1. **终端1**：运行知识图谱服务（8081端口）
2. **终端2**：运行用户服务（8082端口）
3. **终端3**：运行前端服务（3000端口）

**不要关闭这些终端！** 关闭后服务会停止。

---

## 🛑 如何停止服务

当您需要停止系统时：

1. 在每个终端按 `Ctrl + C` 停止服务
2. 可选：停止数据库服务
```bash
sudo systemctl stop mysql redis-server neo4j
```

---

## 🔄 如何重新启动

下次使用时：

```bash
# 1. 启动数据库（如果已停止）
sudo systemctl start mysql redis-server neo4j

# 2. 启动后端（两个终端）
cd /mnt/e/Code/GraduationProject/backend/modules/knowledge-graph-service
mvn spring-boot:run

cd /mnt/e/Code/GraduationProject/backend/modules/user-service
mvn spring-boot:run

# 3. 启动前端（一个终端）
cd /mnt/e/Code/GraduationProject/frontend
npm run dev
```

或者使用快捷脚本：
```bash
# 进入脚本目录
cd /mnt/e/Code/GraduationProject/scripts

# 启动后端
./start-backend.sh

# 启动前端（新终端）
./start-frontend.sh
```

---

## ❓ 常见问题

### Q1: Maven编译失败？

```bash
# 清除缓存重试
rm -rf ~/.m2/repository
cd /mnt/e/Code/GraduationProject/backend
mvn clean install -DskipTests -U
```

### Q2: MySQL连接失败？

```bash
# 检查MySQL是否运行
sudo systemctl status mysql

# 重启MySQL
sudo systemctl restart mysql

# 检查密码
mysql -u root -p
```

### Q3: Neo4j无法访问？

```bash
# 检查Neo4j状态
sudo systemctl status neo4j

# 重启Neo4j
sudo systemctl restart neo4j

# 等待30秒后访问
sleep 30
curl http://localhost:7474
```

### Q4: 端口被占用？

```bash
# 查找占用8081端口的进程
sudo lsof -i :8081

# 结束进程
sudo kill -9 [PID]
```

### Q5: npm install失败？

```bash
# 清除缓存
npm cache clean --force

# 删除node_modules重试
rm -rf node_modules package-lock.json
npm install
```

---

## 📞 获取更多帮助

- 详细部署指南：`docs/DEPLOYMENT_GUIDE.md`
- 快速参考：`QUICK_REFERENCE.md`
- 安装步骤：`INSTALLATION_STEPS.md`

---

## 🎊 恭喜！

如果您完成了所有步骤并成功访问了系统，那么恭喜您！

**项目已成功部署到本地！** 🎉🎉🎉

现在您可以：
- ✅ 开始开发新功能
- ✅ 学习系统架构
- ✅ 编写毕业论文
- ✅ 进行功能测试

祝您使用愉快！
