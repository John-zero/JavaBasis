package com.java.lang;

/**
 * Created by John_zero on 2018/1/14.
 *
 * 在 Java 中所有的 Class 都默认直接或者间接继承 Object
 *
 * API:
 *  https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html
 *
 *
 * synchronized
 *  对应的字节码是 monitorenter 和 monitorexit
 *
 * ObjectWaiter
 *  双向链表结构对象, 每一个等待锁的线程都会被封装成 ObjectWaiter 对象
 *  存放 Thread (线程对象), ParkEvent, 当前状态 TStates(枚举) 等数据
 *
 * ObjectMonitor
 *  Entry Set 队列 和 Wait Set 队列
 *      这两个队列中存放的是 ObjectWaiter 对象
 *    Entry Set: 处于等待获取锁的线程的对象所处队列
 *    Wait Set : 处于 wait 状态 的线程对象所处队列
 *
 *  ObjectMonitor.wait();
 *      释放当前锁, 线程挂起进入 WAITING 状态, 将生成 ObjectWaiter 对象添加到 Wait Set 队列等待唤醒
 *  ObjectMonitor.notify(TRAPS);
 *      唤醒 Wait Set 列表中的 第一个 ObjectWaiter 节点 (虽然 JDK 的 notify() 的注释是随机唤醒一个线程)
 *  ObjectMonitor.notifyAll(TRAPS);
 *      遍历将 Wait Set 列表中的所有 ObjectWaiter 节点根据不同策略加入到 Entry Set 或者进行自旋操作
 *
 *    注意: notify() 和 notifyAll() 不会释放所占有的 ObjectMonitor 对象, 真正释放 ObjectMonitor 对象的时间点是在执行 monitorexit 指令
 *      一旦释放 ObjectMonitor 对象, 那么 Entry Set 中的 ObjectWaiter 节点所保存的线程就可以公平竞争 ObjectMonitor 对象进行加锁操作了
 *
 *
 * 函数套件:
 *  hashCode() equals()
 *
 *  clone()
 *    拷贝分类:
 *      浅拷贝
 *        浅拷贝仅仅是复制了这个对象本身的成员变量, 该对象如果引用了其他对象的话, 也不对其复制
 *      深拷贝
 *        深拷贝会复制这个对象和它所引用的对象的成员变量. 如果该对象引用了其他对象, 深拷贝也会对其复制
 *        实现方式:
 *          1. 实现 java.io.Serializable 接口, 序列化对象后再反序列化就可以得到深拷贝对象
 *          2. ...
 *
 *    clone() 属于浅拷贝
 *    必须实现 java.lang.Cloneable 接口的类才能调用 clone() 函数进行拷贝, 否则会抛 CloneNotSupportedException 异常
 *
 *  toString()
 *
 *  wait() notify() notifyAll()
 *    配合 synchronized 一起使用
 *
 *    属于 阻塞锁 (阻塞锁的优势在于阻塞锁不会占用CPU的时间, 不会导致CPU占用率过高, 但是进入时以及恢复时间都要比自旋锁略慢)
 *
 *    wait()
 *      ...
 *    notify()
 *      ...
 *    notifyAll()
 *      ...
 *
        示例:
         synchronized (obj)
         {
             try
             {
                // do something ...

                while (<condition does not hold>)
                    obj.wait(timeout); // obj.wait();

                // do something ...
             }
             catch (InterruptedException e)
             {

             }
         }

         synchronized (obj)
         {
             // do something ...

             obj.notifyAll(); // obj.notify();

             // do something ...
         }

 *    延伸
 *      Object.wait() 和 Thread.sleep()
 *        当前线程调用对象 Object.wait() 会释放锁, 然后进入 Wait Set 队列, 依靠 Object.notify()/Object.notifyAll() 或者 Object.wait(timeout) 自动唤醒
 *        当前线程调用 Thread.sleep() 不会释放锁 (睡着了也抱着锁对象), 强制当前线程进入阻塞状态, 睡眠到期当前线程自动苏醒进入可运行状态
 *
 *  finalize()
 *
 *    Java将弃用finalize()方法？(2017-03-30) : http://www.infoq.com/cn/news/2017/03/Java-Finalize-Deprecated
 *
 *
 *  相关文章:
 *    干货 | 深入分析Object.wait/notify实现机制 (2016-11-22 携程技术中心) :
 *      https://mp.weixin.qq.com/s?__biz=MjM5MDI3MjA5MQ==&mid=2697265637&idx=3&sn=62d7f1377af8835aba183e6f54ae3936
 *    知乎搜索 Java wait 等:
 *      https://www.zhihu.com/search?type=content&q=java%20wait
 *      https://www.zhihu.com/search?type=content&q=java%20notify
 *
 */
public class _Object
{



}
