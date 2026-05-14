package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 登录：将密码MD5后与数据库匹配
     */
    @Override
    public User login(String username, String password) {
        String md5 = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getPassword, md5));
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        return user;
    }

    /**
     * 注册：校验用户名唯一性，密码MD5加密后入库
     */
    @Override
    public User register(User user) {
        User exist = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        if (exist != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        user.setBalance(BigDecimal.ZERO);
        user.setRole("USER");
        save(user);
        return user;
    }

    /**
     * 充值：余额累加
     */
    @Override
    public void recharge(Long userId, BigDecimal amount) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setBalance(user.getBalance().add(amount));
        updateById(user);
    }
}
