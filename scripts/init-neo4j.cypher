// ========================================
// 电力设备故障诊断系统 - Neo4j知识图谱初始化脚本
// ========================================

// 清空数据库（慎用！仅在开发环境使用）
// MATCH (n) DETACH DELETE n;

// ========================================
// 创建约束和索引
// ========================================

// 设备约束
CREATE CONSTRAINT equipment_code_unique IF NOT EXISTS FOR (e:Equipment) REQUIRE e.equipmentCode IS UNIQUE;
CREATE INDEX equipment_name IF NOT EXISTS FOR (e:Equipment) ON (e.name);
CREATE INDEX equipment_type IF NOT EXISTS FOR (e:Equipment) ON (e.type);

// 故障约束
CREATE CONSTRAINT fault_code_unique IF NOT EXISTS FOR (f:Fault) REQUIRE f.faultCode IS UNIQUE;
CREATE INDEX fault_type IF NOT EXISTS FOR (f:Fault) ON (f.type);
CREATE INDEX fault_category IF NOT EXISTS FOR (f:Fault) ON (f.category);

// 症状约束
CREATE CONSTRAINT symptom_code_unique IF NOT EXISTS FOR (s:Symptom) REQUIRE s.symptomCode IS UNIQUE;
CREATE INDEX symptom_type IF NOT EXISTS FOR (s:Symptom) ON (s.type);

// 原因约束
CREATE CONSTRAINT cause_code_unique IF NOT EXISTS FOR (c:Cause) REQUIRE c.causeCode IS UNIQUE;

// 解决方案约束
CREATE CONSTRAINT solution_code_unique IF NOT EXISTS FOR (s:Solution) REQUIRE s.solutionCode IS UNIQUE;

// ========================================
// 创建示例设备节点
// ========================================

// 创建变压器
CREATE (t1:Equipment {
    equipmentCode: 'TRANS-001',
    name: '主变压器1号',
    type: '变压器',
    model: 'SFZ10-50000/110',
    manufacturer: '西电集团',
    commissionDate: '2018-05-20',
    ratedVoltage: 110.0,
    ratedCapacity: 50000.0,
    location: '1号变电站',
    status: '运行中',
    description: '110kV主变压器，三相油浸式自冷变压器'
});

CREATE (t2:Equipment {
    equipmentCode: 'TRANS-002',
    name: '主变压器2号',
    type: '变压器',
    model: 'SFZ10-50000/110',
    manufacturer: '西电集团',
    commissionDate: '2018-05-20',
    ratedVoltage: 110.0,
    ratedCapacity: 50000.0,
    location: '1号变电站',
    status: '运行中',
    description: '110kV主变压器备用'
});

// 创建断路器
CREATE (b1:Equipment {
    equipmentCode: 'BREAKER-001',
    name: '110kV断路器1号',
    type: '断路器',
    model: 'LW36-126',
    manufacturer: '平高电气',
    commissionDate: '2019-03-15',
    ratedVoltage: 126.0,
    location: '1号变电站主进线',
    status: '运行中',
    description: 'SF6断路器'
});

// 创建母线
CREATE (bus1:Equipment {
    equipmentCode: 'BUS-001',
    name: '110kV I段母线',
    type: '母线',
    ratedVoltage: 110.0,
    location: '1号变电站',
    status: '运行中',
    description: '主母线I段'
});

// ========================================
// 创建设备连接关系
// ========================================

MATCH (b:Equipment {equipmentCode: 'BREAKER-001'})
MATCH (bus:Equipment {equipmentCode: 'BUS-001'})
CREATE (b)-[:CONNECTS_TO {connectionType: '电气连接', voltageLevel: 110}]->(bus);

MATCH (bus:Equipment {equipmentCode: 'BUS-001'})
MATCH (t:Equipment {equipmentCode: 'TRANS-001'})
CREATE (bus)-[:CONNECTS_TO {connectionType: '电气连接', voltageLevel: 110}]->(t);

// ========================================
// 创建故障节点
// ========================================

// 变压器故障
CREATE (f1:Fault {
    faultCode: 'FAULT-001',
    name: '变压器局部放电',
    type: '绝缘故障',
    category: '电气故障',
    severity: 4,
    impact: '可能导致变压器损坏，影响供电',
    description: '变压器内部绝缘材料老化或受潮，产生局部放电'
});

CREATE (f2:Fault {
    faultCode: 'FAULT-002',
    name: '变压器过热',
    type: '热故障',
    category: '热力故障',
    severity: 3,
    impact: '降低设备寿命，严重时可能引发火灾',
    description: '变压器运行温度超过正常范围'
});

