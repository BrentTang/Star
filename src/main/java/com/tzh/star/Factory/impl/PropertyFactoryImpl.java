package com.tzh.star.Factory.impl;

import com.tzh.star.Factory.PropertyFactory;
import org.dom4j.Element;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyFactoryImpl implements PropertyFactory {

    private Properties configs;
    private final String CLASSPATH = "classpath";
    private final String pathSplit = ",";
    private final String classPathUrlPrefix = "file:/";
    private static final String fileSeparator = "/";
    private final String matcherPlaceholder = "*";

    public PropertyFactoryImpl() {
        this.configs = new Properties();
    }

    /**
     * 加载配置文件
     * @param element
     */
    public void innitPropertyFactory(Element element) {
        if (element == null) {
            return ;
        }
        String classpaths = element.attributeValue(CLASSPATH);

        if (classpaths != null && classpaths.trim().length() > 0) {
            load(classPathsConvertor(classpaths.split(pathSplit)));
        }
    }

    /**
     * 如果包含通配符的路径将被转换为可用的正常路径
     * @param classpaths
     * @return
     */
    private String[] classPathsConvertor(String[] classpaths) {
        if (!(classpaths != null && classpaths.length > 0)) {
            return null;
        }
        // *.properties db/*.properties
        Set<String> paths = new TreeSet<String>();
        URL classpathUrl = this.getClass().getClassLoader().getResource("");
        //System.out.println(classpathUrl.toString().substring(classPathUrlPrefix.length()));
        String classpath = classpathUrl.toString().substring(classPathUrlPrefix.length());

        for (String p : classpaths) {
            addPath(paths, classpath, p, fileSeparator, matcherPlaceholder);
        }

        return paths.toArray(new String[paths.size()]);
    }

    /**
     * 添加符合path的文件路径
     * @param paths
     * @param classpath
     * @param path
     * @param fileSeparator
     * @param matcherPlaceholder
     */
    private void addPath(Set<String> paths, String classpath, String path, String fileSeparator, String matcherPlaceholder) {

        if (path.contains(matcherPlaceholder)) {
            if (path.contains(fileSeparator)) {
                // 包含目录并且使用通配符
                int lastFileSeparator = path.lastIndexOf(fileSeparator);
                String dir = path.substring(0, lastFileSeparator + 1);
                String pattern = path.substring(lastFileSeparator + 1);
                getMatcherFilePath(paths, classpath, dir, pattern);
            } else {
                // 只包含通配符
                getMatcherFilePath(paths, classpath, "", path);
            }
        } else {
            // 正常路径
            paths.add(path);
        }
    }

    /**
     * 获取匹配file路径的file相对路径
     * @param paths
     * @param classpath
     * @param dir
     * @param pattern
     */
    private static void getMatcherFilePath(Set<String> paths, final String classpath,
                                          final String dir, final String pattern) {

        File dirFile = new File(classpath + dir);
        if (dirFile.exists() && dirFile.isDirectory()) {
            final Set<String> set = new TreeSet<String>();
            dirFile.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    if (file.isDirectory()) {
                        PropertyFactoryImpl.getMatcherFilePath(set, classpath, dir + file.getName() + fileSeparator, pattern);
                    }
                    if (PropertyFactoryImpl.matcher(pattern, file.getName())) {
                        set.add(dir + file.getName());
                    }
                    return false;
                }
            });
            paths.addAll(set);
        }
    }

    /**
     * 匹配符合regex的str
     * @param regex
     * @param str
     * @return
     */
    private static boolean matcher(String regex, String str) {
        String re = regex.replaceAll("\\.", "\\\\.").replaceAll("\\*", "\\.*");
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 加载配置文件
     * @param classpaths
     */
    public void load(String... classpaths) {
        for (String classpath : classpaths) {
            //System.out.println("加载：" + classpath);
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(classpath);
            Properties properties = new Properties();
            try {
                properties.load(in);
                this.configs.putAll(properties);
            } catch (IOException e) {
                throw new IllegalArgumentException(classpath + "不存在！");
            }
        }
    }

    /**
     * 获取value
     * @param key
     * @return
     */
    public Object get(String key) {
        return this.configs.get(key);
    }
}
