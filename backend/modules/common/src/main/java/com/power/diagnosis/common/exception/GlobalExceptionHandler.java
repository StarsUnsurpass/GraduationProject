package com.power.diagnosis.common.exception;

import com.power.diagnosis.common.result.Result;
import com.power.diagnosis.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常 - @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.error("参数校验异常：{}", message, e);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 参数绑定异常 - @Validated
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.error("参数绑定异常：{}", message, e);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.isEmpty() ? "约束违反" : violations.iterator().next().getMessage();
        log.error("约束违反异常：{}", message, e);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常：{}", e.getMessage(), e);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.fail(ResultCode.SERVER_ERROR.getCode(), "系统内部错误");
    }

    /**
     * 未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.fail(ResultCode.SERVER_ERROR.getCode(), "系统内部错误");
    }
}
