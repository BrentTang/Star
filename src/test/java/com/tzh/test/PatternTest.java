package com.tzh.test;

import com.tzh.star.Factory.impl.register.ApplicationContextRegister;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {

    @Test
    public void test1() {
        String classPathUrlPrefix = "file:/";
        URL classpathUrl = this.getClass().getClassLoader().getResource("");
        String classpath = classpathUrl.toString().substring(classPathUrlPrefix.length());
        //String[] classpaths = new String[]{"cn/*.pro", "t*.property", "haha.*"};
        String[] classpaths = new String[]{
                "cn/*.properties",
                //"cn/database.properties",
                "jdbc.properties",
                "db.*"};
        for (String p : classpaths) {
            if (p.contains("/") && p.contains("*")) {
                int lastSeparator = p.lastIndexOf("/");
                String dir = p.substring(0, lastSeparator + 1);
                File dirFile = new File(classpath + dir);
                System.out.println("包含目录" + classpath + dir);
                final String name = p.substring(lastSeparator + 1);
                File[] files = dirFile.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        if (pathname.isFile()) {
                            PatternTest p = new PatternTest();
                            boolean matcher = p.matcher(name, pathname.getName());
                            System.out.println("name:" + name + ", file:" + pathname.getName() + "  ---------" + matcher);
                            return true;
                        }

                        return false;
                    }
                });
            } else if (p.contains("*")) {
                File dir = new File(classpath);
                final String pattern = p;
                File[] files = dir.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        if (pathname.isDirectory()) {
                            return false;
                        }

                        PatternTest p = new PatternTest();
                        boolean matcher = p.matcher(pattern, pathname.getName());
                        System.out.println("name:" + pattern + ", file:" + pathname.getName() + "  ---------" + matcher);
                        return true;
                    }
                });
            } else {
                System.out.println(classpath + p);
            }
        }
    }

    public boolean matcher(String regex, String content) {
        if (regex == null) {
            return false;
        }

        String pattern = regex.replaceAll("\\.", "\\\\.").replaceAll("\\*", "\\.*");
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(content);
        return matcher.find();
    }

    @Test
    public void test2() {
        //String re = "adc.*";
        String re = "*.*";
        re = re.replaceAll("\\.", "\\\\."); // core
        System.out.println(re);
        re = re.replaceAll("\\*", ".*");  // core
        Pattern pattern = Pattern.compile(re);
        String[] names = new String[]{"adc.pro", "adcd.txt", "abc.txt", "aaa"};
        for (String s : names) {
            Matcher matcher = pattern.matcher(s);
            System.out.println("name:" + pattern + ", file:" + s + "    " + matcher.find());
        }
    }

    @Test
    public void test3() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(null, "haha");
        map.put(null, "123");
        System.out.println(map.get(null));
        for (String s : map.keySet()) {
            System.out.println("key:" + s);
        }
    }

    @Test
    public void test4() {
        Class<?>[] interfaces = ApplicationContextRegister.class.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (anInterface.equals(Comparable.class)) {
                System.out.println(anInterface);
            }
        }
    }

}
