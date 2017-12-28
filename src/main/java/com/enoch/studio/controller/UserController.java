package com.enoch.studio.controller;

import com.enoch.studio.entity.User;
import com.enoch.studio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @RestController :该注解是spring 4.0引入的。
 * 查看源码可知其包含了 @Controller 和 @ResponseBody 注解。
 * 我们可以将其看做对@Controller注解的增强与细分,常用来返回json格式的数据。
 * Created by Enoch on 2017/12/10.
 */

//@RestController
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/getUserByID")
    @ResponseBody
    User getUserByID() {
        return userService.getUserByID(1);
    }

    @RequestMapping("/getAllUser")
    @ResponseBody
    List<User> getAllUser() {
        return userService.getAllUser();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(UserController.class, args);
    }
}
