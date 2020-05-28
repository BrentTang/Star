package com.tzh.dao.impl;

import com.tzh.dao.UserDao;
import com.tzh.star.annotation.Component;

@Component(scope = "prototype")
public class UserDaoImpl implements UserDao {

    public void add() {
        System.out.println("UserDaoImpl.add()");
    }
}
