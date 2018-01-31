package com.java.lang;

/**
 * Created by John_zero on 2018/1/17.
 *
 * API
 *  Thread : https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html
 *  ThreadGroup : https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadGroup.html
 *  Thread.State : https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html
 *  Thread.UncaughtExceptionHandler : https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.UncaughtExceptionHandler.html
 *
 *  Runnable : https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html
 *
 *
 * 延伸
 *  进程
 *  线程
 *  协程
 *  管程
 *  纤程
 *
 *  参考:
 *      进程与线程的一个简单解释 (阮一峰, 2013-04-24) : http://www.ruanyifeng.com/blog/2013/04/processes_and_threads.html
 *
 *
 * 线程生命周期状态: (java.lang.Thread.State)
 *  新建 (NEW)
 *  可运行 (RUNNABLE) # A thread executing in the Java virtual machine is in this state.
 *  阻塞 (BLOCKED)
 *  等待 (WAITING)
 *  计时等待 (TIMED_WAITING)
 *  死亡 (TERMINATED)
 *
 *      参考:
 *          Java 线程的状态以及切换 (2016-04-25) : https://segmentfault.com/a/1190000005006079
 *          知乎: 对于OpenJDK而言，是不是每个Java线程都对应一个执行引擎线程？ : https://www.zhihu.com/question/64677339
 *
 * 创建线程方式:
 *  extends Thread
 *  implements java.lang.Runnable 无返回值
 *  implements java.util.concurrent.Callable 有返回值(Future<T>), 异常抛出
 *
 * 线程高成本
 *  线程的创建和销毁成本较高
 *  线程的内存成本较高
 *  线程的上下文切换成功较高
 *
 * 线程内存
 *  -Xss1M # 每个线程默认内存大小, JDK 5+ 后默认是 1M, (128k, 256k, 1M)
 *  过多的线程会导致内存飙升, 比如配置 -Xss1M, 那300个线程就最少占用300M内存了...
 *
 * 线程安全
 *  可以被多个线程同时调用的安全代码叫做线程安全
 *      参考: Thread Safety and Shared Resources : http://tutorials.jenkov.com/java-concurrency/thread-safety.html
 *
 *  线程不安全的原因
 *      乱序执行                    (CPU层)
 *      重排序                      (编译器层)
 *      缓存导致的延迟, 对应原子性    (导致线程不安全大部分都是该原因)
 *
 *  保证线程安全的方法
 *      不要跨域访问共享变量 (域 = 范围)
 *      使共享变量是 final 类型的
 *      将共享变量的操作加上同步 (比如 synchronized, Lock ...)
 *
 * 线程死锁
 *  死锁是线程间互相等待其他线程释放锁资源造成的
 *  解决方法
 *      死锁预防
 *      死锁避免
 *      死锁检测和解除
 *
 * 线程上下文切换 (Context switching)
 *  分为: 让步式上下文切换 和 抢占式上下文切换
 *  上下文切换就是 CPU 给每个线程任务分配 CPU 时间片 (Time Slicing),
 *      在切换到其他线程任务前先要保存上一个线程任务的状态, 当下次该线程任务再次获得 CPU 时间片的时候能够从中断点恢复执行(也就是刚刚保存的状态)
 *  频繁的上下文切换开销会导致系统 load 偏高, CPU sy 使用率偏高 等情况
 *  Linux 系统可以使用 vmstat 1 查看 cs 该列数据
 *  如何减少上下文切换
 *      减少线程数量 (比如尽量使用 线程池 ThreadPoolExecutor 等, 减少使用 Timer 和 减少直接使用 new Thread().start() 等)
 *      CAS 算法 (比如 java.util.concurrent.atomic 包使用 CAS 算法, 而不需要加锁操作)
 *      减少锁竞争, 无锁并发编程
 *
 * 线程中断
 *  Thread.interrupted();
 *
 * 线程合并
 *  Thread.join();
 *
 * 线程优先级
 *  Thread.MAX_PRIORITY;    // 10, 高优先级
 *  Thread.NORM_PRIORITY; // 5, 默认
 *  Thread.MIN_PRIORITY; // 1, 低优先级
 *  范围 1 - 10, 其他值会抛 IllegalArgumentException 异常
 *
 *  new Thread().setPriority(Thread.MIN_PRIORITY); // 设置线程优先级
 *
 * 线程让步
 *  Thread.yield();
 *
 * 守护线程
 *  Thread thread = new Thread();
 *  thread.setDaemon(true); // 设置为守护线程, 默认为 false
 *  thread.start();
 *
 */
public class _Thread
{

    public static void main (String [] args)
    {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler()); // 默认处理跑飞的未捕获的异常

        Thread thread = new Thread(() -> {
            try
            {
                Thread.sleep(10 * 1000); // 10秒
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start(); // 注意: 不是调用 thread.run();

        // 多次/重复 start()
        thread.start();
        thread.start();
        thread.start();

    }

}

class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {

    }

}

