package com.power.diagnosis.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ���一返回状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 通用状态码 1xxx
    SUCCESS(1000, "操作成功"),
    FAIL(1001, "操作失败"),
    PARAM_ERROR(1002, "参数错误"),
    UNAUTHORIZED(1003, "未授权"),
    FORBIDDEN(1004, "禁止访问"),
    NOT_FOUND(1005, "资源不存在"),
    SERVER_ERROR(1006, "服务器内部错误"),

    // 用户相关 2xxx
    USER_NOT_EXIST(2001, "用户不存在"),
    USER_PASSWORD_ERROR(2002, "用户名或密码错误"),
    USER_DISABLED(2003, "用户已被禁用"),
    USER_EXIST(2004, "用户已存在"),
    TOKEN_INVALID(2005, "Token无效"),
    TOKEN_EXPIRED(2006, "Token已过期"),
    PERMISSION_DENIED(2007, "权限不足"),

    // 知识图谱相关 3xxx
    ENTITY_NOT_EXIST(3001, "实体不存在"),
    ENTITY_EXIST(3002, "实体已存在"),
    RELATION_NOT_EXIST(3003, "关系不存在"),
    KNOWLEDGE_IMPORT_ERROR(3004, "知识导入失败"),
    KNOWLEDGE_EXPORT_ERROR(3005, "知识导出失败"),
    GRAPH_QUERY_ERROR(3006, "图查询失败"),

    // 故障诊断相关 4xxx
    DIAGNOSIS_FAILED(4001, "诊断失败"),
    SYMPTOM_INVALID(4002, "症状信息无效"),
    NO_MATCHING_FAULT(4003, "未找到匹配的故障"),
    REASONING_ERROR(4004, "推理引擎错误"),

    // 案例相关 5xxx
    CASE_NOT_EXIST(5001, "案例不存在"),
    CASE_SAVE_ERROR(5002, "案例保存失败"),
    CASE_SEARCH_ERROR(5003, "案例检索失败"),

    // 数据库相关 6xxx
    DB_CONNECTION_ERROR(6001, "数据库连接失败"),
    DB_OPERATION_ERROR(6002, "数据库操作失败"),
    NEO4J_CONNECTION_ERROR(6003, "Neo4j连接失败"),

    // 文件相关 7xxx
    FILE_UPLOAD_ERROR(7001, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(7002, "文件下载失败"),
    FILE_TYPE_ERROR(7003, "文件类型错误"),
    FILE_SIZE_ERROR(7004, "文件大小超出限制");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;
}
