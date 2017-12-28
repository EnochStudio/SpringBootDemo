package com.enoch.studio.log;

import com.enoch.studio.util.IpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

/**
 * Created by Enoch on 2017/12/12.
 */
@Aspect
@Configuration
public class WebLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    @Pointcut("execution(public * com.enoch.studio.controller..*.*(..))")//两个..代表所有子目录，最后括号里的两个..代表所有参数
    public void logPointCut() {
    }

//    @Before("logPointCut()")
//    public void doBefore(JoinPoint joinPoint) throws Throwable {
//        // 接收到请求，记录请求内容
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//
//        // 记录下请求内容
//        logger.info("请求地址 : " + request.getRequestURL().toString());
//        logger.info("HTTP METHOD : " + request.getMethod());
//        // 获取真实的ip地址
//        logger.info("IP : " + IpUtil.getIpAddress(request));
//        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "."
//                + joinPoint.getSignature().getName());
//        logger.info("参数 : " + Arrays.toString(joinPoint.getArgs()));
////        loggger.info("参数 : " + joinPoint.getArgs());
//
//    }
//
//    @AfterReturning(returning = "ret", pointcut = "logPointCut()")// returning的值和doAfterReturning的参数名一致
//    public void doAfterReturning(Object ret) throws Throwable {
//        // 处理完请求，返回内容(返回值太复杂时，打印的是物理存储空间的地址)
//        logger.info("返回值 : " + ret);
//    }

    /**
     * 环绕通知
     *
     * @param pjp 连接点
     * @return
     * @throws Throwable
     */
    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = attributes.getRequest().getSession();
        String sessionID = session.getId();

        // 记录请求内容
        String requestURL = request.getRequestURL().toString();
        String httpMethod = request.getMethod();
        String classMethod = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
        String params = Arrays.toString(pjp.getArgs());

        // 获取真实的ip地址
        String ip = IpUtil.getIpAddress(request);

        logger.info("IP=[{}]，请求URL=[{}]，method=[{}]，sessionID=[{}]", ip, requestURL, httpMethod, sessionID);
        logger.info("IP=[{}]，响应方法=[{}]，参数=[{}]", ip, classMethod, params);

        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            // result的值就是被拦截方法的返回值
            result = pjp.proceed();
        } catch (Exception e) {
            logger.info("IP=[{}]请求URL=[{}]响应异常", ip, requestURL);
            throw e;
        }
        long endTime = System.currentTimeMillis();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        logger.info("IP=[{}]请求响应结果=[{}]，累计耗时=[{}]，sessionID=[{}]", ip, json, endTime - startTime, sessionID);
        return result;


    }
}