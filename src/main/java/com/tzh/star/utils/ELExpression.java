package com.tzh.star.utils;

import com.tzh.star.Factory.PropertyFactory;

public class ELExpression {

    private final String PREFIX = "${";
    private final String SUFFIX = "}";
    private PropertyFactory propertyFactory;

    public ELExpression(PropertyFactory propertyFactory) {
        this.propertyFactory = propertyFactory;
    }

    /**
     * 获取value，如果是EL格式
     * @param expression
     * @return
     */
    public Object get(String expression) {
        return get(expression, false);
    }

    /**
     * supportNotEL为true时，如果不符合EL也会以当前字符串为key去查找
     * @param expression
     * @param supportNotEL
     * @return
     */
    public Object get(String expression, boolean supportNotEL) {
        if (propertyFactory == null) {
            return null;
        }
        if (isEL(expression)) {
            if (expression.trim().length() <= 3) {
                return null;
            }
            String content = parse(expression);
            return propertyFactory.get(content.trim());
        } else if (supportNotEL){
            return propertyFactory.get(expression);
        }
        return null;
    }

    /**
     * 解析EL表达式
     * @param expression
     * @return
     */
    private String parse(String expression) {
        if (!(expression != null && expression.trim().length() > 0)) {
            return null;
        }
        String trim = expression.trim();
        return trim.substring(PREFIX.length(), trim.length() - SUFFIX.length());
    }

    /**
     * 通过前缀（${）与后缀（}）判断是否是EL表达式
     * @param expression
     * @return
     */
    public boolean isEL(String expression) {
        String trimExpression = expression.trim();
        if (trimExpression.startsWith(PREFIX) && trimExpression.endsWith(SUFFIX)) {
            return true;
        }
        return false;
    }

}
