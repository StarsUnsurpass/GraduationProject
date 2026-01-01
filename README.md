# 基于知识图谱的电力设备故障分析系统 - 设计方案

## 1. 项目背景与目标
本项目旨在设计并开发一个基于知识图谱的电力设备故障分析系统。通过构建电力设备（如变压器、开关柜等）的故障知识图谱，实现对设备故障的快速诊断、分析及解决方案推荐，辅助运维人员提高故障处理效率。

## 2. 技术栈选型

根据任务书要求及当前主流技术，采用以下技术栈：

### 2.1 前端 (Frontend)
*   **框架**: Vue.js 3 (Composition API)
*   **构建工具**: Vite
*   **UI 组件库**: Element Plus (适合后台管理与企业级应用)
*   **图谱可视化**: ECharts 5 (力导向图展示知识图谱节点与关系)
*   **图标库**: Element Plus Icons Vue
*   **网络请求**: Axios

### 2.2 后端 (Backend)
*   **开发语言**: Java (JDK 17+)
*   **框架**: Spring Boot 3
*   **图数据库集成**: Spring Data Neo4j (提供对象图映射 OGM，简化开发)
*   **API 文档**: Swagger / Knife4j

### 2.3 数据库 (Database)
*   **图数据库**: Neo4j (Community Edition) - 核心存储，用于存储实体及其复杂关系。
*   (可选) **关系型数据库**: MySQL - 仅用于存储系统管理数据（如用户账号、权限日志等，若需简化也可全用Neo4j）。

### 2.4 开发工具
*   **IDE**: IntelliJ IDEA, VS Code
*   **版本控制**: Git

---

## 3. 系统架构设计

系统采用经典的 **B/S (Browser/Server)** 架构，前后端分离。

### 3.1 架构分层
1.  **表现层 (Presentation Layer - Vue)**:
    *   提供用户交互界面（登录、图谱展示、数据录入、故障查询）。
    *   负责数据的可视化渲染（特别是力导向图的展示）。
2.  **接口层 (API Layer - Spring Boot Controller)**:
    *   暴露 RESTful API 供前端调用。
    *   处理输入校验与统一响应格式。
3.  **业务逻辑层 (Service Layer)**:
    *   **图谱构建服务**: 处理实体和关系的增删改查逻辑。
    *   **诊断分析服务**: 实现故障推理算法（如基于路径的搜索、关联度分析）。
4.  **数据访问层 (Repository Layer - Spring Data Neo4j)**:
    *   直接与 Neo4j 数据库交互，利用 Cypher 查询语言执行图操作。
5.  **数据存储层 (Data Layer)**:
    *   Neo4j 图数据库：存储节点（设备、故障、原因）及边（关联关系）。

---

## 4. 详细设计思路

### 4.1 知识图谱数据模型设计 (Schema Design)
这是系统的核心。我们需要定义“节点 (Node)”和“关系 (Relationship)”。

*   **实体节点 (Nodes)**:
    *   `DeviceType` (设备类型): 如“变压器”、“断路器”。
    *   `Component` (部件): 如“油箱”、“绕组”。
    *   `FaultPhenomenon` (故障现象): 如“油温过高”、“异响”。
    *   `FaultCause` (故障原因): 如“匝间短路”、“冷却系统失效”。
    *   `Solution` (解决方案): 如“更换线圈”、“清洗散热器”。

*   **关系 (Relationships)**:
    *   `(:DeviceType)-[:HAS_COMPONENT]->(:Component)`
    *   `(:Component)-[:HAS_POSSIBLE_FAULT]->(:FaultPhenomenon)`
    *   `(:FaultPhenomenon)-[:CAUSED_BY]->(:FaultCause)`
    *   `(:FaultCause)-[:SOLVED_BY]->(:Solution)`

### 4.2 核心功能模块设计

#### A. 实体信息构建与维护模块
*   **功能**: 管理员可以通过表单录入或 Excel 批量导入的方式，向数据库中添加设备、故障知识。
*   **实现思路**:
    *   前端提供动态表单，选择节点类型（如“添加故障现象”）。
    *   后端接收数据，保存为 Neo4j 节点。
    *   提供“关联”功能，例如选择一个“变压器”节点和一个“油温过高”节点，建立 `HAS_FAULT` 关系。

