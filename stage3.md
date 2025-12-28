# 第三阶段：核心业务逻辑与图谱可视化 (Stage 3)

**时间节点**：4月1日 至 4月20日

本阶段的核心任务是实现知识图谱的关联关系管理、图谱数据的可视化展示以及基础的故障诊断推理功能。

## 一、 后端功能扩展 (Backend)

1.  **关系管理 (Relationship Management)**:
    *   在 `KnowledgeGraphService` 中实现建立节点间关联的方法，例如：
        *   `addComponentToDeviceType`
        *   `addFaultToComponent`
        *   `addCauseToPhenomenon`
        *   `addSolutionToCause`
    *   完善 `Repository` 层，确保能够正确查询关联数据。

2.  **图谱数据接口 (Graph Data API)**:
    *   新增 API `GET /api/knowledge-graph/whole-graph`，返回系统中所有的节点和关系，数据格式需适配前端可视化组件（如 ECharts 或 D3.js）。
    *   数据结构示例：
        ```json
        {
          "nodes": [{"id": "变压器", "category": "DeviceType"}, ...],
          "links": [{"source": "变压器", "target": "油箱", "name": "HAS_COMPONENT"}, ...]
        }
        ```

3.  **故障诊断接口 (Diagnosis API)**:
    *   新增 API `GET /api/fault-analysis/diagnose?phenomenon={name}`。
    *   根据输入的故障现象，通过图谱遍历查询出潜在的“故障原因”及对应的“解决方案”。

4.  **数据初始化 (Data Initialization)**:
    *   编写 `CommandLineRunner` 或类似机制，在应用启动时自动加载一批电力设备故障的测试数据，避免每次重启数据库为空。

## 二、 前端功能实现 (Frontend)

1.  **依赖安装**:
    *   引入 `echarts` 或 `d3` 库用于图谱渲染。

2.  **知识图谱可视化 (KnowledgeGraph.vue)**:
    *   调用后端 `whole-graph` 接口。
    *   使用 Force-Directed Graph (力导向图) 展示节点和连线。
    *   支持不同类型的节点显示不同颜色（如设备为蓝色，故障为红色）。

3.  **故障分析页面 (FaultAnalysis.vue)**:
    *   提供输入框或下拉菜单让用户选择“故障现象”。
    *   调用诊断接口，将返回的 原因-方案 列表展示在页面上。

## 三、 预期成果

*   [ ] 后端能够正确存储并查询复杂的节点关系。
*   [ ] 系统启动自带演示数据。
*   [ ] 前端“知识图谱”页面能直观展示节点网络。
*   [ ] 前端“故障分析”页面能进行简单的故障诊断推理。
