package com.java.lang;

/**
 * Created by John_zero on 2018/1/13.
 *
 * API:
 *  String : https://docs.oracle.com/javase/8/docs/api/java/lang/String.html
 *  StringBuffer : https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuffer.html
 *  StringBuilder : https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
 *
 * 类关系:
 *  public final class String implements java.io.Serializable, Comparable<String>, CharSequence
 *
 *  public abstract class AbstractStringBuilder implements Appendable, CharSequence
 *  public final class StringBuffer extends AbstractStringBuilder implements java.io.Serializable, CharSequence
 *  public final class StringBuilder extends AbstractStringBuilder implements java.io.Serializable, CharSequence
 *
 * 间接关系:
 *  char 字符, 基本类型
 *  Character 是 char 的包装类
 *
 *  String 字符串
 *
 * 一个中文占字节数(编码长度):
 *  不同编码格式占用字节数不一样, 并且 UTF-8 编码格式下的中文所占字节数也不一样
 *  部分编码格式:
 *      ASCII
 *      ISO-8859-1
 *      GB2312
 *      GBK
 *      UTF-8
 *      UTF-16
 *
 * 对比:
 *                  底层容器                 是否可变     是否线程安全            性能
 *  String          final char value[];     不可变                             视情况而定
 *  StringBuffer    char[] value;           可变        是 (synchronized)      低
 *  StringBuilder   char[] value;           可变        否                     视情况而定
 *
 * 编译优化:
 *  1.
 *    编译优化前:
 *      String _String = "";
 *      for(int i = 0; i < 10; i++)
 *          _String = _String + i + ", ";
 *    编译优化后:
 *      StringBuilder _String = new StringBuilder();
 *      for(int i = 0; i < 10; i++)
 *          _String.append(i).append(", ");
 *
 *  2.
 *    ...
 *
 * 常量池:
 *  内存分配:
 *    HotSpot 1.6 常量池 在 永久代 上分配内存
 *    HotSpot 1.7 常量池 在 Heap 堆 上分配内存
 *
 * GC 垃圾回收:
 *  ...
 *
 * 字节码指令:
 *  编写的是 .java 源代码文件, 由 jdk1.8.0_102\bin\javac (Java 语言编译器) 编译成 .class 字节码文件
 *  编译后的 .class 字节码文件, 是无法直接查看的, 需要使用 jdk1.8.0_102\bin\javap (Java Class 文件反编译工具) 的执行 javap -c 类名.class 命令可视化字节码文件
 *
 * 相关文章:
 *  深入分析String.intern和String常量的实现原理 (2016-12-08) : https://www.jianshu.com/p/c14364f72b7e
 *  JVM源码分析之String.intern()导致的YGC不断变长 (2016-11-06) : http://lovestblog.cn/blog/2016/11/06/string-intern/
 *  StringBuilder 在高性能场景下的正确用法 (2015-12-07) : http://calvin1978.blogcn.com/articles/stringbuilder.html
 *
 */
public final class _String
{

    public static void main (String [] args)
    {
        String _String = "String"; // 常量池


        try
        {
            String _Chinese_language = "壹";
            System.out.println("ASCII       编码长度: " + _Chinese_language.getBytes("ASCII").length);
            System.out.println("ISO-8859-1  编码长度: " + _Chinese_language.getBytes("ISO-8859-1").length);
            System.out.println("GB2312      编码长度: " + _Chinese_language.getBytes("GB2312").length);
            System.out.println("GBK         编码长度: " + _Chinese_language.getBytes("GBK").length);
            System.out.println("UTF-8       编码长度: " + _Chinese_language.getBytes("UTF-8").length);
            System.out.println("UTF-16      编码长度: " + _Chinese_language.getBytes("UTF-16").length);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        System.out.println("------------------------------------------------------------------------------------------");


        StringBuffer _StringBuffer = new StringBuffer("StringBuffer");
        String _before_intern = _StringBuffer.toString(); // intern() 之前 = false
        String _after_intern = _before_intern.intern(); // intern() 之后 = true

        System.out.println("intern() 之前: " + (_before_intern == "StringBuffer")); // = false
        System.out.println("intern() 之后: " + (_after_intern == "StringBuffer")); // = true


        System.out.println("------------------------------------------------------------------------------------------");


        StringBuilder _StringBuilder = new StringBuilder("StringBuilder");


    }

}
