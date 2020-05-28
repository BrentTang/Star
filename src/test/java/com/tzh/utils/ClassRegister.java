package com.tzh.utils;

/**
 * @author 豪
 * @title: Register
 * @projectName Spring
 * @description: TODO
 * @date 2019/5/2719:34
 */
public abstract class ClassRegister<T> {

    private T container;

    public ClassRegister(T container) {
        this.container = container;
    }

    public abstract void doFilter(Class t);

    public T getContainer() {
        return container;
    }

}
