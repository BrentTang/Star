package com.tzh.star.config;

public abstract class ApplicationContext {

    /**
     * 通过id获取Bean
     * @param id
     * @return
     */
    public abstract Object getBean(String id);

    public abstract Object getValue(String key);

}
