package com.java.lang;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by John_zero on 2018/1/12.
 *
 * API
 *  System : https://docs.oracle.com/javase/8/docs/api/java/lang/System.html
 *
 * 使用场景举例:
 *  1. GC 回收
 *      常见: Netty 的 Direct Memory 回收
 *      主动调用 System.gc(); 触发 FULL GC
 *      如果JVM 配置参数 -XX:+DisableExplicitGC 会导致 System.gc(); 调用无效
 *
 *  2. 对于一些关键配置, 比如 生产环境数据库的账号密码, 生产环境的 Redis 账号密码等 可以使用 环境变量或者系统属性
 *      常见: Spring (玩的最溜)
 *          参考: https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config
 *
 *  3. 数组内存拷贝
 *      常见: String, ArrayList, Arrays, CopyOnWriteArrayList ...
 *
 *  4. 时间
 *      System.currentTimeMillis(); // 毫秒级别 (ms)
 *      System.nanoTime(); // 纳秒级别 (ns)
 *
 *      比如:
 *          long startNano = System.nanoTime();
 *          //
 *          // do something ...
 *          //
 *          long endNano = System.nanoTime();
 *          System.out.println("代码执行耗时 : " + (endNano - startNano) + "ns");
 *          System.out.println("代码执行耗时 : " + ((endNano - startNano) / 1000000) + "ms");
 *
 *        建议使用 Spring 的 StopWatch
 *
 *  5. 输出打印
 *      System.out.println();
 *      System.err.println();
 *
 *  6. ...
 *      ...
 *
 */
public final class _System
{

    public static void main (String [] args)
    {
        // 环境变量
        Map<String, String> envs = System.getenv();

        for(Map.Entry<String, String> entry : envs.entrySet())
        {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        System.out.println("------------------------------------------------------------------------------------------");

        // 系统属性
        Properties properties = System.getProperties();
        Set<Object> keySet = properties.keySet();
        for(Object objectKey : keySet)
        {
            Object objectValue = properties.get(objectKey);
            System.out.println(objectKey + " : " + objectValue);
        }

        System.out.println("------------------------------------------------------------------------------------------");


    }

}