#### B. 知识图谱可视化展示
*   **功能**: 直观展示当前的电力设备知识库。
*   **实现思路**:
    *   后端提供接口 `GET /graph/data` 返回节点和边的 JSON 数据。
    *   前端使用 D3.js Force Simulation (力导向图) 渲染。
    *   支持缩放、拖拽、点击节点查看详细信息。

#### C. 设备故障分析模块 (智能诊断)
*   **功能**: 用户输入或选择观察到的“故障现象”，系统自动分析可能的原因和建议。
*   **实现思路**:
    1.  **输入**: 用户在搜索框输入“变压器油温高”。
    2.  **查询**: 后端在 Neo4j 中执行 Cypher 查询：
        ```cypher
        MATCH (p:FaultPhenomenon {name: '变压器油温高'})-[:CAUSED_BY]->(c:FaultCause)
        OPTIONAL MATCH (c)-[:SOLVED_BY]->(s:Solution)
        RETURN p, c, s
        ```
    3.  **展示**: 前端以列表或高亮图谱的方式，显示该现象可能对应的多种原因及对应的解决措施。

---

## 5. 实施步骤

1.  **环境搭建**: 安装 Java, Node.js, Neo4j 数据库。
2.  **后端基础**: 初始化 Spring Boot 项目，配置 Neo4j 连接。
3.  **数据建模**: 在 Neo4j 中预置少量测试数据，验证 Cypher 查询语句。
4.  **API 开发**: 实现实体/关系的 CRUD 接口。
5.  **前端开发**: 搭建 Vue 脚手架，实现基础布局和图谱可视化组件。
6.  **联调与优化**: 对接前后端，优化图谱加载速度和交互体验。

---

## 6. 版本更新记录 (Recent Updates)

**v1.0 - 核心功能完善与UI现代化重构 (Current Version)**

本次更新重点提升了系统的用户体验、视觉美观度及数据管理能力。

### 6.1 前端重构 (Frontend Modernization)
*   **视觉升级**: 全面引入 Element Plus Icons，优化了配色方案（采用科技蓝/深青色），设计了现代化的深色顶部导航栏与页脚。
*   **首页仪表盘 (Dashboard)**: 将首页改造为数据仪表盘，展示知识图谱中各类实体的统计数据（设备数、故障数等），并提供功能引导。
*   **图谱交互优化**:
    *   在知识图谱页面增加了**悬浮式筛选面板**，允许用户按类别（如只看故障、只看方案）动态过滤节点。
    *   优化了力导向图的样式和交互体验。
*   **智能诊断界面**: 将故障分析页面改造为**搜索引擎风格**的居中布局，增加了“推荐搜索”标签，并在诊断成功后以高亮图谱形式展示因果链。
*   **全中文本地化**: 所有的菜单、按钮、提示信息及图表标签均已汉化，更符合国内用户使用习惯。

### 6.2 新增功能 (New Features)
*   **数据管理模块 (Data Management)**:
    *   新增 `DataManagement.vue` 页面，提供可视化的数据录入界面。
    *   支持创建 5 种类型的实体节点。
    *   提供**可视化关联建立工具**，用户可通过下拉菜单选择“起点”和“终点”，直观地建立节点间的逻辑关系（如“设备->包含->部件”）。
    *   **Excel 批量导入**: 支持上传 Excel 文件批量导入节点和关系，极大提高了初始化效率。
*   **全局异常处理**: 后端增加了 `GlobalExceptionHandler`，统一处理运行时异常，返回标准化的错误提示，提升了系统的健壮性。

**v1.1 - 增强功能与部署优化 (Updated: 2026-01-01)**

本次更新引入了企业级功能支持，包括批量数据导入、容器化部署及用户体验增强。

