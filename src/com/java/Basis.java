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

        // 需求: 奇数在前, 偶数在后 (O(1))
        int [] sequences = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        sequences(sequences); // 运算
        for (int k = 0; k < sequences.length; k++)
        {
            System.out.print(sequences[k] + ", ");

            if((k + 1) == sequences.length)
                System.out.println();
        }

        // 判断一个数是否是2的幂次方
        System.out.println("isPowerOfTwo: " + isPowerOfTwo(2));
        System.out.println("isPowerOfTwo: " + isPowerOfTwo(3));
    }

    protected static void sequences (int [] sequences)
    {
        // https://www.cnblogs.com/xing901022/p/3755795.html
    }

    private static boolean isPowerOfTwo(int val)
    {
        return (val & -val) == val;
    }

}