package com.tzh.star.Factory.impl;

import com.tzh.reflect.TZHInvoke;
import com.tzh.reflect.filter.FieldFilter;
import com.tzh.star.annotation.Resource;
import com.tzh.star.annotation.Value;
import com.tzh.star.utils.ELExpression;
import com.tzh.xml.filter.ElementFilter;
import com.tzh.xml.utils.XMLParser;
import org.dom4j.Element;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DIFactory {

    private BeanRegister beanRegister;
    private Map<String, String> DIMapper;
    private final String XMLDI = "xmlDI";
    private Map<String, Element> xmlBeans;
    private final Class[] fieldAnnos = new Class[]{Resource.class, Value.class};

    public DIFactory(BeanRegister beanRegister) {
        this.beanRegister = beanRegister;
    }


    public void innitDI(Map<String, Element>... xmlBeans) {
        DIMapper = new HashMap<String, String>();
        this.xmlBeans = new HashMap<String, Element>();

        for (Map<String, Element> beans : xmlBeans) {
            this.xmlBeans.putAll(beans);
            for (Map.Entry<String, Element> bean : beans.entrySet()) {
                DIMapper.put(bean.getKey(), XMLDI);
            }
        }

    }

    /**
     * 通过name判定是否是在xml的property形式注入还是以注解形式注入
     * 如果通过注解注入，name可以直接传入null
     * @param mainObj
     * @param name
     * @return
     */
    public Object dependencyInjection(Object mainObj, String name) {
        if (mainObj == null) {
            return null;
        }

        String DItype = DIMapper.get(name);
        if (DItype != null && DItype.equals(XMLDI)) {
            return XMLDependencyInjection(mainObj, name);
        } else {
            return annoDependencyInjection(mainObj);
        }
    }

    /**
     * 通过XML方式注入
     * @param mainObj
     * @return
     */
    public Object XMLDependencyInjection(Object mainObj, String name) {

        Element element = xmlBeans.get(name);
        if (element == null) {
            return mainObj;
        }

        XMLParser xmlParser = new XMLParser();
        Map<String, Element> nextLevelElement = xmlParser.getNextLevelElement(element, new ElementFilter() {
            public boolean doFilter(Element element) {
                return true;
            }

            public String defineKey(Element element) {
                return element.attributeValue("name");
            }
        });
        TZHInvoke invoke = new TZHInvoke(mainObj);
        for (Map.Entry<String, Element> entry : nextLevelElement.entrySet()) {
            Element ele = entry.getValue();
            Object refObj = null;
            String ref = ele.attributeValue("ref");
            String value = null;
            if (!(ref != null && ref.trim().length() > 0)) {
                value = handleEL(ele.attributeValue("value"));
            } else {
                refObj = beanRegister.getBean(ref);
            }
            try {
                if (value != null && value.trim().length() > 0) {
                    invoke.setField(entry.getKey(), value);
                } else if (refObj != null) {
                    invoke.setField(entry.getKey(), refObj);
                } else {
                    continue;
                }
            } catch (Exception e) {
                throw new RuntimeException(name + "的" + entry.getKey() + "注入失败");
            }
        }

        return invoke.getInstance();
    }

    /**
     * 处理EL表达式
     * 如果不是EL表达式则返回原字符串
     * @param str
     * @return
     */
    public String handleEL(String str) {
        if (str == null)
            return null;
        ELExpression elExpression = beanRegister.getElExpression();
        if (elExpression.isEL(str)) {
            return (String) elExpression.get(str);
        }
        return str;
    }

    /**
     * 通过注解注入
     * @param mainObj
     * @return
     */
    public Object annoDependencyInjection(Object mainObj) {
        TZHInvoke invoke = new TZHInvoke(mainObj);
        Field[] fields = invoke.filterField(invoke.getDeclaredFields(), new FieldFilter() {
            public boolean doFilter(Field field) {
                for (Class anno : fieldAnnos) {
                    if (field.getAnnotation(anno) != null) {
                        return true;
                    }
                }
                return false;
            }
        });

        annoFieldInjection(invoke, fields);
        return invoke.getInstance();
    }

    /**
     * 对注解修饰的类注入值
     * @param invoke
     * @param fields
     */
    public void annoFieldInjection(TZHInvoke invoke, Field[] fields) {
        for (Field field : fields) {
            if (field.getAnnotation(Resource.class) != null) {
                resourceInjection(invoke, field);
            } else if (field.getAnnotation(Value.class) != null) {
                valueInjection(invoke, field);
            }
        }
    }

    /**
     * 注入Value注解修饰的域
     * @param invoke
     * @param field
     */
    public void valueInjection(TZHInvoke invoke, Field field) {
        Value value = field.getAnnotation(Value.class);
        try {
            Method v = Value.class.getDeclaredMethod("value");
            Object result = v.invoke(value);
            invoke.setField(field, handleEL((String) result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注入resource注解修饰的域
     * @param invoke
     * @param field
     */
    public void resourceInjection(TZHInvoke invoke, Field field) {
        try {
            Map<String, Object> fieldAnnoValue = invoke.getFieldAnnoValue(field.getName(), Resource.class);
            Object value = fieldAnnoValue.get("value");
            String className = null;
            if (value != null && value.toString().trim().length() > 0) {
                className = (String) value;
            } else {
                Class<?> type = field.getType();
                String simpleName = type.getSimpleName();
                className = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
            }
            Object bean = beanRegister.getBean(className);
            invoke.setField(field, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