CREATE (f3:Fault {
    faultCode: 'FAULT-003',
    name: '断路器拒动',
    type: '机械故障',
    category: '机械故障',
    severity: 5,
    impact: '无法切断故障电流，可能扩大事故范围',
    description: '断路器接收到分闸命令后未能动作'
});

// ========================================
// 创建症状节点
// ========================================

CREATE (s1:Symptom {
    symptomCode: 'SYM-001',
    name: '油色谱异常',
    type: '化学指标异常',
    description: '绝缘油中溶解气体分析异常',
    detectionMethod: '色谱分析',
    threshold: '乙炔>5ppm',
    indicationProbability: 0.85
});

CREATE (s2:Symptom {
    symptomCode: 'SYM-002',
    name: '温度异常升高',
    type: '物理指标异常',
    description: '设备运行温度超过额定值',
    detectionMethod: '温度监测',
    threshold: '温度>85℃',
    indicationProbability: 0.75
});

CREATE (s3:Symptom {
    symptomCode: 'SYM-003',
    name: '异常声音',
    type: '物理指标异常',
    description: '设备运行时发出异常声响',
    detectionMethod: '声音监测',
    threshold: '噪声>65dB',
    indicationProbability: 0.70
});

CREATE (s4:Symptom {
    symptomCode: 'SYM-004',
    name: '操作机构卡涩',
    type: '机械指标异常',
    description: '断路器操作机构动作不灵活',
    detectionMethod: '机械检查',
    indicationProbability: 0.90
});

// ========================================
// 创建原因节点
// ========================================

CREATE (c1:Cause {
    causeCode: 'CAUSE-001',
    name: '绝缘老化',
    type: '材料老化',
    description: '长期运行导致绝缘材料性能降低',
    causeLevel: '根本原因',
    frequency: '常见',
    causalStrength: 0.80
});

CREATE (c2:Cause {
    causeCode: 'CAUSE-002',
    name: '过载运行',
    type: '运行不当',
    description: '负荷超过额定容量',
    causeLevel: '直接原因',
    frequency: '常见',
    causalStrength: 0.85
});

CREATE (c3:Cause {
    causeCode: 'CAUSE-003',
    name: '冷却系统故障',
    type: '设备故障',
    description: '冷却系统无法正常工作',
    causeLevel: '直接原因',
    frequency: '较少',
    causalStrength: 0.75
});

CREATE (c4:Cause {
    causeCode: 'CAUSE-004',
    name: '机械部件磨损',
    type: '材料磨损',
    description: '长期动作导致机械部件磨损',
    causeLevel: '根本原因',
    frequency: '常见',
    causalStrength: 0.90
});

// ========================================
// 创建解决方案节点
// ========================================

CREATE (sol1:Solution {
    solutionCode: 'SOL-001',
    name: '更换绝缘油',
    type: '维护保养',
    description: '排空旧油，补充新绝缘油',
    steps: '1.停电隔离 2.排油 3.清洗 4.真空注油 5.检测 6.投运',
    requiredTools: '真空滤油机、油色谱仪、绝缘测试仪',
    estimatedTime: 8.0,
    estimatedCost: 20000.0,
    effectivenessScore: 8.5,
    applicationCount: 15,
    precautions: '注意安全距离，确保油品质量'
});

CREATE (sol2:Solution {
    solutionCode: 'SOL-002',
    name: '检查冷却系统',
    type: '故障排查',
    description: '检查并修复冷却系统',
    steps: '1.检查风扇 2.检查油泵 3.清理散热器 4.测试运行',
    requiredTools: '万用表、工具箱',
    estimatedTime: 4.0,
    estimatedCost: 5000.0,
    effectivenessScore: 7.5,
    applicationCount: 20,
    precautions: '检查前确保设备已停电'
});

CREATE (sol3:Solution {
    solutionCode: 'SOL-003',
    name: '更换操作机构',
    type: '设备更换',
    description: '更换断路器操作机构',
    steps: '1.停电 2.拆卸旧机构 3.安装新机构 4.调试 5.试验 6.投运',
    requiredTools: '专用工具、吊装设备',
    estimatedTime: 16.0,
    estimatedCost: 50000.0,
    effectivenessScore: 9.0,
    applicationCount: 5,
    precautions: '严格按照厂家说明书操作'
});

// ========================================
// 创建关系：设备-故障
// ========================================

MATCH (e:Equipment {equipmentCode: 'TRANS-001'})
MATCH (f:Fault {faultCode: 'FAULT-001'})
CREATE (e)-[:HAS_FAULT {occurrenceCount: 2, lastOccurrence: '2024-01-15'}]->(f);

