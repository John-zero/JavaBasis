package com.java;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by John_zero on 2018/1/12.
 */
public class SystemInfo
{

    public static void main (String [] args)
    {
        // 环境变量
        Map<String, String> envs = System.getenv();

        for(Map.Entry<String, String> entry : envs.entrySet())
        {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        System.out.println("---------------------------------------------");

        // 系统属性
        Properties properties = System.getProperties();
        Set<Object> keySet = properties.keySet();
        for(Object objectKey : keySet)
        {
            Object objectValue = properties.get(objectKey);
            System.out.println(objectKey + " : " + objectValue);
        }
    }

}
