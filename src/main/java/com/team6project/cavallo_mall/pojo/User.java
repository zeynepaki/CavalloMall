package com.team6project.cavallo_mall.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Integer id;

    private String username;

    private String password;

    private String email;

    private String phoneNo;

    private Integer role;

    private Date createTime;

    private Date updateTime;

    // TODO: 21/03/2021 Author: Paul -- should this constructor not also include phoneNo, createTime and updateTime? 
    public User(String username, String password, String email, Integer role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public User() {}
}