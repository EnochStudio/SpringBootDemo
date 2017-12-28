package com.enoch.studio.service;

import com.enoch.studio.entity.User;
import com.enoch.studio.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Enoch on 2017/12/11.
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User getUserByID(Integer id) {
        return userMapper.getUserByID(id);
    }

    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }
}
