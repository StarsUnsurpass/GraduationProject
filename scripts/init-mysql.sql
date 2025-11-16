-- ========================================
-- 电力设备故障诊断系统数据库初始化脚本
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS power_diagnosis DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE power_diagnosis;

-- ========================================
-- 用户相关表
-- ========================================

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'operator' COMMENT '角色：admin-管理员，expert-专家，operator-操作员',
    `department` VARCHAR(100) COMMENT '部门',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入默认管理员账号（密码：admin123）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('admin', '$2a$10$XjHQhSvVZ3VN7XqJQ9W9eeO9Z1K8F5K8F5K8F5K8F5K8F5K8F5K8F5', '系统管理员', 'admin', 1);

-- ========================================
-- 故障案例相关表
-- ========================================

-- 故障案例表
DROP TABLE IF EXISTS `fault_case`;
CREATE TABLE `fault_case` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '案例ID',
    `case_no` VARCHAR(50) NOT NULL COMMENT '案例编号',
    `equipment_id` BIGINT COMMENT '设备ID（关联设备表或Neo4j）',
    `equipment_code` VARCHAR(50) COMMENT '设备编码',
    `equipment_name` VARCHAR(100) COMMENT '设备名称',
    `equipment_type` VARCHAR(50) COMMENT '设备类型',
    `fault_type` VARCHAR(50) COMMENT '故障类型',
    `fault_code` VARCHAR(50) COMMENT '故障编码',
    `fault_name` VARCHAR(100) COMMENT '故障名称',
    `symptom_description` TEXT COMMENT '症状描述',
    `diagnosis_result` TEXT COMMENT '诊断结果',
    `cause_analysis` TEXT COMMENT '原因分析',
    `solution` TEXT COMMENT '解决方案',
    `treatment_steps` TEXT COMMENT '处理步骤',
    `occurred_at` DATETIME COMMENT '故障发生时间',
    `resolved_at` DATETIME COMMENT '故障解决时间',
    `duration_hours` DECIMAL(10,2) COMMENT '处理时长（小时）',
    `cost` DECIMAL(10,2) COMMENT '处理成本',
    `severity` TINYINT COMMENT '严重程度（1-5）',
    `status` VARCHAR(20) COMMENT '状态：pending-待处理，processing-处理中，resolved-已解决',
    `attachments` TEXT COMMENT '附件（JSON数组）',
    `remarks` TEXT COMMENT '备注',
    `created_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_case_no` (`case_no`),
    KEY `idx_equipment_code` (`equipment_code`),
    KEY `idx_fault_code` (`fault_code`),
    KEY `idx_occurred_at` (`occurred_at`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故障案例表';

-- ========================================
-- 诊断记录相关表
-- ========================================

-- 诊断记录表
DROP TABLE IF EXISTS `diagnosis_record`;
CREATE TABLE `diagnosis_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `record_no` VARCHAR(50) NOT NULL COMMENT '记录编号',
    `equipment_code` VARCHAR(50) COMMENT '设备编码',
    `equipment_name` VARCHAR(100) COMMENT '设备名称',
    `symptoms` TEXT COMMENT '症状信息（JSON）',
    `diagnosis_result` TEXT COMMENT '诊断结果（JSON）',
    `recommended_solutions` TEXT COMMENT '推荐解决方案（JSON）',
    `confidence` DECIMAL(5,4) COMMENT '置信度（0-1）',
    `reasoning_path` TEXT COMMENT '推理路径（JSON）',
    `diagnosis_method` VARCHAR(50) COMMENT '诊断方法：rule-规则推理，case-案例推理，graph-图推理，hybrid-混合',
    `diagnosis_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '诊断时间',
    `time_cost` INT COMMENT '耗时（毫秒）',
    `operator_id` BIGINT COMMENT '操作员ID',
    `operator_name` VARCHAR(50) COMMENT '操作员姓名',
    `is_confirmed` TINYINT DEFAULT 0 COMMENT '是否已确认：0-未确认，1-已确认',
    `confirmed_fault` VARCHAR(100) COMMENT '确认的故障',
    `feedback` TEXT COMMENT '反馈信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_no` (`record_no`),
    KEY `idx_equipment_code` (`equipment_code`),
    KEY `idx_diagnosis_time` (`diagnosis_time`),
    KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诊断记录表';

-- ========================================
-- 知识管理相关表
-- ========================================

-- 知识导入记录表
DROP TABLE IF EXISTS `knowledge_import_record`;
CREATE TABLE `knowledge_import_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `import_no` VARCHAR(50) NOT NULL COMMENT '导入编号',
    `file_name` VARCHAR(255) COMMENT '文件名',
    `file_path` VARCHAR(500) COMMENT '文件路径',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `import_type` VARCHAR(50) COMMENT '导入类型：equipment-设备，fault-故障，case-案例',
    `total_count` INT COMMENT '总记录数',
    `success_count` INT COMMENT '成功数',
    `fail_count` INT COMMENT '失败数',
    `status` VARCHAR(20) COMMENT '状态：pending-待处理，processing-处理中，completed-已完成，failed-失败',
    `error_message` TEXT COMMENT '错误信息',
    `import_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '导入时间',
    `operator_id` BIGINT COMMENT '操作员ID',
    `operator_name` VARCHAR(50) COMMENT '操作员姓名',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_import_no` (`import_no`),
    KEY `idx_import_time` (`import_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识导入记录表';

-- ========================================
-- 系统配置相关表
-- ========================================

-- 系统配置表
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `config_type` VARCHAR(50) COMMENT '配置类型',
    `description` VARCHAR(500) COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ========================================
-- 操作日志表
-- ========================================

-- 操作日志表
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT COMMENT '用户ID',
    `username` VARCHAR(50) COMMENT '用户名',
    `operation` VARCHAR(100) COMMENT '操作',
    `method` VARCHAR(200) COMMENT '方法名',
    `params` TEXT COMMENT '请求参数',
    `result` TEXT COMMENT '返回结果',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `location` VARCHAR(100) COMMENT '操作地点',
    `time_cost` INT COMMENT '耗时（毫秒）',
    `status` TINYINT COMMENT '状态：0-失败，1-成功',
    `error_message` TEXT COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ========================================
-- 初始化示例数据
-- ========================================

-- 插入示例故障案例
INSERT INTO `fault_case` (
    `case_no`, `equipment_code`, `equipment_name`, `equipment_type`,
    `fault_type`, `fault_code`, `fault_name`, `symptom_description`,
    `diagnosis_result`, `cause_analysis`, `solution`, `occurred_at`,
    `resolved_at`, `severity`, `status`, `created_by`
) VALUES (
    'CASE-2024-001', 'TRANS-001', '主变压器1号', '变压器',
    '绝缘故障', 'FAULT-001', '绝缘油异常',
    '设备温度异常升高，油色谱分析显示乙炔含量超标',
    '变压器内部局部放电故障',
    '绝缘老化导致局部放电，产生乙炔气体',
    '停��检修，更换绝缘材料，补充绝缘油',
    '2024-01-15 08:30:00', '2024-01-16 18:00:00',
    4, 'resolved', 1
);

-- 插入系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('system.name', '电力设备故障诊断系统', 'system', '系统名称'),
('diagnosis.confidence.threshold', '0.7', 'diagnosis', '诊断置信度阈值'),
('diagnosis.max.results', '5', 'diagnosis', '诊断结果最大返回数量'),
('knowledge.cache.ttl', '3600', 'knowledge', '知识缓存过期时间（秒）');

-- ========================================
-- 查看表结构
-- ========================================
SHOW TABLES;
