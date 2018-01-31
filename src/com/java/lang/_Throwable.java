package com.java.lang;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

/**
 * Created by John_zero on 2018/1/24.
 *
 * API
 *  Throwable : https://docs.oracle.com/javase/8/docs/api/java/lang/Throwable.html
 *
 *      Error : https://docs.oracle.com/javase/8/docs/api/java/lang/Error.html
 *
 *      Exception : https://docs.oracle.com/javase/8/docs/api/java/lang/Exception.html
 *          RuntimeException : https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html
 *          ...
 *
 * 强制要求 JVM 始终抛出含堆栈的异常 (JDK 5 引入的新特性, 对频繁抛出的异常, JDK 会对其做性能优化, JIT 重新编译后会抛出没有堆栈的异常 [server 模式下是默认开启])
 *   -XX:-OmitStackTraceInFastThrow # 注意是 -号 (+号是启用)
 *
    java.lang.Throwable
        Error # 程序在运行期间出现十分严重不可恢复的错误, 在这种情况下应用程序只能中止运行.
            比如:
                abstract VirtualMachineError
                 StackOverflowError
                 OutOfMemoryError
                ...
            在程序中不用捕获 Error 类型的异常, 并且在一般情况下程序中也不应该抛出 Error 类型的异常.

        Exception #
            该异常如果没有被应用程序捕获, 那最终都由 JVM 来进行才处理
            所以会出现以下两种结果:
                1. 当前线程会停止运行 (导致异常触发点后面的代码将得不到运行)
                2. 异常栈信息通过标准错误流输出
            RuntimeException # 运行时异常
                是一种 Unchecked Exception, 即表示编译器不会检查程序是否对 RuntimeException 做了处理
                常见的 RuntimeException 有:
                 NullPointerException
                 IllegalArgumentException
                 ...

            Checked Exception # 受检异常 (强制调用方必须捕获异常或者继续上抛)
                常见的 Checked Exception 有:
                 IOException
                 ClassNotFoundException
                 ...

        StackRecorder

 *
 * 如果你是开发底层核心组件, 那么应该将异常尽可能详细化!!!
 *
 */
public class _Throwable
{

    public static void main (String [] args)
    {
        /**
         * 强制捕获 Exception
         */

        try
        {
            throwException ();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("------------------------------------------------------------------------------------------");

        /**
         * 强制捕获 Throwable
         */

        try
        {
            throwThrowable ();
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }

        System.out.println("------------------------------------------------------------------------------------------");

        /**
         * 线程内异常
         */

        Executors.callable(() -> {

            // 第一种情况：没有捕获异常
            System.out.println("线程池: 0");
            System.out.println("线程池: 3 / 0 = " + (3 / 0));
            System.out.println("线程池: 1");

            // 第二种情况: 捕获异常
            try
            {
                System.out.println("线程池: before try");
                System.out.println("线程池: 3 / 0 = " + (3 / 0));
                System.out.println("线程池: after try");
            }
            catch (ArithmeticException e)
            {
                System.out.println("线程池: catch");
                e.printStackTrace();
            }
            finally
            {
                System.out.println("线程池: finally");
                System.out.println("3 / 0 = ?, finally: Exception in thread \"main\" java.lang.ArithmeticException: / by zero");
            }
        });

        System.out.println("------------------------------------------------------------------------------------------");

        /**
         * Timer 定时器
         */

        Timer timer  = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 不捕获异常, 则会导致该定时器会中止运行
                System.out.println("Timer 定时器: 3 / 0 = " + (3 / 0));

                // 捕获异常, 将异常处理掉, 不会导致该定时器会中止运行
                try
                {
                    System.out.println("Timer 定时器: 3 / 0 = " + (3 / 0));
                }
                catch (ArithmeticException e)
                {
//                    e.printStackTrace();
                }
            }
        }, 3000);

        System.out.println("------------------------------------------------------------------------------------------");

        /**
         * 异常顺序兼容性
         */

        arithmeticException ();

        System.out.println("------------------------------------------------------------------------------------------");

        /**
         * 异常捕获后, finally 块依然会执行
         */

        try
        {
            System.out.println(3 / 0);
        }
        catch (ArithmeticException e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println("3 / 0 = ?, finally: Exception in thread \"main\" java.lang.ArithmeticException: / by zero");
        }

        System.out.println("------------------------------------------------------------------------------------------");

        /**
         * 异常轨迹
         */

        try
        {
            throwableTrajectory_0 ();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("------------------------------------------------------------------------------------------");
    }

    protected static void throwException () throws RuntimeException, Exception // Checked Exception, 编译期间就会提示强制调用方必须捕获异常或者继续上抛
    {
//        throw new CustomizeRuntimeException();

        throw new CustomizeRuntimeException("Checked Exception");

//        throw new RuntimeException("运行时异常");

//        throw new Exception("异常");

//        throw new Error("错误");
    }

    /**
     * Checked Exception
     * @throws Throwable
     */
    protected static void throwThrowable () throws Throwable
    {
        throw new Throwable();
    }

    /**
     * 异常顺序兼容性
     */
    protected static void arithmeticException ()
    {
        try
        {
            System.out.println(3 / 0); // Exception in thread "main" java.lang.ArithmeticException: / by zero
        }
        catch (ArithmeticException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /**
         * JDK 7 + 多异常处理方式
         */
        try
        {
            System.out.println(3 / 0);
        }
//        catch (ArithmeticException | IllegalArgumentException | Exception e) // 这里无法编译通过是因为 ArithmeticException 和 IllegalArgumentException 都是 Exception 的子类
        catch (ArithmeticException | IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 异常轨迹
     * @throws Exception
     */
    protected static void throwableTrajectory_0 () throws Exception
    {
        try
        {
            throwableTrajectory_1 ();
        }
        catch (Exception e)
        {
            e.printStackTrace();

//            throw e; //

            throw (Exception) e.fillInStackTrace();
        }
    }

    /**
     * 异常轨迹
     * @throws Exception
     */
    private static void throwableTrajectory_1 () throws Exception
    {
        throw new CustomizeRuntimeException("测试异常轨迹");
    }

}

/**
 * 自定义异常
 */
class CustomizeRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 5162710183389028792L;

    public CustomizeRuntimeException()
    {
        super();
    }

    public CustomizeRuntimeException(String s)
    {
        super(s);
    }

    /**
     * 收集线程整个异常栈信息
     * @return
     */
    @Override
    public synchronized Throwable fillInStackTrace()
    {
        return super.fillInStackTrace();

//        return this; // 覆盖, 只返回当前对象, 减少对异常栈信息的收集, 可以提高性能, 但是对后续的异常排错定位不精确
    }

}
