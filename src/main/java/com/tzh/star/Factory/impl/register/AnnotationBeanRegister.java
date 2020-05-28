package com.tzh.star.Factory.impl.register;

import com.tzh.reflect.scan.ClassRegister;
import com.tzh.star.Factory.impl.BeanRegister;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author 豪
 * @title: AnnotationBeanRegister
 * @projectName Spring
 * @description: TODO
 * @date 2019/5/2720:38
 */
public class AnnotationBeanRegister extends ClassRegister<List<Class<?>>> {

    public AnnotationBeanRegister(List<Class<?>> container) {
        super(container);
    }

    public void doFilter(Class clazz) {
        Annotation anno = BeanRegister.getComponentAnno(clazz);
        if (anno != null) {
            getContainer().add(clazz);
        }
    }
}
