package com.tzh.star.Factory.impl;

import com.tzh.reflect.filter.ClassFilter;
import com.tzh.reflect.scan.ClassRegister;
import com.tzh.reflect.scan.PackageScan;
import com.tzh.star.Factory.impl.register.AnnotationBeanRegister;
import com.tzh.star.utils.Commons;
import com.tzh.star.Factory.BeanFactory;
import com.tzh.star.annotation.Component;
import com.tzh.star.annotation.Controller;
import com.tzh.star.annotation.Service;
import com.tzh.star.utils.ELExpression;
import com.tzh.xml.filter.ElementFilter;
import com.tzh.xml.utils.XMLParser;
import org.dom4j.Element;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanRegister implements BeanFactory {

    // beans节点名称
    private final String BEANS_ELEMENT = "beans";
    // component-scan元素节点名
    private final String COMPONENTSCAN = "component-scan";
    private final String PROTOTYPE = "prototype";
    private final String SINGLETON = "singleton";
    // 注解类的全限定名保存在values(注解属性)中，以classPath为key
    private final String CLASSPATH = "classPath";
    // 保存beans元素节点里的所有bean节点
    private Map<String, Element> beans;
    // 带有component注解的类
    private List<Class<?>> annoBeans;
    // component-scan元素的basePackage值
    private String BASEPACKAGE;
    // key为bean的id，value指定为单例容器或多例容器
    private Map<String, String> beansMapper;
    // 多例bean
    private Map<String, Element> prototypeBeans;
    // 多例注解Beans
    private Map<String, Map<String, Object>> prototypeAnnoBeans;
    // 单例注解Beans
    private Map<String, Map<String, Object>> singletonAnnoBeans;
    // 单例bean
    private Map<String, Element> singletonBeans;
    // 注解Class
    private static Class[] annotations =
            new Class[]{ Controller.class, Service.class, Component.class };

    private SingletonFactory singletonFactory;
    private PrototypeFactory prototypeFactory;
    private DIFactory diFactory;

    private ELExpression elExpression;

    public ELExpression getElExpression() {
        return elExpression;
    }

    public DIFactory getDiFactory() {
        return diFactory;
    }

    public void initBean(Map<String, Element> config, ELExpression elExpression) {
        this.elExpression = elExpression;

        // 获取XML中配置的bean
        getBeans(config);
        // 获取XML中的basePackage
        getBasePackage(config);
        beansMapper = new ConcurrentHashMap<String, String>();
        prototypeBeans = new HashMap<String, Element>();
        prototypeAnnoBeans = new HashMap<String, Map<String, Object>>();
        singletonBeans = new HashMap<String, Element>();
        singletonAnnoBeans = new HashMap<String, Map<String, Object>>();
        // 创建bean
        createBeans(beans, BASEPACKAGE);
    }
    public void initBean(Map<String, Element> config) {
        initBean(config, null);
    }

    private void createBeans(Map<String, Element> beansElement, String basePackage) {

        annoBeans = new ArrayList<Class<?>>();
        loadBasePackageBeans(basePackage);
        distributBeans(annoBeans, beans);
        // 创建SingletonFactory并初始化
        singletonFactory = new SingletonFactory(singletonBeans, singletonAnnoBeans);
        singletonFactory.initBeans();
        // 创建PrototypeFactory
        prototypeFactory = new PrototypeFactory(prototypeBeans, prototypeAnnoBeans);
        prototypeFactory.initBeans();
        // 创建依赖注入工厂
        diFactory = new DIFactory(this);
        diFactory.innitDI(prototypeBeans, singletonBeans);
    }

    /**
     * 加载basePackage下含有Controller.class, Service.class, Component.class注解的Beans
     * @param basePackage
     */
    private void loadBasePackageBeans(String basePackage) {

        PackageScan packageScan = new PackageScan();
        try {
            packageScan.scan(basePackage, new AnnotationBeanRegister(annoBeans));
        } catch (Exception e) {

        }
    }

    /**
     * 分配单例bean与多例bean
     * @param beansClass
     */
    private void distributBeans(List<Class<?>> beansClass, Map<String, Element> beans) {
        if (beansClass != null) {
            distributAnnoBeans(beansClass);
        }
        if (beans != null) {
            distributXMLBeans(beans);
        }
    }

    /**
     * 分发XML中配置的bean
     * @param beans
     */
    private void distributXMLBeans(Map<String, Element> beans) {

        for (Map.Entry<String, Element> entry : beans.entrySet()) {
            Element element = entry.getValue();
            String scope = element.attributeValue("scope");
            if (scope != null && scope.trim().equals(PROTOTYPE)) {
                prototypeBeans.put(entry.getKey(), element);
                addUniqueKey(beansMapper, entry.getKey(), PROTOTYPE);
            } else {
                singletonBeans.put(entry.getKey(), element);
                addUniqueKey(beansMapper, entry.getKey(), SINGLETON);
            }
        }

    }

    /**
     * 为当前map添加key-value前会校验key是否存在
     * 如果存在则会报错
     * @param map
     * @param key
     * @param val
     */
    private void addUniqueKey(Map map, String key, Object val) {
        if (!map.containsKey(key)) {
            map.put(key, val);
        } else {
            throw new IllegalArgumentException("请保证" + key + "的唯一性！");
        }
    }

    /**
     * 分发注解bean
     * @param beansClass
     */
    private void distributAnnoBeans(List<Class<?>> beansClass) {
        for (Class beanClass : beansClass) {
            Annotation anno = getComponentAnno(beanClass);
            if (anno != null) {
                Map<String, Object> annoValues = getAnnoValues(anno);
                // 当前class的全路径名保存下来
                annoValues.put(CLASSPATH, beanClass.getName());
                Object scope = annoValues.get("scope");

                /*String simpleName = (String) annoValues.get("value");
                if (!(simpleName != null && simpleName.trim().length() > 0)) {
                    simpleName = beanClass.getSimpleName();
                    simpleName = simpleName.substring(0, 1).toLowerCase() +
                            simpleName.substring(1);
                }*/
                // 如果不指定value，将会以类名的第一个字母小写
                String simpleName = getFirstWordLowerName((String) annoValues.get("value"),
                        beanClass.getSimpleName());
                if (scope != null && scope.equals(PROTOTYPE)) {
                    this.prototypeAnnoBeans.put(simpleName, annoValues);
                    addUniqueKey(beansMapper, simpleName, PROTOTYPE);
                    Commons.addClassTypeMapper(beansMapper, beanClass, PROTOTYPE);
                } else {
                    this.singletonAnnoBeans.put(simpleName, annoValues);
                    addUniqueKey(beansMapper, simpleName, SINGLETON);
                    Commons.addClassTypeMapper(beansMapper, beanClass, SINGLETON);
                }
            }
        }
    }

    /**
     * priorName不为空时返回priorName
     * priorName为空时，返回secondName的第一个字母小写形式
     * @param priorName
     * @param secondName
     * @return
     */
    private String getFirstWordLowerName(String priorName, String secondName) {
        if (!(priorName != null && priorName.trim().length() > 0)) {
            secondName = secondName.substring(0, 1).toLowerCase() +
                    secondName.substring(1);
            return secondName;
        }
        return priorName;
    }

    /**
     * 获取这个注解实例的所有value
     * @param anno
     * @return
     */
    private Map<String, Object> getAnnoValues(Annotation anno) {
        Map<String, Object> values = new HashMap<String, Object>();
        for (Class annoClass : annotations) {
            if (annoClass.isInstance(anno)) {
                Method[] methods = annoClass.getDeclaredMethods();
                for (Method m : methods) {
                    if (!(m.getParameterTypes() != null && m.getParameterTypes().length > 0)) {
                        try {
                            values.put(m.getName(), m.invoke(anno));
                        } catch (Exception e) {
                            throw new RuntimeException("解析" + annoClass.getName() + "注解的" + m.getName() + "失败！");
                        }
                    }
                }
            }
        }
        return values;
    }

    /**
     * 查看类是否含有Controller.class, Service.class, Component.class
     * @param clazz
     * @return
     */
    public static Annotation getComponentAnno(Class clazz) {
        for (Class anno : annotations) {
            Annotation annotation = clazz.getAnnotation(anno);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 获取component-scan节点的basePackage
     * @param config
     */
    private void getBasePackage(Map<String, Element> config) {
        Element element = config.get(COMPONENTSCAN);
        if (element != null) {
            this.BASEPACKAGE = element.attributeValue("basePackage");
        } else {
            this.BASEPACKAGE = null;
        }
    }

    /**
     * 获取Bean元素节点
     * @param config
     */
    private void getBeans(Map<String, Element> config) {
        Element beansNode = config.get(BEANS_ELEMENT);
        if (beansNode == null) {
            return ;
        }
        XMLParser xmlParser = new XMLParser();
        beans = xmlParser.getNextLevelElement(beansNode, new ElementFilter() {
            public boolean doFilter(Element element) {
                String id = element.attributeValue("id");
                if (id != null && id.trim().length() > 0) {
                    return true;
                }
                return false;
            }

            public String defineKey(Element element) {
                return element.attributeValue("id");
            }
        });
    }

    public Object getBean(String name) {

        String beanType = beansMapper.get(name);
        Object main = null;
        if (beanType != null) {
            if (beanType.equals(SINGLETON)) {
                main = singletonFactory.getBean(name);
            } else if (beanType.equals(PROTOTYPE)) {
                main = prototypeFactory.getBean(name);
            }
        }
        return diFactory.dependencyInjection(main, name);
    }
}
