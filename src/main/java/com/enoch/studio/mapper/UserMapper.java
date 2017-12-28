package com.enoch.studio.mapper;

import com.enoch.studio.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by Enoch on 2017/12/11.
 */
@Mapper
public interface UserMapper {
    public User getUserByID(Integer id);

    public List<User> getAllUser();
}
