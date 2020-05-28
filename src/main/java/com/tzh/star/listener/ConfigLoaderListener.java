package com.tzh.star.listener;

import com.tzh.star.config.ApplicationContext;
import com.tzh.star.config.impl.ClassPathXMLApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ConfigLoaderListener implements ServletContextListener {

    private final String CONFIGCLASSPATH = "configClassPath";

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        String configClassPath = servletContext.getInitParameter(CONFIGCLASSPATH);
        try {
            if (!(configClassPath != null && configClassPath.trim().length() > 0)) {
                configClassPath = "star.xml";
            }
            //String contextPath = servletContext.getRealPath("");
            //ApplicationContext app = new ClassPathXMLApplicationContext("absolutepath:" + contextPath + "/" + configClassPath);
            ApplicationContext app = new ClassPathXMLApplicationContext(configClassPath);

            servletContext.setAttribute("starContext", app);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(configClassPath + "配置文件加载失败！");
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
