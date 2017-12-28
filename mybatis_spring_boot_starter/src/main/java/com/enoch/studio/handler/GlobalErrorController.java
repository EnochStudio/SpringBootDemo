package com.enoch.studio.handler;

import com.alibaba.druid.wall.violation.ErrorCode;
import com.enoch.studio.constant.ResultCode;
import com.enoch.studio.entity.CustomerResponseEntity;
import com.enoch.studio.util.IpUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理，用于映射/error请求，当异常被@ControllerAdvice时不会走到这个处理类，没被处理时会走到这里
 * 例如 @valid入参校验失败的是不会走ControllerAdvice的，但会走这个处理器
 * Created by Enoch on 2017/12/12.
 */
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@ControllerAdvice
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class GlobalErrorController implements ErrorController {
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorController.class);
    @Value("${server.error.path:${error.path:/error}}")
    private static String errorPath = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath() {
        return errorPath;
    }

    /**
     * 全局异常处理
     *
     * @return
     */
    @RequestMapping
    @ResponseBody
    public CustomerResponseEntity<?> error(HttpServletRequest request) {
        return handlerError(request, false);
    }

    /**
     * 具体的处理
     *
     * @param request
     * @param includeStackTrace
     * @return
     */
    private CustomerResponseEntity<?> handlerError(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        Throwable e = errorAttributes.getError(requestAttributes);

        Map<String, Object> data = errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);

        String message = null;
        StringBuilder detailMessage = new StringBuilder("");

        HttpStatus status = getStatus(request);
        ResultCode code = ResultCode.ERROR_UNKNOWN;
        if (status == HttpStatus.BAD_REQUEST) {
            log.error("参数异常", e);
            message = "请检查参数是否合法";
            code = ResultCode.ERROR_DATA_PARSE;
        } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("系统错误", e);
            message = "系统繁忙";
            code = ResultCode.ERROR_INTERNAL;
        } else if (status == HttpStatus.NOT_FOUND) {
            log.error("404错误");
            message = "服务或者页面不存在";
            code = ResultCode.ERROR_SERVICE_NOT_EXIST;
        } else {
            log.error("系统错误", e);
            message = "系统出错,未知错误";
            code = ResultCode.ERROR_UNKNOWN;
        }
        if (null != data.get("error")) {
            detailMessage.append(String.valueOf(data.get("error"))).append(":");
        }
        if (null == e || null == e.getMessage()) {
            if (null != data.get("message")) {
                detailMessage.append(String.valueOf(data.get("message")));
            }
        } else {
            detailMessage.append(e.getMessage());
        }
        return CustomerResponseEntity.getResponse(code, String.valueOf(data.get("status")) + message + detailMessage.toString());
    }

    /**
     * 获取错误编码
     *
     * @param request
     * @return
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

//    public GlobalErrorController(ErrorAttributes errorAttributes) {
//        super(errorAttributes);
//    }

    /**
     * 500错误.
     *
     * @param req
     * @param rsp
     * @param ex
     * @return
     * @throws Exception
     */
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CustomerResponseEntity<?> serverError(HttpServletRequest req, HttpServletResponse rsp, Exception ex) throws Exception {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher("/api/**");
        if (matcher.matches(req)) {
            log.error("!!! request uri:{} from {} server exception:{}", req.getRequestURI(), IpUtil.getIpAddress(req), ex.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
//            String msg = mapper.writeValueAsString(CustomerResponseEntity.getResponse(ResultCode.STATUS_ERROR, "系统繁忙,请稍候重试"));
//            return handleJSONError(rsp, msg, HttpStatus.OK);
            return CustomerResponseEntity.getResponse(ResultCode.STATUS_ERROR, "系统繁忙,请稍候重试");
        } else {
            throw ex;
        }
    }

    /**
     * 404的拦截.
     *
     * @param request
     * @param response
     * @param ex
     * @return
     * @throws Exception
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public CustomerResponseEntity<?> notFound(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {
        log.error("!!! request uri:{} from {} not found exception:{}", request.getRequestURI(), IpUtil.getIpAddress(request), ex == null ? null : ex.getMessage());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        return CustomerResponseEntity.getResponse(ResultCode.ERROR_RESOURCE_NOT_EXIST, "你访问的资源不存在");
//        String msg = mapper.writeValueAsString(CustomerResponseEntity.getResponse(ResultCode.ERROR_RESOURCE_NOT_EXIST, "你访问的资源不存在"));
//        handleJSONError(response, msg, HttpStatus.OK);
//        return null;
    }

    /**
     * 参数不完整错误.
     *
     * @param req
     * @param rsp
     * @param ex
     * @return
     * @throws Exception
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CustomerResponseEntity<?> methodArgumentNotValidException(HttpServletRequest req, HttpServletResponse rsp, MethodArgumentNotValidException ex) throws Exception {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher("/**");
        if (matcher.matches(req)) {
            BindingResult result = ex.getBindingResult();
            List<FieldError> fieldErrors = result.getFieldErrors();
            StringBuffer msg = new StringBuffer();
            for (FieldError fieldError : fieldErrors) {
                msg.append("[" + fieldError.getField() + "," + fieldError.getDefaultMessage() + "]");
            }
//            被注释的代码为jdk1.8的lambda表达式
//            fieldErrors.stream().forEach(fieldError -> {
//                msg.append("[" + fieldError.getField() + "," + fieldError.getDefaultMessage() + "]");
//            });
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            return CustomerResponseEntity.getResponse(ResultCode.STATUS_BADREQUEST, "参数不合法:" + msg.toString());
//            String json = mapper.writeValueAsString(CustomerResponseEntity.getResponse(ResultCode.STATUS_BADREQUEST, "参数不合法:" + msg.toString()));
//            return handleJSONError(rsp, json, HttpStatus.OK);
        } else {
            throw ex;
        }
    }


//    @RequestMapping
//    @ResponseBody
//    public CustomerResponseEntity<?> handleError(HttpServletRequest request, HttpServletResponse response,Exception ex) throws Exception {
//        System.out.println(ex.getClass().getName());
//        HttpStatus status = getStatus(request);
//        System.out.println(status);
//        ex.printStackTrace();
//        ResponseEntityExceptionHandler entityExceptionHandler;
//        return CustomerResponseEntity.getResponse(ResultCode.ERROR_INTERNAL,ex.getMessage());
//        if (status == HttpStatus.NOT_FOUND) {
//            return notFound(request, response, ex);
//        } else if (status == HttpStatus.BAD_REQUEST) {
//            ex.printStackTrace();
//            return methodArgumentNotValidException(request, response, (MethodArgumentNotValidException) ex);
//        } else {
//            return serverError(request, response, ex);
//        }
//        return handleErrors(request, response);
//    }

//    @RequestMapping(produces = "text/html")
//    public ModelAndView handleHtml(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        return null;
//    }

//    protected ModelAndView handleViewError(String url, String errorStack, String errorMessage, String viewName) {
//        ModelAndView mav = new ModelAndView();
//        mav.addObject("exception", errorStack);
//        mav.addObject("url", url);
//        mav.addObject("msg", errorMessage);
//        mav.addObject("timestamp", new Date());
//        mav.setViewName(viewName);
//        return mav;
//    }

//    protected ModelAndView handleJSONError(HttpServletResponse rsp, String errorMessage, HttpStatus status) throws IOException {
//        rsp.setCharacterEncoding("UTF-8");
//        rsp.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
//        rsp.setStatus(status.value());
//        PrintWriter writer = rsp.getWriter();
//        writer.write(errorMessage);
//        writer.flush();
//        writer.close();
//        return null;
//    }


}