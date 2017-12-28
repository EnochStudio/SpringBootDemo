package com.enoch.studio.handler;

import com.enoch.studio.constant.ResultCode;
import com.enoch.studio.entity.CustomerResponseEntity;
import com.enoch.studio.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.sql.SQLException;
import java.util.Set;

/**
 * 全局异常处理
 * Created by Enoch on 2017/12/13.
 */
@ControllerAdvice
@ResponseBody
class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CustomerResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.error("缺少请求参数", e);
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_PARAM_MISS, e.getMessage());
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CustomerResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("参数解析失败", e);
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_DATA_PARSE, e.getMessage());
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CustomerResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("参数验证失败", e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_DATA_FORMAT, e.getMessage());
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public CustomerResponseEntity<?> handleBindException(BindException e) {
        logger.error("参数绑定失败", e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_DATA_FORMAT, e.getMessage());
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public CustomerResponseEntity<?> handleServiceException(ConstraintViolationException e) {
        logger.error("参数验证失败", e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_DATA_FORMAT, e.getMessage());
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public CustomerResponseEntity<?> handleValidationException(ValidationException e) {
        logger.error("参数验证失败", e);
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_DATA_FORMAT, e.getMessage());
    }

    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CustomerResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.error("不支持当前请求方法", e);
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_DATA_FORMAT, e.getMessage());
    }

    /**
     * 415 - Unsupported Media Type
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CustomerResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        logger.error("不支持当前媒体类型", e);
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_CONTENT_TYPE_NOT_SUPPORTED, e.getMessage());
    }

//    /**
//     * 500 - Internal Server Error
//     */
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(ServiceException.class)
//    public CustomerResponseEntity<?> handleServiceException(ServiceException e) {
//        logger.error("业务逻辑异常", e);
//        return new CustomerResponseEntity<?>().failure("业务逻辑异常：" + e.getMessage());
//    }

    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public CustomerResponseEntity<?> handleSQLException(SQLException e) {
        logger.error("数据库错误", e);
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_DATABASE, e.getMessage());
    }

    /**
     * 通用错误处理
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CustomerResponseEntity<?> handleException(Exception exception) {
        logger.error("通用错误",exception);
        /**
         * 为安全起见，只有业务异常的详细信息我们对客户端可见，否则统一归为系统内部异常
         */
        if (exception instanceof BusinessException) {
            BusinessException businessException = (BusinessException) exception;
            return CustomerResponseEntity.getResponse(businessException.getCode(), businessException.getMessage());
        } else {
            return CustomerResponseEntity.getResponse(ResultCode.ERROR_GENERAL, null);
        }
    }

}
