package com.tzh.star.Factory;

import org.dom4j.Element;

public interface PropertyFactory {

    public void load(String... classpaths);
    public void innitPropertyFactory(Element element);
    public Object get(String key);

}
