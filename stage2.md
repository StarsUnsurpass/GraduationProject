# 第二阶段：需求分析、详细设计与基础实现 (Stage 2)

**时间节点**：11月22日 至 3月31日

本阶段的核心任务是掌握开发技术，完成系统的详细设计，搭建开发环境，并实现系统的基础框架和核心数据模型。

## 一、 技术选型与学习

确定了项目的技术栈，并完成了相关环境的配置：
*   **后端**: Java 17, Spring Boot 3, Spring Data Neo4j
*   **数据库**: Neo4j (图数据库核心), MySQL (系统管理辅助)
*   **前端**: Vue.js 3, Vite, Element Plus, D3.js/ECharts
*   **工具**: Maven, Git, IntelliJ IDEA/VS Code

## 二、 系统设计 (Detailed Design)

1.  **架构设计**: 确立了 B/S 分层架构（表现层、接口层、业务层、数据访问层）。
2.  **数据模型设计 (Schema)**:
    *   定义了核心**节点 (Node)**: `DeviceType`(设备类型), `Component`(部件), `FaultPhenomenon`(故障现象), `FaultCause`(故障原因), `Solution`(解决方案)。
    *   定义了核心**关系 (Relationship)**: `HAS_COMPONENT`, `HAS_POSSIBLE_FAULT`, `CAUSED_BY`, `SOLVED_BY`。
3.  **API 设计**: 规划了 RESTful 风格的接口用于实体的 CRUD 操作。

## 三、 基础编码实现 (Implementation)

本阶段已完成以下代码的编写和框架搭建：

### 1. 后端 (Spring Boot)
*   **项目脚手架**: 初始化了 `backend/power-fault-analysis` 项目，配置了 `pom.xml` 依赖。
*   **Entity 类**: 在 `com.graduationproject.power_fault_analysis.model` 包中实现了上述 5 个节点实体类，并配置了 `@Relationship` 映射。
*   **Repository 接口**: 创建了继承自 `Neo4jRepository` 的数据访问接口。
*   **Service 层**: 实现了 `KnowledgeGraphService`，封装了对图数据库的基本操作业务逻辑。
*   **Controller 层**: 实现了 `KnowledgeGraphController`，暴露了标准 HTTP 接口供前端调用。
*   **配置**: 完成了 CORS 跨域配置，允许前端访问。

### 2. 前端 (Vue 3)
*   **项目脚手架**: 初始化了 `frontend/power-fault-analysis-frontend` 项目。
*   **UI 框架集成**: 安装并配置了 `Element Plus`。
*   **路由管理**: 安装 `vue-router`，并配置了 `Home`, `KnowledgeGraph`, `FaultAnalysis` 三个基础页面路由。
*   **页面布局**: 在 `App.vue` 中实现了顶部的导航栏结构。

## 四、 编译与运行指南 (How to Run)

### 环境要求
*   **Java**: JDK 17+ (运行 `java -version` 检查)
*   **Node.js**: LTS 版本 (运行 `node -v` 检查)
*   **Neo4j**: 数据库服务需启动 (默认端口 7687)

### 1. 数据库配置 (Backend)
修改配置文件: `backend/power-fault-analysis/src/main/resources/application.properties`
```properties
# Neo4j 连接配置
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=your_password

# 服务端口 (修改为 8081 以避免与前端默认端口冲突)
server.port=8081
```

### 2. 启动后端服务
```bash
# 进入后端目录
cd backend/power-fault-analysis

# 编译并安装依赖
./mvnw clean install

# 启动 Spring Boot 应用
./mvnw spring-boot:run
```
*成功启动后，API 访问地址: `http://localhost:8081/api/knowledge-graph/devicetype`*

### 3. 启动前端应用
```bash
# 进入前端目录
cd frontend/power-fault-analysis-frontend

# 安装项目依赖
npm install

# 启动开发服务器
npm run dev
```
*成功启动后，浏览器访问: `http://localhost:8080`*

### 4. 故障排查
*   **端口被占用**: 检查是否有其他服务占用了 8080 或 8081 端口。
*   **连接被拒绝**: 确保 Neo4j 数据库正在运行且密码配置正确。

## 五、 阶段成果
*   [x] 系统详细设计文档 (`README.md` / `design_plan.md`)
*   [x] 后端工程源码 (可运行，含基础 API)
*   [x] 前端工程源码 (可运行，含基础 UI 框架)