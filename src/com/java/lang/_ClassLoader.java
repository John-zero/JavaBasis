package com.java.lang;

import java.net.URL;

/**
 * Created by John_zero on 2018/1/23.
 *
 * API:
 *  ClassLoader : https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html
 *
 * Class 类的加载/卸载
 *  -XX:-TraceClassLoading
 *  -XX:-TraceClassUnloading
 *
 *  Class.forName("");
 *      静态方法, 动态加载类文件, 是要求JVM查找并加载指定的类, forName 只是加载类, 并不执行任何代码
 *
 *  注意:
 *      Class 一般是延迟加载的
 *      使用自定义类加载器, 强行用 defineClass() 加载一个以 "java.lang" 开头的类将会得到虚拟机抛出的 "java.lang.SecurityException: Prohibited package name: java.lang" 的异常
 *
 *
 * 相关文章:
 *  知乎
 *      关于class loader的一点疑惑？ : https://www.zhihu.com/question/29996850#answer-14369353
 *
 */
public class _ClassLoader
{

    public static void main (String [] args)
    {
        java.lang.String _String = new java.lang.String("自定义 java.lang.String 相同包路径的类");

//        java.util.Date _Date = new java.util.Date();
//        ClassLoader _classLoader = java.lang.String.class.getClassLoader();
        ClassLoader _classLoader = _String.class.getClassLoader();
        while(_classLoader != null)
        {
            System.out.println(_classLoader);
            _classLoader = _classLoader.getParent();
        }
        System.out.println(_classLoader);


        System.out.println("------------------------------------------------------------------------------------------");


        URL [] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        for (int i = 0; i < urls.length; i++)
        {
            System.out.println(urls[i].toExternalForm());
        }


        System.out.println("------------------------------------------------------------------------------------------");


        System.out.println(System.getProperty("sun.boot.class.path"));


        System.out.println("------------------------------------------------------------------------------------------");


        try
        {
            Class.forName("");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }


        System.out.println("------------------------------------------------------------------------------------------");


        ClassLoader classLoader = _ClassLoader.class.getClassLoader();
        while(classLoader != null)
        {
            System.out.println(classLoader);
            classLoader = classLoader.getParent();
        }
        System.out.println(classLoader);


        System.out.println("------------------------------------------------------------------------------------------");


        ScriptClassLoader scriptClassLoader = new ScriptClassLoader();


        System.out.println("------------------------------------------------------------------------------------------");

    }

}

class ScriptClassLoader extends ClassLoader
{

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        return super.findClass(name);
    }

    /**
     * loadClass() 最终也会调用 findClass()
     *
     * 不推荐覆盖 loadClass()
     *
     * 实现双亲委派模型
     *
     * 当然, 除非你要刻意破坏双亲委派模型...
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        return super.loadClass(name, resolve);
    }

}
