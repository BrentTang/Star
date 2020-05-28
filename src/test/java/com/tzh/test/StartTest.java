package com.tzh.test;

import com.tzh.dao.UserDao;
import com.tzh.service.UserService;
import com.tzh.star.config.ApplicationContext;
import com.tzh.star.config.impl.ClassPathXMLApplicationContext;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class StartTest {

    @Test
    public void test1() throws FileNotFoundException {
        ClassPathXMLApplicationContext app = new ClassPathXMLApplicationContext("star.xml");
        Object userService1 = app.getBean("userService");
        Object userService2 = app.getBean("userService");
        System.out.println(userService1);
        System.out.println(userService2);
        System.out.println(userService1 == userService2);
    }

    @Test
    public void test2() throws FileNotFoundException {
        ClassPathXMLApplicationContext app = new ClassPathXMLApplicationContext("star.xml");
        Object userDao1 = app.getBean("dao");
        Object userDao2 = app.getBean("dao");
        Object userDao3 = app.getBean("userDao");
        Object userDao4 = app.getBean("userDao");
        System.out.println("userDao1=" + userDao1);
        System.out.println("userDao2=" + userDao2);
        System.out.println("userDao1 == userDao2 ? " + (userDao1 == userDao2));
        System.out.println("userDao3=" + userDao3);
        System.out.println("userDao4=" + userDao4);
        System.out.println("userDao1 == userDao3 ? " + (userDao1 == userDao3));
        System.out.println("userDao2 == userDao3 ? " + (userDao2 == userDao3));
        System.out.println("userDao3 == userDao4 ? " + (userDao3 == userDao4));
    }

    @Test
    public void test4() throws FileNotFoundException {
        ApplicationContext app = new ClassPathXMLApplicationContext("star.xml");
        UserService userService1 = (UserService) app.getBean("userService");
        userService1.add();
    }

    @Test
    public void test5() throws FileNotFoundException {
        ApplicationContext app = new ClassPathXMLApplicationContext("star.xml");
        UserDao userDao = (UserDao) app.getBean("uDao");
        System.out.println(userDao);
        //userService1.add();
    }

    @Test
    public void test6() throws FileNotFoundException {
        ApplicationContext app = new ClassPathXMLApplicationContext("star.xml");
        System.out.println(app.getValue("${ db.driver }"));
        System.out.println(app.getValue("jdbc.url"));
        System.out.println(app.getValue("${ database.username }"));
    }
    @Test
    public void test7() throws FileNotFoundException {
        Object s = null;
        String a = (String) s;
        System.out.println(a);
    }
    @Test
    public void test8() throws FileNotFoundException {
        String a = "123,";
        String[] split = a.split(",");
        for (String s : split) {
            System.out.println(s + "--");
        }
    }
    @Test
    public void test9() throws FileNotFoundException {
        Set<String> tree = new TreeSet<String>();
        tree.add("haha");
        tree.add("haha");
        System.out.println(tree.size());
    }
    @Test
    public void test10() throws FileNotFoundException {
        System.out.println(System.getProperty("java.version"));
    }

}
