package com.tzh.star.Factory;

import org.dom4j.Element;

import java.util.Map;

public interface BeanFactory {

    public void initBean(Map<String, Element> config);
    public Object getBean(String name);

}
