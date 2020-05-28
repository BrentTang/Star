package com.tzh.star.config.impl;

import com.tzh.star.Factory.PropertyFactory;
import com.tzh.star.Factory.impl.BeanRegister;
import com.tzh.star.Factory.impl.PropertyFactoryImpl;
import com.tzh.star.config.ApplicationContext;
import com.tzh.star.utils.ELExpression;
import com.tzh.xml.utils.XMLParser;
import org.dom4j.Element;

import java.io.FileNotFoundException;
import java.util.Map;

public class ClassPathXMLApplicationContext extends ApplicationContext {

    private XMLParser xmlParser;
    private BeanRegister beanRegister;
    private final String PROPERTYLOAD = "property-load";

    private ELExpression elExpression;

    public ClassPathXMLApplicationContext(String classPathXML) throws FileNotFoundException {
        // 加载xml文件
        this.xmlParser = new XMLParser(classPathXML);
        Map<String, Element> secondLevelElement = this.xmlParser.getSecondLevelElement();
        // 加载properties文件
        PropertyFactory propertyFactory = new PropertyFactoryImpl();
        propertyFactory.innitPropertyFactory(secondLevelElement.get(PROPERTYLOAD));
        this.elExpression = new ELExpression(propertyFactory);
        // 初始化Beans
        beanRegister = new BeanRegister();
        beanRegister.initBean(secondLevelElement, this.elExpression);
    }

    public BeanRegister getBeanRegister() {
        return beanRegister;
    }

    /**
     * 通过id获取Bean
     * @param id
     * @return
     */
    public Object getBean(String id) {
        return this.beanRegister.getBean(id);
    }

    /**
     * properties文件中的key
     * @param key
     * @return
     */
    public Object getValue(String key) {
        return elExpression.get(key, true);
    }
}
