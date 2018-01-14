package com.java.lang;

/**
 * Created by John_zero on 2018/1/15.
 *
 * API:
 *  Number : https://docs.oracle.com/javase/8/docs/api/java/lang/Number.html
 *
 *  Byte : https://docs.oracle.com/javase/8/docs/api/java/lang/Byte.html
 *  Short : https://docs.oracle.com/javase/8/docs/api/java/lang/Short.html
 *  Integer : https://docs.oracle.com/javase/8/docs/api/java/lang/Integer.html
 *  Long : https://docs.oracle.com/javase/8/docs/api/java/lang/Long.html
 *  Float : https://docs.oracle.com/javase/8/docs/api/java/lang/Float.html
 *  Double : https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html
 *
 *  BigInteger : https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html
 *  BigDecimal : https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html
 *
 *  AtomicInteger : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html
 *  AtomicLong : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicLong.html
 *
 *  LongAdder : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/LongAdder.html
 *  DoubleAdder : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/DoubleAdder.html
 *
 * 逻辑类型
 *  boolean, Boolean
 *      true, false (默认值)
 *
 * 文本型
 *  char, Character
 *      A-Z : 65-90
 *      a-z : 97-122
 *
 * 整型 (默认值: 0)
 *  byte, Byte
 *      占用 1 字节, 8 位, 范围 -128~127
 *  short, Short
 *      占用 2 字节, 16 位, 范围 -2ⁿ~2ⁿ-1 (ⁿ = 15), -32768~32767
 *  int (默认), Integer
 *      占用 4 字节, 32 位, 范围 -2ⁿ~2ⁿ-1 (ⁿ = 31), -2147483648~2147483647
 *  long, Long
 *      占用 8 字节, 64 位, 范围 -2ⁿ~2ⁿ-1 (ⁿ = 63), -9223372036854774808~9223372036854774807
 *
 *  十进制整型, 比如: -1, 0, 1, ...
 *  八进制整型, 要求以 0 开头, 比如: 010, 011, ...
 *  十六进制整型, 要求以 0x 或者 0X 开头, 比如: 0x10, 0x11, ...
 *
 * 浮点型 (默认值: 0.0D)
 *  float, Float
 *      占用 4 字节, 32 位, 范围 -3.403E38~3.403E38
 *  double (默认), Double
 *      占用 8 字节, 64 位, 范围 -1.798E308~1.798E308
 *
 *   十进制浮点型, 比如: 3.1415926, ...
 *   科学记数法浮点型, 比如: 3.403E38, 1.798E308, ...
 *
 * Money 金钱
 *   1. 整数倍扩大法, 比如 1.11元 = 111分 (1元=10角=100分)
 *   2. 使用 BigDecimal
 *
 * 相关文章:
 *   比 AtomicLong 还高效的 LongAdder 源码解析 : http://ifeve.com/atomiclong-and-longadder/comment-page-1/
 *
 */
public class _Number
{

    public static void main (String [] args)
    {

    }

}
