package com.team6project.cavallo_mall.service.impl;

import com.team6project.cavallo_mall.dao.UserMapper;
import com.team6project.cavallo_mall.enums.UserRole;
import com.team6project.cavallo_mall.pojo.User;
import com.team6project.cavallo_mall.service.UserService;
import com.team6project.cavallo_mall.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

import static com.team6project.cavallo_mall.enums.RespStatusAndMsg.*;
import static com.team6project.cavallo_mall.enums.UserRole.ADMIN;

/**
 * description: Implementing class for user service behaviour
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/16 23:10
 */
@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public RespVo<User> register(User user) {
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if (countByUsername > 0) return RespVo.error(USERNAME_EXIST);
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByEmail > 0) return RespVo.error(EMAIL_EXIST);
        user.setRole(UserRole.CUSTOMER.getCode());
        // MD5 Hash
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        int resultCount = userMapper.insertSelective(user);
        if (resultCount <= 0) return RespVo.error(SERVER_ERROR);
        return RespVo.success();
    }

    @Override
    public RespVo<User> login(String username, String password) {
        /*User user = userMapper.selectByUsername(username);
        if (user == null) return RespVo.error(USERNAME_OR_PASSWORD_ERROR);
        if (! user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            // wrong password
            return RespVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("");*/
        User user = verifyUser(username, password);
        if (user == null) return RespVo.error(USERNAME_OR_PASSWORD_ERROR);
        if (ADMIN.getCode().equals(user.getRole())) return RespVo.error(USER_ROLE_ERROR);
        return RespVo.success(user);
    }

    public RespVo<User> loginForAdmin(String username, String password) {
        User user = verifyUser(username, password);
        if (user == null) return RespVo.error(USERNAME_OR_PASSWORD_ERROR);
        if (! ADMIN.getCode().equals(user.getRole())) return RespVo.error(USER_ROLE_ERROR);
        return RespVo.success(user);
    }

    private User verifyUser(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null || ! user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) return null;
        user.setPassword("");
        return user;
    }

}
