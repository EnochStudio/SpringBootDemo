package com.enoch.studio.service;

import com.enoch.studio.entity.User;

import java.util.List;

/**
 * Created by Enoch on 2017/12/12.
 */
public interface UserService {
    User getUserByID(Integer id);

    List<User> getAllUser();

    Integer saveUser(User user) throws Exception;
}
