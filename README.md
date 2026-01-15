# 基于知识图谱的电力设备故障分析系统 (Power Equipment Fault Analysis System)

## 1. 项目背景与目标
本项目旨在设计并开发一个基于知识图谱的电力设备故障分析系统。通过构建电力设备（如变压器、开关柜等）的故障知识图谱，实现对设备故障的快速诊断、分析及解决方案推荐，辅助运维人员提高故障处理效率。

## 2. 功能模块

本系统严格对照毕业设计任务书要求实现，包含以下核心模块：

### 2.1 实体信息构建与维护模块
*   **功能**: 支持对知识图谱中实体（节点）的增、删、改、查。
*   **支持实体类型**: 设备类型 (DeviceType)、部件 (Component)、故障现象 (FaultPhenomenon)、故障原因 (FaultCause)、解决方案 (Solution)。
*   **特色**:
    *   **智能重命名**: 支持修改实体名称，系统自动同步更新图谱中所有相关联的节点和关系，保持数据一致性。
    *   **动态属性**: 支持为每个实体添加自定义属性（如“额定电压: 220kV”）。
    *   **Excel 批量导入**: 支持上传 Excel 文件批量导入节点，自动解析属性列。
    *   **全生命周期管理**: 提供完整的实体列表、编辑与删除功能。

### 2.2 实体关系维护模块
*   **功能**: 管理图谱中节点之间的关联关系。
*   **关系类型**:
    *   `HAS_COMPONENT` (设备包含部件)
    *   `HAS_POSSIBLE_FAULT` (部件产生故障)
    *   `CAUSED_BY` (故障由...引起)
    *   `SOLVED_BY` (原因通过...解决)
*   **可视化操作**: 提供可视化的关联建立工具，通过下拉选择起点和终点，直观构建知识链路。

### 2.3 设备故障分析模块 (智能诊断)
*   **功能**: 根据用户输入的故障现象，自动推理可能的原因及解决方案。
*   **可视化**: 使用力导向图 (Force-Directed Graph) 动态展示“故障 -> 原因 -> 解决方案”的因果链。
*   **辅助决策**: 
    *   系统自动分析并列出具体的维护建议。
    *   展示每个解决方案的详细描述及参考属性参数。

### 2.4 用户管理模块
*   **功能**: 系统的登录认证与权限控制。
*   **安全**: 采用 **SHA-256** 算法对用户密码进行加密存储，保障数据安全。
*   **管理功能**:
    *   **用户列表**: 查看所有注册用户及其角色。
    *   **新增/删除**: 管理员可添加新用户或移除离职人员账号。
    *   **信息编辑**: 支持修改用户角色 (USER/ADMIN) 及重置用户密码。
*   **默认账号**: `admin` / `admin123` (系统启动时自动创建或重置)。

## 3. 技术栈选型

### 3.1 前端 (Frontend)
*   **框架**: Vue.js 3 (Composition API)
*   **构建工具**: Vite
*   **UI 组件库**: Element Plus (暗黑模式支持)
*   **可视化**: ECharts 5 (图谱渲染)
*   **网络请求**: Axios

### 3.2 后端 (Backend)
*   **开发语言**: Java (JDK 17+)
*   **框架**: Spring Boot 3
*   **数据库集成**: Spring Data Neo4j (SDN)
*   **安全**: Java Security (MessageDigest SHA-256)
*   **工具**: Apache POI (Excel 处理)

### 3.3 数据库 (Database)
*   **图数据库**: Neo4j (Community Edition 4.x/5.x)
    *   连接协议: Bolt Protocol

## 4. 快速开始 (Quick Start)

### 4.1 环境准备
*   **JDK 17+**
*   **Node.js 16+**
*   **Neo4j Database** (需运行在 localhost:7687，用户名: neo4j, 密码: 12344321，可在 `application.properties` 修改)

### 4.2 一键启动 (Linux/Mac)
项目根目录下提供了自动化启动脚本：

```bash
chmod +x run_project.sh
./run_project.sh
```

该脚本会自动：
1.  清理旧进程。
2.  启动 Spring Boot 后端 (端口 8081)。
3.  启动 Vue 前端 (端口 8080/5173)。
4.  输出访问地址。

### 4.3 手动启动

**后端**:
```bash
cd backend/power-fault-analysis
./mvnw clean spring-boot:run
```

**前端**:
```bash
cd frontend/power-fault-analysis-frontend
npm install
npm run dev
```

### 4.4 访问地址
*   **前端页面**: `http://localhost:8080` (或控制台提示的端口)
*   **后端 API**: `http://localhost:8081`
*   **默认登录**: 用户名 `admin`，密码 `admin123`

## 5. 目录结构
```
/
├── backend/                  # Spring Boot 后端代码
│   └── power-fault-analysis/
│       ├── src/main/java     # Java 源代码 (Controller, Service, Model)
│       └── src/main/resources# 配置文件 (application.properties)
├── frontend/                 # Vue 前端代码
│   └── power-fault-analysis-frontend/
│       ├── src/views         # 页面视图 (Login, Home, FaultAnalysis...)
│       └── src/components    # UI 组件
├── run_project.sh            # 自动化启动脚本
└── 毕设.md                    # 毕业设计任务书
```