*   **Excel 批量导入**:
    *   在“数据管理”页面新增导入功能，支持上传 `.xlsx` 文件批量创建设备、部件、故障、原因及解决方案。
    *   提供标准的 Excel 模板下载与生成工具。
*   **Docker 容器化部署**:
    *   新增 `Dockerfile` 及 `docker-compose.yml`，支持一键启动前后端及数据库服务。
    *   简化了开发与演示环境的搭建流程。
*   **深色模式 (Dark Mode)**:
    *   全站支持昼夜模式切换，自动适配系统主题。
    *   知识图谱与 ECharts 图表实现了主题联动，确保在深色模式下的可视化效果。
*   **UI/UX 细节优化**:
    *   优化了故障分析页面的搜索交互，新增“推荐搜索”标签。
    *   全局样式采用 CSS 变量管理，提升了界面的一致性。

---

## 7. 项目编译、启动与运行指南

### 7.1 环境准备

在开始之前，请确保您的开发环境满足以下要求：

*   **Java**: JDK 17 或更高版本。
    *   检查命令: `java -version`
*   **Node.js**: LTS 版本 (推荐 v18+)。
    *   检查命令: `node -v`
*   **Neo4j 数据库**: 社区版或企业版，需启动并运行。
    *   默认连接地址: `bolt://localhost:7687`
    *   默认用户名/密码: `neo4j` / `password` (请根据实际情况在后端配置文件中修改)

### 7.2 后端 (Spring Boot) 启动步骤

1.  **进入后端项目目录**:
    ```bash
    cd backend/power-fault-analysis
    ```

2.  **配置数据库连接**:
    *   打开 `src/main/resources/application.properties` 文件。
    *   确认或修改 Neo4j 连接配置：
        ```properties
        spring.neo4j.uri=bolt://localhost:7687
        spring.neo4j.authentication.username=neo4j
        spring.neo4j.authentication.password=your_password
        ```
    *   **建议修改端口** (避免与前端 8080 冲突):
        ```properties
        server.port=8081
        ```

3.  **编译项目**:
    ```bash
    # Linux/macOS
    ./mvnw clean install

    # Windows
    mvnw.cmd clean install
    ```

4.  **启动服务**:
    ```bash
    # Linux/macOS
    ./mvnw spring-boot:run

    # Windows
    mvnw.cmd spring-boot:run
    ```
    *   启动成功后，API 接口文档地址通常为: `http://localhost:8081/swagger-ui.html` (如果集成了 Swagger)。

### 7.3 前端 (Vue 3) 启动步骤

1.  **进入前端项目目录**:
    ```bash
    cd frontend/power-fault-analysis-frontend
    ```

2.  **安装依赖**:
    ```bash
    npm install
    ```

3.  **启动开发服务器**:
    ```bash
    npm run dev
    ```
    *   启动后，控制台会输出访问地址，默认为: `http://localhost:8080` (或 5173，取决于 Vite 配置及端口占用情况)。

4.  **构建生产版本**:
    ```bash
    npm run build
    ```
    *   构建产物将生成在 `dist` 目录下。

### 7.4 常见问题

*   **端口冲突**: 如果后端启动报错 "Address already in use"，请检查是否修改了 `server.port` 为 8081 或其他未占用端口。
*   **Neo4j 连接失败**: 请检查 Neo4j 服务是否已启动，以及 `application.properties` 中的账号密码是否正确。
*   **跨域问题 (CORS)**: 后端已配置 `CorsConfig` 允许前端跨域访问。如果遇到 CORS 错误，请检查浏览器控制台的网络请求详情及后端日志。

### 7.5 Docker 快速部署 (Docker Deployment)

本系统支持使用 Docker Compose 进行一键部署，无需手动配置 Java、Node.js 或 Neo4j 环境。

1.  **确保安装 Docker 和 Docker Compose**。
2.  **在项目根目录下运行**:
    ```bash
    docker-compose up --build -d
    ```
3.  **访问系统**:
    *   前端页面: `http://localhost:8080`
    *   后端 API: `http://localhost:8081`
    *   Neo4j 控制台: `http://localhost:7474` (默认账号: neo4j / password)