MATCH (e:Equipment {equipmentCode: 'TRANS-001'})
MATCH (f:Fault {faultCode: 'FAULT-002'})
CREATE (e)-[:HAS_FAULT {occurrenceCount: 1, lastOccurrence: '2024-02-10'}]->(f);

MATCH (e:Equipment {equipmentCode: 'BREAKER-001'})
MATCH (f:Fault {faultCode: 'FAULT-003'})
CREATE (e)-[:HAS_FAULT {occurrenceCount: 1, lastOccurrence: '2024-03-05'}]->(f);

// ========================================
// 创建关系：故障-症状
// ========================================

MATCH (f:Fault {faultCode: 'FAULT-001'})
MATCH (s:Symptom {symptomCode: 'SYM-001'})
CREATE (f)-[:HAS_SYMPTOM {indicationStrength: 0.9}]->(s);

MATCH (f:Fault {faultCode: 'FAULT-001'})
MATCH (s:Symptom {symptomCode: 'SYM-003'})
CREATE (f)-[:HAS_SYMPTOM {indicationStrength: 0.7}]->(s);

MATCH (f:Fault {faultCode: 'FAULT-002'})
MATCH (s:Symptom {symptomCode: 'SYM-002'})
CREATE (f)-[:HAS_SYMPTOM {indicationStrength: 0.95}]->(s);

MATCH (f:Fault {faultCode: 'FAULT-002'})
MATCH (s:Symptom {symptomCode: 'SYM-003'})
CREATE (f)-[:HAS_SYMPTOM {indicationStrength: 0.6}]->(s);

MATCH (f:Fault {faultCode: 'FAULT-003'})
MATCH (s:Symptom {symptomCode: 'SYM-004'})
CREATE (f)-[:HAS_SYMPTOM {indicationStrength: 0.95}]->(s);

// ========================================
// 创建关系：故障-原因
// ========================================

MATCH (f:Fault {faultCode: 'FAULT-001'})
MATCH (c:Cause {causeCode: 'CAUSE-001'})
CREATE (f)-[:CAUSED_BY {causalProbability: 0.85}]->(c);

MATCH (f:Fault {faultCode: 'FAULT-002'})
MATCH (c:Cause {causeCode: 'CAUSE-002'})
CREATE (f)-[:CAUSED_BY {causalProbability: 0.80}]->(c);

MATCH (f:Fault {faultCode: 'FAULT-002'})
MATCH (c:Cause {causeCode: 'CAUSE-003'})
CREATE (f)-[:CAUSED_BY {causalProbability: 0.70}]->(c);

MATCH (f:Fault {faultCode: 'FAULT-003'})
MATCH (c:Cause {causeCode: 'CAUSE-004'})
CREATE (f)-[:CAUSED_BY {causalProbability: 0.90}]->(c);

// ========================================
// 创建关系：故障-解决方案
// ========================================

MATCH (f:Fault {faultCode: 'FAULT-001'})
MATCH (s:Solution {solutionCode: 'SOL-001'})
CREATE (f)-[:SOLVED_BY {effectiveness: 0.85, priority: 1}]->(s);

MATCH (f:Fault {faultCode: 'FAULT-002'})
MATCH (s:Solution {solutionCode: 'SOL-002'})
CREATE (f)-[:SOLVED_BY {effectiveness: 0.75, priority: 1}]->(s);

MATCH (f:Fault {faultCode: 'FAULT-003'})
MATCH (s:Solution {solutionCode: 'SOL-003'})
CREATE (f)-[:SOLVED_BY {effectiveness: 0.90, priority: 1}]->(s);

// ========================================
// 查询验证
// ========================================

// 查看所有设备
MATCH (e:Equipment) RETURN e.equipmentCode, e.name, e.type;

// 查看所有故障
MATCH (f:Fault) RETURN f.faultCode, f.name, f.type, f.severity;

// 查看设备及其故障
MATCH (e:Equipment)-[:HAS_FAULT]->(f:Fault)
RETURN e.name, f.name;

// 查看故障诊断完整路径（症状->故障->原因->解决方案）
MATCH path = (s:Symptom)<-[:HAS_SYMPTOM]-(f:Fault)-[:CAUSED_BY]->(c:Cause)
MATCH (f)-[:SOLVED_BY]->(sol:Solution)
RETURN s.name AS 症状, f.name AS 故障, c.name AS 原因, sol.name AS 解决方案
LIMIT 10;

// 统计各类型设备数量
MATCH (e:Equipment)
RETURN e.type AS 设备类型, COUNT(e) AS 数量;

// 统计各类故障数量
MATCH (f:Fault)
RETURN f.category AS 故障分类, COUNT(f) AS 数量;
