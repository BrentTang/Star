package com.tzh.star.servlet;

import com.tzh.star.Factory.impl.BeanRegister;
import com.tzh.star.Factory.impl.DIFactory;
import com.tzh.star.config.impl.ClassPathXMLApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class StarServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();

        ServletContext servletContext = getServletContext();
        ClassPathXMLApplicationContext app = (ClassPathXMLApplicationContext) servletContext.getAttribute("starContext");
        if (app != null) {
            BeanRegister beanRegister = app.getBeanRegister();
            if (beanRegister != null) {
                DIFactory diFactory = beanRegister.getDiFactory();
                if (diFactory != null) {
                    diFactory.dependencyInjection(this, null);
                }
            }
        }
    }

}
