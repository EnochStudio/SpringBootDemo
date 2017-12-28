package com.enoch.studio.constant;

/**
 * Created by Enoch on 2017/12/12.
 */
public enum ResultCode {
    SUCCESS(0, "执行成功"),
    FAILURE(1, "执行失败"),
    ERROR_UNKNOWN(2, "未知错误"),
    ERROR_INTERNAL(3, "内部错误，系统异常"),
    ERROR_ACCESS_DENIED(5, "非法用户"),
    ERROR_GENERAL(4, "通用错误"),
    ERROR_DATABASE(5, "数据库错误"),
    ERROR_PARAM_MISS(400,"缺少请求参数"),
    ERROR_DATA_FORMAT(401, "数据格式错误"),
    ERROR_DATA_PARSE(402, "数据解析错误"),
    ERROR_DATA_EXCEED_SCOPE(402, "数据超过预定义范围"),
    ERROR_SERVICE_NOT_EXIST(403, "请求的资源不存在"),
    ERROR_RESOURCE_NOT_EXIST(404, "请求的资源不存在"),
    ERROR_RESOURCE_ACCESS_FAIL(405, "请求资源失败"),
    ERROR_REQUEST_METHOD_NOT_SUPPORTED(800,"不支持当前请求方法"),
    ERROR_CONTENT_TYPE_NOT_SUPPORTED(801,"不支持当前媒体类型"),
    STATUS_ERROR(601, "系统繁忙,请稍候重试"),
    STATUS_BADREQUEST(701, "错误请求");
    //返回码
    public Integer code;
    //返回码描述
    public String description;

    private ResultCode(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
