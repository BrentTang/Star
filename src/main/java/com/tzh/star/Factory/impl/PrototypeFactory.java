package com.tzh.star.Factory.impl;

import com.tzh.reflect.TZHClassLoader;
import com.tzh.star.utils.Commons;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

public class PrototypeFactory {

    // 注解类的全限定名保存在values(注解属性)中，以classPath为key
    private final String CLASSPATH = "classPath";
    private Map<String, Element> prototypeBeans;
    private Map<String, Map<String, Object>> prototypeAnnoBeans;
    private Map<String, Class> beansClass;
    private Map<String, String> classTypeMapper;

    public PrototypeFactory(Map<String, Element> prototypeBeans, Map<String, Map<String, Object>> prototypeAnnoBeans) {
        this.prototypeBeans = prototypeBeans;
        this.prototypeAnnoBeans = prototypeAnnoBeans;
    }

    public Object getBean(String name) {

        String key = classTypeMapper.get(name);
        Class clazz = null;
        if (key != null) {
            clazz = beansClass.get(key);
        } else {
            clazz = beansClass.get(name);
        }

        if (clazz == null) {
            return null;
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(clazz.getName() + "创建失败");
        }
    }

    /**
     * 初始化所有的单例bean
     */
    public void initBeans() {
        beansClass = new HashMap<String, Class>();
        classTypeMapper = new HashMap<String, String>();
        initBeansClass();
    }

    /**
     * 初始化所有bean
     */
    private void initBeansClass() {
        TZHClassLoader loader = new TZHClassLoader();
        initXMLBeansClass(loader);
        initAnnoBeansClass(loader);
    }

    /**
     * 初始化XML中配置的bean
     * @param loader
     */
    private void initXMLBeansClass(TZHClassLoader loader) {
        for (Map.Entry<String, Element> entry : prototypeBeans.entrySet()) {
            Element element = entry.getValue();
            String classPath = element.attributeValue("class");
            addBeansClass(loader, beansClass, entry.getKey(), classPath);
        }
    }

    /**
     * 初始化注解bean
     * @param loader
     */
    private void initAnnoBeansClass(TZHClassLoader loader) {
        for (Map.Entry<String, Map<String, Object>> entry : prototypeAnnoBeans.entrySet()) {
            Map<String, Object> values = entry.getValue();
            String classPath = (String) values.get(CLASSPATH);
            addBeansClass(loader, beansClass, classTypeMapper, entry.getKey(), classPath);
        }
    }

    /**
     * 添加已解析的bean,并添加接口类型映射
     * @param loader
     * @param beansClass
     * @param classTypeMapper
     * @param key
     * @param classPath
     */
    private void addBeansClass(TZHClassLoader loader, Map<String, Class> beansClass, Map<String, String> classTypeMapper, String key, String classPath) {
        try {
            if (!beansClass.containsKey(key)) {
                Class clazz = loader.load(classPath);
                if (classTypeMapper != null) {
                    Commons.addClassTypeMapper(classTypeMapper, clazz, key);
                }
                beansClass.put(key, clazz);
            } else {
                throw new RuntimeException(key + "不唯一");
            }
        } catch (Exception e) {
            throw new RuntimeException(classPath + "加载失败");
        }
    }

    /**
     * 添加已解析的bean
     * @param beansClass
     * @param key
     * @param classPath
     */
    private void addBeansClass(TZHClassLoader loader, Map<String, Class> beansClass, String key, String classPath) {
        addBeansClass(loader, beansClass, null, key, classPath);
    }

}
