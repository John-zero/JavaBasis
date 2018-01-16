package com.jvm;

/**
 * Created by John_zero on 2018/1/16.
 *
 * 类各组件的初始化顺序
 */
public class InitializationOrder
{

    public static void main(String[] args)
    {
        new Child().printX();
    }

}

class Father
{
    static
    {
        System.out.println("Father --> static {}");
    }

    public static int age = 10;
    public int x = 100;

    {
        System.out.println("Father --> {}");
    }

    public Father ()
    {
        System.out.println("Father's x = " + x);

        age();
    }

    public void age()
    {
        System.out.println("Father's age");
    }
}

class Child extends Father
{
    static
    {
        System.out.println("Child --> static {}");
    }

    private int age = 20;
    public int x = 200;

    {
        age = 30;
        System.out.println("Child --> {}");
    }

    public Child ()
    {
        this("Child other constructor");
        System.out.println("Child constructor body : age = " + age);
    }

    public Child(String s)
    {
        System.out.println("Child --> this : " + s);
    }

    @Override
    public void age()
    {
        System.out.println("Child age = " + age);
    }

    public void printX ()
    {
        System.out.println("Child x = " + x);
    }
}