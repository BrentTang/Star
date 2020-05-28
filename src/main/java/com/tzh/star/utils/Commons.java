package com.tzh.star.utils;

import com.tzh.reflect.TZHClassLoader;

import java.util.Map;

public class Commons {

    /**
     * 通过接口找实现类
     * @param classTypeMapper
     * @param classPath
     * @param instanceKey
     */
    public static void addClassTypeMapper(Map<String, String> classTypeMapper, String classPath, String instanceKey) {
        addClassTypeMapper(null, classTypeMapper, classPath, instanceKey);
    }

    public static void addClassTypeMapper(Map<String, String> classTypeMapper, Class clazz, String instanceKey) {
        Class[] interfaces = clazz.getInterfaces();
        for (Class in : interfaces) {
            String simpleName = in.getSimpleName();
            simpleName = simpleName.substring(0, 1).toLowerCase() +
                    simpleName.substring(1);
            if (!classTypeMapper.containsKey(simpleName)) {
                classTypeMapper.put(simpleName, instanceKey);
            } else {
                throw new RuntimeException("无法自动注入" + clazz.getName() + "请保证唯一性");
            }
        }
    }

    /**
     * 通过接口找实现类
     * @param loader
     * @param classTypeMapper
     * @param classPath
     * @param val
     */
    public static void addClassTypeMapper(TZHClassLoader loader, Map<String, String> classTypeMapper, String classPath, String val) {
        if (loader == null) {
            loader = new TZHClassLoader();
        }

        try {
            Class clazz = loader.load(classPath);
            addClassTypeMapper(classTypeMapper, clazz, val);
        } catch (Exception e) {
            throw new RuntimeException(classPath + "加载失败");
        }
    }

}
