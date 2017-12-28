package com.enoch.studio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Enoch on 2017/12/10.
 * 使用spring boot扫描的两种注解配置方式：
 * 1、@Controller
 * @EnableAutoConfiguration
 * @ComponentScan
 * 2、@SpringBootApplication
 * @SpringBootApplication 注解等价于以默认属性使用@Configuration，@EnableAutoConfiguration和@ComponentScan：
 * !!!另外application.java也应该按照官方的建议放在root目录下，这样才能扫描到Service和dao，不然还会引起，扫描不到注解的问题。
 */
@SpringBootApplication
//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
