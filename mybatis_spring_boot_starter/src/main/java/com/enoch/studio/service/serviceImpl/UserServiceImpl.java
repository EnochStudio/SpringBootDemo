package com.enoch.studio.service.serviceImpl;

import com.enoch.studio.entity.User;
import com.enoch.studio.mapper.UserMapper;
import com.enoch.studio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Enoch on 2017/12/11.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    public User getUserByID(Integer id) {
        return userMapper.getUserByID(id);
    }

    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    @Transactional(rollbackFor = Exception.class)
    /**
     * 因为Spring的默认的事务规则是遇到运行异常（RuntimeException）和程序错误（Error）才会回滚。
     * 如果想针对非检测异常进行事务回滚，可以在@Transactional 注解里使用rollbackFor 属性明确指定异常。
     * 例如下面这样，就可以正常回滚：
     @Transactional(rollbackFor = Exception.class)
     public void addMoney() throws Exception {
     //先增加余额
     accountMapper.addMoney();
     //然后遇到故障
     throw new SQLException("发生异常了..");
     }
     */
    public Integer saveUser(User user) throws Exception {
        Integer result = userMapper.insertUser(user);
        if (true)
            throw new SQLException("测试");
        return result;
    }
}
