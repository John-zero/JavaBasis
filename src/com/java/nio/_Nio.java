package com.java.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by John_zero on 2018/1/24.
 *
 * API
 *
 *
 * 核心:
 *  Channel
 *      FileChannel：从文件中读写数据(阻塞)
 *      DatagramChannel：通过UDP读写网络中的数据
 *      SocketChannel：通过TCP读写网络中的数据
 *      ServerSocketChannel：可以监听新进来的TCP连接, 像Web服务器那样. 对每一个新进来的连接都会创建一个SocketChannel
 *  Buffer
 *  Selector
 *
 *  SocketChannel、ServerSocketChannel 和 Selector 的实例初始化都是通过 SelectorProvider (provider()) 类实现的
 *
 */
public class _Nio
{

    public static void main (String [] args)
    {

    }

}

class NioServer
{

    private class HandleClient
    {
        protected FileChannel channel;
        protected ByteBuffer buffer;

        @SuppressWarnings("resource")
        public HandleClient () throws FileNotFoundException
        {
            this.channel = new FileInputStream(FILENAME).getChannel();
            this.buffer = ByteBuffer.allocate(BLOCK);
        }

        public ByteBuffer readBlock ()
        {
            try
            {
                buffer.clear();
                int count = channel.read(buffer);
                buffer.flip();
                if(count <= 0)
                    return null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return buffer;
        }

        public void close()
        {
            try
            {
                channel.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected static final int BLOCK = 4096;
    protected static final String FILENAME = "E:\\AchievementProxy.java";

    protected Selector selector;
    protected ByteBuffer clientBuffer = ByteBuffer.allocate(BLOCK);
    protected ThreadLocal<CharsetDecoder> threadLocal = new ThreadLocal<>();

    public NioServer (int port) throws IOException
    {
        selector = this.getSelector(port);
        Charset charset = Charset.forName("UTF-8");
        threadLocal.set(charset.newDecoder());
    }

    protected Selector getSelector (int port) throws IOException
    {
        // 打开 ServerSocketChannel 用于监听客户端的连接, 它是所有客户端的连接的父管道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 创建多路复用并启动线程
        Selector selector = Selector.open();
        // 绑定监听端口
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        // 设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 将 ServerSocketChannel 注册到 Reactor 线程的多路复用器 Selector 上, 监听 ACCEPT 事件
//		SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        /**
         sel --> Selector
         ops --> SelectionKey.OP_
         att --> 附加属性对象
         acceptorSvr.register(sel, ops, att);
         (等于下面两句代码)
         acceptorSvr.register(sel, ops);
         selectionKey.attach(att);

         监听四种不同类型的事件
         SelectionKey.OP_ACCEPT		接受
         SelectionKey.OP_CONNECT	连接
         SelectionKey.OP_WRITE		写
         SelectionKey.OP_READ		读

         当你对不止一种事件感兴趣的时候, 那么可以用 "位或" 操作符将常量连接起来
         int interest = SelectionKey.OP_CONNECT | SelectionKey.OP_ACCEPT;

         在 SelectionKey 对象中包含了一些比较感兴趣的属性:

             interest 集合是你所选择的感兴趣的事件集合
             int interest = SelectionKey.interestOps();

             ready 集合是通道已经准备就绪的操作集合
             在一次选择 (Selection) 之后, 会首先访问这个 readyset
             int ready = SelectionKey.readyOps();

             Channel  SelectionKey.channel();
             该方法返回的通道需要转成成你要处理的类型 , 如: ServerSocketChannel 或者 SocketChannel 等

             Selector  SelectionKey.selector();

             附加的属性对象	(可有可无)	SelectionKey.attach(att);	// 设置附加
                                        SelectionKey.attachment();	// 获取附加
         */
        return selector;
    }

    public void listen ()
    {
        try
        {
            while(true)
            {
                // 多路复用器在线程 run 方法的无限循环体内轮询准备就绪的Key, 会阻塞直至至少有一个事件发生
                int num = selector.select();
                /**
                 select();				阻塞到至少有一个通道在你注册的事件上就绪了, 返回值int 表示有多少通道已经就绪
                 select(long timeout);	和 select(); 一样, 至少会限制最长阻塞 timeout 毫秒, 超时自动返回
                 selectNow();			不会阻塞, 不管什么通道就绪就立刻返回
                 (这个方法比较特别的是: 如果自从前一次操作后, 没有通道变成可选操作, 则此方法直接返回 0)

                 wakeUp();
                 某个线程调用 select(); 方法后阻塞了, 即使没有通道已经就绪触发返回, 也可以使用该线程调用 wakeUp() 立马返回
                 这是取最近一次的调用, 如果还没有调用, 则下次调用会直接返回
                 */
                if(num == 0)
                    continue;

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                /**
                 一旦调用了 select() 方法, 并且返回值表明有一个或者多个通道就绪了, 然后就可以通过调用
                 selector.selectedKeys(); 访问 "已选择键集 (selected key set)" 中的就绪通道

                 当向 Selector 注册 Channel 时, Channel.register() 方法会返回一个 SelectionKey 对象
                 这个对象代表了注册到该 Selector 的通道

                 Selector 维护3个 key 集合,
                 一个注册过的, 一个是选择过的, 最后是 cancel 过但是未反注册的
                 */
                Iterator<SelectionKey> iter = selectionKeys.iterator();
                while(iter.hasNext())
                {
                    try
                    {
                        SelectionKey selectionKey = iter.next();

                        handleKey(selectionKey);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        /**
                         Selector 不会自己从已选择键集中移除 SelectionKey 实例, 必须在处理完通道时自己移除
                         下次改通道就绪时, Selector 会再次将其放入已选择的键集中
                         */
                        iter.remove();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void handleKey(SelectionKey selectionKey) throws IOException
    {
        if(selectionKey.isAcceptable())
        {
            ServerSocketChannel server_socket_channel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel channel = server_socket_channel.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
//            channel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        }
        else if (selectionKey.isReadable())
        {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            int count = channel.read(clientBuffer);
            if(count > 0)
            {
                clientBuffer.flip();
                CharBuffer charBuffer = threadLocal.get().decode(clientBuffer);

                System.out.println("Client : " + charBuffer.toString());

                SelectionKey wKey = channel.register(selector, SelectionKey.OP_WRITE);
                wKey.attach(new HandleClient());
            }
            else
            {
                channel.close();
                /**
                 close(); 	关闭该 Selector 并且注册到该 Selector 上的所有 SlectionKey 实例无效, 但是通道本身并不会关闭
                 */
            }
            clientBuffer.clear();
        }
        else if (selectionKey.isWritable() && selectionKey.isValid())
        {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            HandleClient client = (HandleClient) selectionKey.attachment();
            ByteBuffer block = client.readBlock();
            if(block != null)
            {
                channel.write(block);
            }
            else
            {
                client.close();
                channel.close();
            }
        }
        else if (selectionKey.isConnectable())
        {

        }
    }

    public static void main(String[] args)
    {
        int port = 9999;
        try
        {
            NioServer server = new NioServer(port);

            System.out.println("NIO Server pore [" + port + "]  启动成功..." );

//			while(true)
//			{
//				server.listen();
//			}
            server.listen();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}


class NioClient
{

    private static final int SIZE = 100;

    private static final InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9999);

    private static final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

    static class Download implements Runnable
    {
        private int index;

        public Download (int index)
        {
            this.index = index;
        }

        @Override
        public void run()
        {
            long start = System.currentTimeMillis();

            try
            {
                // 打开 ServerSocketChannel 用于监听客户端的连接, 它是所有客户端的连接的父管道
                SocketChannel client = SocketChannel.open();

                client.configureBlocking(false);

                Selector selector = Selector.open();

                client.register(selector, SelectionKey.OP_CONNECT);

                client.connect(address);

                ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);

                int total = 0;

                FOR:
                while(true)
                {
                    selector.select();
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectionKeys.iterator();
                    while(iter.hasNext())
                    {
                        SelectionKey selectionKey = iter.next();
                        iter.remove();
                        if(selectionKey.isConnectable())
                        {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            if(channel.isConnectionPending())
                                channel.finishConnect();

                            channel.write(encoder.encode(CharBuffer.wrap("Hello : " + index)));
                            channel.register(selector, SelectionKey.OP_READ);
                        }
                        else if(selectionKey.isReadable())
                        {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            int count = channel.read(buffer);
                            if(count > 0)
                            {
                                total += count;
                                buffer.clear();
                            }
                            else
                            {
                                client.close();
                                break FOR;
                            }
                        }
                    }
                }

                double last = (System.currentTimeMillis() - start) * 1.0 / 1000;

                System.out.println("Thread index[" + (index > 9 ? index + "" : "0" + index)  + "] downloaded [" + total + "] bytes is [" + (total / 1024.0) + "] KB in [" + last + "] s." );

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args)
    {
        ExecutorService exec = Executors.newFixedThreadPool(SIZE);

        for (int index = 0; index < SIZE; index++)
        {
            exec.execute(new Download(index));
        }

        exec.shutdown();
    }

}
