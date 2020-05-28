package com.tzh.service.impl;

import com.tzh.dao.UserDao;
import com.tzh.service.UserService;
import com.tzh.star.annotation.Resource;
import com.tzh.star.annotation.Service;
import com.tzh.star.annotation.Value;

//@Service
public class UserServiceImpl implements UserService {

    //@Value("${ jdbc.username }")
    private String username;

    //@Resource("uDao")
    private UserDao userDao;

    public void add() {
        System.out.println("UserServiceIMpl.add(" + username + ")");
        userDao.add();
    }
}
