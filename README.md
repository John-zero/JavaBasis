# JavaBasis

Java 基础迭代加深    

***  

JavaSE (Java SE Development Kit, Java 标准开发工具, JavaEE 和 JavaME 的核心)

    开发包: JDK
    
JavaEE (Java Platform, Enterprise Edition, 企业级开发解决方案)

    开发包: JavaEE SDK (Software Development Kit)

JavaME 

    忽略...

***

JDK

    提供了编译, 运行调试 Java 程序需要的各种资源和工具, 比如 javac 编译器, jre 运行时环境, jdb 调试器, Java 类库 等
    (包含 开发, 编译, 执行, 调试 等等)

    JDK 
        Oracle JDK (原 SUN 公司被 Oracle 收购, 非开源协议[JRL协议])

        OpenJDK (JDK 的开源版本, GPL协议[允许商用])

        相关文档:
            知乎 | OpenJDK和SunJDK有啥区别? : https://www.zhihu.com/question/19646618
        
SDK 
    
    基于 JDK 基础扩展的工具包
    
JRE (Java Runtime Environment)  
    
    运行时环境, 比如通过 JVM 将字节码解释成可执行的机器码
    
    由 JVM, Java 运行时类库, 动态链接库 等组成
    
JVM (Java Virtual Machine)
    
    Java 虚拟机 (JDK 默认使用的实现是 Hotspot VM)
    
    JVM 将字节码解释成可执行的机器码, 不同硬件环境和操作系统所产生的机器码是不同的, 所以导致 JVM 在不同平台有不同的实现
    
    JDK 7 体系包括:
        Program Counter Register (程序计数器 [线程私有]) 
            异常: 不会抛出任何 OOM Error
        
        Java Virtual Machine Stacks (Java 虚拟机栈 [线程私有], 服务于 Java [字节码]方法)
            异常: StackOverflowError 和 OutOfMemoryError
            
        Native Method Stack (本地方法栈, 服务于 Native 方法) 
            异常: StackOverflowError 和 OutOfMemoryError
        
            注意: Sun HotSpot 虚拟机直接将本地方法栈和虚拟机栈合二为一.
            
        Java Heap (Java 堆)
            新生代 (From Survivor, To Survivor, Eden) + 老年代
            异常: java.lang.OutOfMemoryError: Java heap space
            
        Method Area (方法区, [永久代], [Non-Heap 非堆]) 
            类信息(名称, 修饰符 等), 静态变量, final 常量, 类 Field 信息, 类 Method 信息, 即时编译器编译后的代码 等
            异常: OutOfMemoryError
        
        Runtime Constant Pool (运行时常量池)
            异常: OutOfMemoryError
            
        Direct Memory (直接内存)    
            不是虚拟机运行时数据区的一部分, 也不是 Java 虚拟机规范中定义的内存区域
            直接内存的分配不受 Java 堆大小的限制, 可以通过 -XX:MaxDirectMemorySize=1024M 指定, 如果不指定则默认与 Java 堆的最大值 (-Xmx) 一样
            异常: OutOfMemoryError
    
    JDK 8 体系包括:
    
        ...
    
        Metaspace (元空间, 本地化堆内存 [Native Heap, Non-Heap 非堆])
            默认是整个系统内存的可用空间
            G1 和 CMS 都会收集该区域 (一般伴随着 Full GC)
            异常: java.lang.OutOfMemoryError: Metaspace space
            
    
    JDK 6 时 String 等常量信息放置于 方法区, JDK 7 时移动到 Java 堆, JDK 8 时 方法区 被 Metaspace 取代.       

***

官网下载: http://www.oracle.com/technetwork/java/javase/downloads/index.html

Windows, Linux 安装: 省略...

    Windows 目录结构:
        C:\Program Files\Java\jdk1.8.0_102
            bin                             (JDK 各种工具命令的可执行文件)
            db                              (Java 实现的 DB 数据库)
            include
            jre                             (运行时环境)
                bin
                lib
            lib                             (Java library)
                tools.jar                       (工具类库)
            ...
            src.zip                         (Java 类库源码)
            ...

Windows, Linux 环境变量配置: 省略...

相关命令

    java -version # 查看当前版本
        java -help # java 命令帮助
        java -XX:+PrintFlagsFinal # 查看所有可设置的参数以及"参数处理"后的默认值
        
        java _Java.class # 执行(运行) .class 字节码文件
        启动运行 (conf 放 配置, libs 放 jar)
            Windows 参考: https://docs.oracle.com/javase/8/docs/technotes/tools/windows/java.html
                java [JVM 参数] -jar xxx.jar com.Main.class
                java [JVM 参数] -classpath .;conf;jars/xxx_0.jar;libs/xxx_1.jar com.Main.class
                java [JVM 参数] -cp .;conf;jars/*;libs/* com.Main.class
                ...
            Linux 参考: https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html
                nohup java [JVM 参数] -jar xxx.jar com.Main.class &  
                nohup java [JVM 参数] -classpath .:conf:jars/xxx_0.jar:libs/xxx_1.jar com.Main.class &  
                nohup java [JVM 参数] -cp .:conf:jars/*:libs/* com.Main.class &  
                ...
        ...
    javac _Java.java # 将 .java 源码文件编译为 .class 字节码文件
        javac -help # javac 命令帮助
            ...
    javap -c _Java.class # 将 .class 字节码文件反汇编为可视化文件
        javap -help # javap 命令帮助
            ...
            
监控工具

    jps
        jps -help # jps 命令帮助
        ... (pid 由此可得) ...
    jstat # 虚拟机统计信息监视工具
        jstat -gcutil pid
        jstat -gccause pid
        ...
    
故障排除工具

    jinfo # Java 配置信息工具
        jinfo -flags pid
        ...
    jmap # Java 内存映像工具
        jmap -heap pid # 查看 heap 堆情况
        jmap -dump:live,format=b,file=/tmp/heap.bin pid # dump
        ...
    jhat # 虚拟机堆转储快照分析工具 (结合 jmap 分析 Java heap 堆)
        jhat -J-Xmx2048m /tmp/heap.bin
        然后访问 http://127.0.0.1:7000 (默认端口为 7000)
    jstack # Java 堆栈分析工具
        jstack -m pid
        ...
    
Windows 图形化整合工具 (一般位于: C:\Program Files\Java\jdk1.8.0_102\bin)

    jconsole # Java 监视与管理控制台
    jvisualvm # 多合一故障处理工具
        (GC 需要安装对应插件 Visual GC)
        
***        

类

    com.java.io._Serializable  # Serializable.java, 序列化/反序列化, 相关联(Externalizable), 延伸其他相关高性能实现方案

    com.java.lang._ClassLoader  # 类加载
    com.java.lang._System  # System.java, 提供 控制台输出, 系统时间, 主动触发 GC, 数组内存拷贝, 环境变量, 系统属性 ... 等
    com.java.lang._String  # String.java, 相关联(char, Character, StringBuffer, StringBuilder)
    com.java.lang._Object  # Object.java, 基础超父类, 提供 对象拷贝, 线程调度, 对象比较, 对象拷贝 ... 等
    com.java.lang._Number  # Number.java, 相关联(Byte, Short, Integer, Long, Float, Double, ...)
    com.java.lang._Thread  # 线程
    com.java.lang._Throwable  # 错误异常
        
    com.java.nio._Nio  # NIO
    
    com.java.util.concurrent._ThreadPoolExecutor  # 线程池
    
***