package com.enoch.studio.controller;

import com.enoch.studio.constant.ControllerConstant;
import com.enoch.studio.entity.CustomerResponseEntity;
import com.enoch.studio.entity.User;
import com.enoch.studio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping("/getUserByID"+ ControllerConstant.URL_SUFFIX)
    @ResponseBody
    public CustomerResponseEntity getUserByID(@RequestParam Integer id) {
        return CustomerResponseEntity.getResponse(userService.getUserByID(1));
    }

    @RequestMapping("/getAllUser"+ ControllerConstant.URL_SUFFIX)
    @ResponseBody
    public CustomerResponseEntity getAllUser() {
        return CustomerResponseEntity.getResponse(userService.getAllUser());
    }

    @RequestMapping("/saveUser"+ ControllerConstant.URL_SUFFIX)
    @ResponseBody
    public CustomerResponseEntity saveUser() throws Exception {
        User user = new User();
        user.setUsername("test1");
        user.setPassword("test1");
        user.setSex(0);
        return CustomerResponseEntity.getResponse(userService.saveUser(user));
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(UserController.class, args);
    }
}
