package com.enoch.studio.interceptor;

import com.enoch.studio.constant.ControllerConstant;
import com.enoch.studio.constant.ResultCode;
import com.enoch.studio.entity.CustomerResponseEntity;
import com.enoch.studio.util.IpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


/**
 *
 * @className ValidationInterceptor
 * @description 验证拦截器
 * @author <a href="http://weibo.com/wangjwenoch">Enoch王建文</a>
 * @date 2017-8-23
 *       <p>
 *       -----------------------
 *       </p>
 *       <p>
 *       Please contact me if you have any questions
 *       </p>
 *       <p>
 *       如有疑问，请联系我
 *       </p>
 *       <p>
 *       新浪微博:@我想要两颗茜柚
 *       </p>
 *       <p>
 *       电子邮箱:wangjwenoch@163.com
 *       </p>
 *       <p>
 *       -----------------------
 *       </p>
 */
public class ValidationInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ValidationInterceptor.class);

    /**
     * 在业务处理器处理请求之前被调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
//        String clientIP = getClientIp(request);// 请求方的ip
        String clientIP = IpUtil.getIpAddress(request);// 请求方的ip
        String ipString = encoderByMd5(clientIP);// 加密后的ip
        Map map = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        /**
         * 身份验证
         * 验证方式：使用同一算法加密请求方IP，判断是否一致
         */
        if (ipString != null && ipString.equals(map.get(ControllerConstant.PATH_VARIABLE_NAME))) {
            return true;
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerResponseEntity responseEntity = CustomerResponseEntity.getResponse(ResultCode.ERROR_ACCESS_DENIED, clientIP);
            String json = objectMapper.writeValueAsString(responseEntity);
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
            writer.close();
            logger.info(json);
            return false;
        }
    }

    /**
     * 在业务处理器处理请求执行完成后,生成视图之前执行
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    /**
     *
     * @description 获取客户端IP地址
     * @param request
     *            request对象
     * @return 客户端IP地址
     * @author Enoch王建文
     * @date 2017-8-25
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     *
     * @description 使用MD5加密算法加密
     * @param message
     *            待加密字符串
     * @return 加密后的字符串[16进制大写形式表示]
     * @author Enoch
     * @date 2017年11月4日
     */
    private static String encoderByMd5(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            // converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString().toUpperCase();
        } catch (UnsupportedEncodingException ex) {
        } catch (NoSuchAlgorithmException ex) {
        }
        return digest;
    }

    public static void main(String[] args) {
        try {
            // 获取本机ip
            String ipString = InetAddress.getLocalHost().getHostAddress();
            System.out.println("本机IP:" + ipString);
            System.out.println("加密IP:" + encoderByMd5(ipString).toUpperCase());
            System.out.println("加密IP:" + encoderByMd5("10.4.101.221").toUpperCase());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } // 获得本机IP
    }

}
