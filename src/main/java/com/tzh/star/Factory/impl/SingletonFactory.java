package com.tzh.star.Factory.impl;

import com.tzh.reflect.TZHClassLoader;
import com.tzh.star.utils.Commons;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

public class SingletonFactory {

    private final String CLASSPATH_KEY = "classPath";
    private Map<String, Element> singletonBeans;
    private Map<String, Map<String, Object>> singletonAnnoBeans;
    private Map<String, Object> beans;
    private Map<String, String> classTypeMapper;


    public SingletonFactory(Map<String, Element> singletonBeans, Map<String, Map<String, Object>> singletonAnnoBeans) {
        this.singletonBeans = singletonBeans;
        this.singletonAnnoBeans = singletonAnnoBeans;
    }

    /**
     * 先查看是否有符合的实现类，没有就直接到beans中查找
     * @param name
     * @return
     */
    public Object getBean(String name) {

        String key = classTypeMapper.get(name);
        if (key != null) {
            return beans.get(key);
        }

        return beans.get(name);
    }

    /**
     * 初始化所有的单例bean
     */
    public void initBeans() {
        beans = new HashMap<String, Object>();
        classTypeMapper = new HashMap<String, String>();
        createBeans();
    }

    /**
     * 创建XML中所有的bean
     */
    private void createBeans() {
        createXMLBeans();
        createAnnoBeans();
    }

    /**
     * 创建注解bean
     */
    private void createAnnoBeans() {
        if (singletonAnnoBeans == null) {
            return ;
        }

        TZHClassLoader loader = new TZHClassLoader();
        for (Map.Entry<String, Map<String, Object>> beanEntry : singletonAnnoBeans.entrySet()) {
            Map<String, Object> values = beanEntry.getValue();
            String classPath = (String) values.get(CLASSPATH_KEY);
            addInstance(loader, beanEntry.getKey(), classPath);
            Commons.addClassTypeMapper(loader, classTypeMapper, classPath, beanEntry.getKey());
        }
    }

    /**
     * 创建XML配置的bean
     */
    private void createXMLBeans() {
        if (singletonBeans == null) {
            return ;
        }

        TZHClassLoader loader = new TZHClassLoader();
        for (Map.Entry<String, Element> entry : singletonBeans.entrySet()) {
            Element element = entry.getValue();
            String classPath = element.attributeValue("class");
            addInstance(loader, entry.getKey(), classPath);
        }
    }

    /**
     * 将bean装入容器时保证唯一性
     * @param loader
     * @param key
     * @param classPath
     */
    private void addInstance(TZHClassLoader loader, String key, String classPath) {
        try {
            Class clazz = loader.load(classPath);
            if (clazz == null) {
                throw new IllegalArgumentException("初始化" + key + "失败");
            }
            if (!this.beans.containsKey(key)) {
                this.beans.put(key, clazz.newInstance());
            } else {
                throw new IllegalArgumentException("请保证" + key + "的唯一性");
            }
        } catch (Exception e) {
            throw new RuntimeException(key + "创建失败");
        }
    }

}
