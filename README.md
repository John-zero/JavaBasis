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

    提供了编译, 运行 Java 程序需要的各种资源和工具, 比如 javac 编译器, jre 运行时环境, Java 类库 等

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
    
    JDK 6 体系包括: 
    
    JDK 7 体系包括:
    
    JDK 8 体系包括:

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
            ...
    javac _Java.java # 将 .java 源码文件编译为 .class 字节码文件
        javac -help # javac 命令帮助
            ...
    java _Java.class # 执行(运行) .class 字节码文件
    javap -c _Java.class # 将 .class 字节码文件反汇编为可视化文件
        javap -help # javap 命令帮助
            ...
            
监控工具

    jps
        jps -help # jps 命令帮助
        ... (pid 由此可得) ...
    jstat
        jstat -gcutil pid
        jstat -gccause pid
        ...
    
故障排除工具

    jinfo
        jinfo -flags pid
        ...
    jmap
        jmap -heap pid # 查看 heap 堆情况
        jmap -dump:live,format=b,file=/tmp/heap.bin pid # dump
        ...
    jhat (结合 jmap 分析 Java heap 堆)
        jhat -J-Xmx2048m /tmp/heap.bin
        然后访问 http://127.0.0.1:7000 (默认端口为 7000)
    jstack
        jstack -m pid
        ...
    
Windows 图形化整合工具 (一般位于: C:\Program Files\Java\jdk1.8.0_102\bin)

    jconsole
    jmc
    jvisualvm 
        (GC 需要安装对应的 插件)
        
***        

类
    com.java.lang._System  # System.java, 提供 控制台输出, 系统时间, 主动触发 GC, 数组内存拷贝, 环境变量, 系统属性 ... 等
    com.java.lang._String  # String.java, 相关联(char, Character, StringBuffer, StringBuilder)
    com.java.lang._Object  # Object.java, 基础超父类, 提供 对象拷贝, 线程调度, 对象比较, 对象拷贝 ... 等
    com.java.lang._Number  # Number.java, 相关联(Byte, Short, Integer, Long, Float, Double, ...)
        
***