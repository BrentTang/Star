package com.tzh.star.Factory.impl.register;

import com.tzh.reflect.scan.ClassRegister;
import com.tzh.star.interfaces.ApplicationContextWired;

import java.util.List;

/**
 * @author 豪
 * @title: ApplicationContextRegister
 * @projectName Spring
 * @description: TODO
 * @date 2019/5/2720:27
 */
public class ApplicationContextRegister extends ClassRegister<List<Class>> {

    private final static Class APPLICATIONCONTEXTWIRED = ApplicationContextWired.class;

    public ApplicationContextRegister(List<Class> container) {
        super(container);
    }

    public void doFilter(Class clazz) {
        Class[] interfaces = clazz.getInterfaces();
        for (Class anInterface : interfaces) {
            if (anInterface.equals(APPLICATIONCONTEXTWIRED)) {
                getContainer().add(clazz);
            }
        }
    }
}
