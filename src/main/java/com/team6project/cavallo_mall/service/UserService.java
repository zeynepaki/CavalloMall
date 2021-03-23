package com.team6project.cavallo_mall.service;

import com.team6project.cavallo_mall.pojo.User;
import com.team6project.cavallo_mall.vo.RespVo;

/**
 * description: Interface for users
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/9 2:29
 */
public interface UserService {

    /**
     * register
     * @param user
     * @return
     */
    RespVo<User> register(User user);

    /**
     * login
     * @param username
     * @param password
     * @return
     */
    RespVo<User> login(String username, String password);
}
