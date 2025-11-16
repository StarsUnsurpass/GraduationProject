# ✅ 部署检查清单

使用此清单逐步完成部署，每完成一步就打勾。

---

## 📋 准备阶段

- [ ] 已查看 `DEPLOY_NOW.md` 了解完整流程
- [ ] 已打开至少3个终端窗口
- [ ] 已准备好开始部署

---

## 🔧 安装软件

- [ ] 已执行 `sudo ./scripts/install-dependencies.sh`
- [ ] Maven 安装成功（`mvn -version` 有输出）
- [ ] Redis 安装成功（`redis-cli ping` 返回PONG）
- [ ] Neo4j 安装成功（`neo4j version` 有输出）

---

## 🗄️ 数据库配置

### MySQL

- [ ] MySQL服务已启动 (`sudo systemctl status mysql` 显示active)
- [ ] root密码已设置为 `root`
- [ ] 已执行初始化脚本 `init-mysql.sql`
- [ ] 数据库 `power_diagnosis` 已创建
- [ ] 6张表已创建 (`SHOW TABLES` 看到6张表)
- [ ] admin用户已存在 (`SELECT * FROM sys_user` 看到数据)

### Neo4j

- [ ] Neo4j服务已启动 (`sudo systemctl status neo4j` 显示active)
- [ ] 可以访问 http://localhost:7474
- [ ] 密码已改为 `password`
- [ ] 已执行初始化脚本 `init-neo4j.cypher`
- [ ] 有18个节点 (`MATCH (n) RETURN count(n)` 返回18)
- [ ] 有14个关系 (`MATCH ()-[r]->() RETURN count(r)` 返回14)

### Redis

- [ ] Redis服务已启动 (`sudo systemctl status redis-server` 显示active)
- [ ] 可以ping通 (`redis-cli ping` 返回PONG)

---

## 🏗️ 后端构建

- [ ] 进入backend目录
- [ ] 执行 `mvn clean install -DskipTests`
- [ ] 编译成功（看到 "BUILD SUCCESS"）
- [ ] 没有错误信息

---

## 🚀 后端启动

### 知识图谱服务 (8081)

- [ ] 终端1已打开
- [ ] 进入 `backend/modules/knowledge-graph-service` 目录
- [ ] 执行 `mvn spring-boot:run`
- [ ] 看到 "知识图谱服务启动成功！"
- [ ] 可以访问 http://localhost:8081

### 用户服务 (8082)

- [ ] 终端2已打开
- [ ] 进入 `backend/modules/user-service` 目录
- [ ] 执行 `mvn spring-boot:run`
- [ ] 看到 "用户服务启动成功！"
- [ ] 可以访问 http://localhost:8082

---

## 🎨 前端启动

- [ ] 终端3已打开
- [ ] 进入 `frontend` 目录
- [ ] 配置淘宝镜像 (`npm config set registry https://registry.npmmirror.com`)
- [ ] 执行 `npm install`
- [ ] 依赖安装成功（没有错误）
- [ ] 执行 `npm run dev`
- [ ] 看到 "Local: http://localhost:3000/"

---

## 🌐 访问系统

- [ ] 打开浏览器
- [ ] 访问 http://localhost:3000
- [ ] 看到登录页面
- [ ] 使用 admin / admin123 登录
- [ ] 登录成功
- [ ] 看到系统主界面
- [ ] 可以点击各个菜单

---

## ✔️ 端口验证

执行 `netstat -tuln | grep "3306\|7474\|7687\|6379\|8081\|8082\|3000"` 检查：

- [ ] 3306 - MySQL 正在监听
- [ ] 7474 - Neo4j HTTP 正在监听
- [ ] 7687 - Neo4j Bolt 正在监听
- [ ] 6379 - Redis 正在监听
- [ ] 8081 - 知识图谱服务 正在监听
- [ ] 8082 - 用户服务 正在监听
- [ ] 3000 - 前端服务 正在监听

---

## 🎯 功能测试

- [ ] 登录/登出功能正常
- [ ] 仪表盘页面可访问
- [ ] 故障诊断页面可访问
- [ ] 知识管理页面可访问
- [ ] 案例管理页面可访问
- [ ] 图谱可视化页面可访问

---

## 📝 记录信息

完成部署后，记录以下信息：

```
部署日期: _______________
部署时长: _______________
遇到的问题: _______________
___________________________
___________________________

解决方案: _________________
___________________________
___________________________
```

---

## 🎉 完成确认

- [ ] 所有检查项都已完成
- [ ] 系统运行正常
- [ ] 已保存所有配置和密码
- [ ] 已了解如何停止和重启系统

**恭喜！项目部署完成！** 🎊

---

## 📌 重要信息速查

| 项目 | 信息 |
|------|------|
| 前端地址 | http://localhost:3000 |
| 默认账号 | admin / admin123 |
| MySQL | root / root |
| Neo4j | neo4j / password |
| Redis | 无密码 |
| 知识图谱服务 | http://localhost:8081 |
| 用户服务 | http://localhost:8082 |
| Neo4j浏览器 | http://localhost:7474 |

---

## 🔄 下次启动检查清单

- [ ] 启动MySQL: `sudo systemctl start mysql`
- [ ] 启动Redis: `sudo systemctl start redis-server`
- [ ] 启动Neo4j: `sudo systemctl start neo4j`
- [ ] 启动知识图谱服务（终端1）
- [ ] 启动用户服务（终端2）
- [ ] 启动前端服务（终端3）
- [ ] 访问 http://localhost:3000

---

**提示**: 将此清单打印出来，在部署时逐项检查！
