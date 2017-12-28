package com.enoch.studio.config;

import com.enoch.studio.interceptor.ValidationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 拦截器配置类
 * Created by Enoch on 2017/12/12.
 */
@Configuration
public class ValidationConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        ValidationInterceptor validationInterceptor = new ValidationInterceptor();
//        registry.addInterceptor(validationInterceptor).addPathPatterns("/**");
        registry.addInterceptor(validationInterceptor).addPathPatterns("/**").excludePathPatterns("/error");
        super.addInterceptors(registry);
    }
}
