package com.java;

/**
 * Created by John_zero on 2018/1/18.
 */
public class Basis
{

    public static void main (String [] args)
    {
        // ++, --在前, 会先执行运算, 再生成值
        // ++, --在后, 会先生成值, 在执行运算
        int i = 1;
        int j = i++; // j = 1, i = 2 (先赋值, 后自增)
//        int j = ++i; // j = 2, i = 2 (先自增, 后赋值)
        if((i == (++j)) && ((i++) == j))
            i += j;
        System.out.println("i = " + i);
        System.out.println("j = " + j);

        // %
        System.out.println("3 % 3 = " + (3 % 3));
        System.out.println("2 % 3 = " + (2 % 3));
        System.out.println("1 % 3 = " + (1 % 3));
        System.out.println("0 % 3 = " + (0 % 3));

    }

}