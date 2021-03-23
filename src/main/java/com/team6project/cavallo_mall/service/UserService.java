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
     * Method that registers a user in the database
     * @param user User object representing a user
     * @return a user service query result indicating whether a user has been successfully registered or not
     */
    RespVo<User> register(User user);

    /**
     * Method that logs a user into the system
     * @param username String representing a user's username
     * @param password String representing a user's password
     * @return a user service query result indicating whether a user has been successfully logged in or not
     */
    RespVo<User> login(String username, String password);
}
