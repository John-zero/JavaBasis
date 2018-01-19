package com.java.util.concurrent;


import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by John_zero on 2018/1/18.
 *
 * API
 *  ExecutorService : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
 *
 *  ThreadPoolExecutor : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
 *
 *  ScheduledExecutorService : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html
 *
 *  定时线程池
 *      ScheduledThreadPoolExecutor : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledThreadPoolExecutor.html
 *
 *  线程池任务队列
 *      ArrayBlockingQueue :
 *          基于数组结构的有界阻塞队列, FIFO (先进先出)
 *      LinkedBlockingQueue :
 *          基于链表结构的无界阻塞队列, FIFO (先进先出)
 *          Executors.newFixedThreadPool(), Executors.newSingleThreadExecutor() 使用该队列
 *      SynchronousQueue :
 *          不存储元素的同步阻塞队列 (每个插入操作必须等到另一个线程调用移除操作, 否则插入操作一直处于阻塞状态)
 *          Executors.newCachedThreadPool() 使用该队列
 *      PriorityBlockingQueue :
 *          具有优先级的无限阻塞队列
 *
 *      吞吐量: (低) ArrayBlockingQueue < LinkedBlockingQueue < SynchronousQueue (高)
 *
 *  线程工厂
 *      ThreadFactory : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadFactory.html
 *
 *  线程池拒绝策略
 *      RejectedExecutionHandler : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RejectedExecutionHandler.html
 *      ThreadPoolExecutor.AbortPolicy : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.AbortPolicy.html
 *          默认策略
 *      ThreadPoolExecutor.CallerRunsPolicy : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.CallerRunsPolicy.html
 *      ThreadPoolExecutor.DiscardOldestPolicy : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.DiscardOldestPolicy.html
 *      ThreadPoolExecutor.DiscardPolicy : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.DiscardPolicy.html
 *
 *  线程池工厂
 *      Executors : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html
 *
 *  Callable : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html
 *
 *  Delayed : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Delayed.html
 *
 *  Future : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html
 *  ScheduledFuture : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledFuture.html
 *  RunnableFuture : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RunnableFuture.html
 *  RunnableScheduledFuture : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RunnableScheduledFuture.html
 *
 *  ...
 *
 *
 *
 *
 *
 */
public class _ThreadPoolExecutor
{

    public static void main (String [] args)
    {
        // 核心线程数
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 20 - 1;
        // 最大线程数
        int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 50 - 1;
        // 空闲线程存活时间
        long keepAliveTime = 60L;
        // 空闲线程存活时间单位
        TimeUnit milliseconds = TimeUnit.SECONDS;
        // 线程任务队列
        BlockingQueue<Runnable> runnableTaskQueue = new LinkedBlockingQueue<>(); // FIFO, 无界阻塞队列
        // 线程工厂
        ThreadFactory threadFactory = new DefaultThreadFactory();
        // 饱和策略
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy(); // 默认, 饱和则抛出异常

        // 线程池创建
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, milliseconds, runnableTaskQueue, threadFactory, handler);
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, handler);

        // 任务提交
        threadPoolExecutor.execute(() -> {});
        Future<?> future = threadPoolExecutor.submit(() -> {});
        scheduledThreadPoolExecutor.execute(() -> {});
        ScheduledFuture<?> scheduledFuture = scheduledThreadPoolExecutor.schedule(() -> {}, 0, TimeUnit.MILLISECONDS);

        if(scheduledFuture.isDone())
        {
            try
            {
                scheduledFuture.get();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // 监控
        threadPoolExecutor.getCorePoolSize();
        threadPoolExecutor.getMaximumPoolSize();
        threadPoolExecutor.getPoolSize();
        threadPoolExecutor.getActiveCount();
        threadPoolExecutor.getCompletedTaskCount();
        threadPoolExecutor.getTaskCount();

        // 关闭
        threadPoolExecutor.shutdown(); // 线程任务分为： 正在执行的线程任务 和 位于线程任务队列中的等待执行线程任务
        try
        {
            while(threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS))
            {

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        List<Runnable> runnables = threadPoolExecutor.shutdownNow(); // 位于线程任务队列中的等待执行线程任务


    }

}

class DefaultThreadFactory implements ThreadFactory
{
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup threadGroup;
    private final String namePrefix;

    DefaultThreadFactory()
    {
        SecurityManager securityManager = System.getSecurityManager();
        threadGroup = (securityManager != null)? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" +  POOL_NUMBER.getAndIncrement() +  "-thread-";
    }

    @Override
    public Thread newThread(Runnable runnable)
    {
        Thread thread = new Thread(threadGroup, runnable,namePrefix + threadNumber.getAndIncrement(),0);

        if (thread.isDaemon())
            thread.setDaemon(false);

        if (thread.getPriority() != Thread.NORM_PRIORITY)
            thread.setPriority(Thread.NORM_PRIORITY);

        return thread;
    }
}