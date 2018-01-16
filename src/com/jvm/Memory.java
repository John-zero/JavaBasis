package com.jvm;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John_zero on 2018/1/16.
 *
 * -Xmx2048M # JVM 最大可用内存 1024M = 1G, 2048M = 2G, 3072M = 3G, 4096M = 4G
 * -Xms1024M # JVM 最小可用内存
 *
 * -Xss1M # 每个线程默认内存大小, JDM 5+ 后默认是 1M, (128k, 256k, 1M)
 *
 * Survivor, Eden, 年老代 设置
 *  1. 直接设置具体内存大小值
 *      # 新生代
 *          -Xmn819M # 设置新生代大小, 等同于设置 -XX:NewSize=819M -XX:MaxNewSize=819M
 *          或者
 *          -XX:NewSize=512M # 设置新生代初始内存
 *          -XX:MaxNewSize=819M # 设置新生代最大内存
 *
 *      # 年老代
 *          # 不需要配置 (-Xmx - -Xmn = 年老代内存大小)
 *
 *  2. 按比例分配内存大小值
 *      -XX:NewRatio=2 # 设置新生代(From Survivor, To Survivor, Eden)与年老代的大小比例, 默认是 2 (新生代 : 年老代 = 1 : 2)
 *      -XX:SurvivorRatio=8 # 设置新生代中 Eden 区与 两个 Survivor 区的大小比例, 默认是 8 (From Survivor : To Survivor : Eden = 1 : 1 : 8)
 *
 * 永久代/Metaspace
 *  # 永久代 (PermGen, JDK 8 开始没有该区域)
 *      -XX:PermSize=64M # 设置永久代初始内存, 默认是物理内存的 1/64
 *      -XX:MaxPermSize=256M # 设置永久代最大内存, 默认是物理内存的 1/4
 *  # Metaspace (JDK 8 开始取代 PermGen)
 *      -XX:MetaspaceSize=64M # 默认动态调整
 *      -XX:MaxMetaspaceSize=256M
 *
 *      相关文档:
 *          Java 8: From PermGen to Metaspace : https://dzone.com/articles/java-8-permgen-metaspace
 *          JVM源码分析之 Metaspace 解密 (你假笨, 2016-11-03): https://www.jianshu.com/p/92a5fbb33764
 *
 * 直接内存 (堆外内存)
 *  -XX:MaxDirectMemorySize=1024M # 如果不指定则默认与 Java 堆的最大值 (-Xmx) 一样
 *      使用 NIO, Netty, Mina 等远程交互较多则建议设置该值, 与此同时不建议设置 -XX:+DisableExplicitGC
 *          参考: 如何比较准确地估算一个Java进程到底申请了多大的Direct Memory？: https://www.zhihu.com/question/55033583
 *
 * -XX:MaxTenuringThreshold=15 # 晋升年龄, 默认是 15
 *
 * -XX:ThreadStackSize=1024 # 线程栈大小, 默认 0 则为系统默认值 (Windows 10, i5, 8G, 64bit = 1024)
 *
 * 强制要求 JVM 始终抛出含堆栈的异常
 *   -XX:-OmitStackTraceInFastThrow # 注意是 -号 (+号是启用)
 *
 * 内存溢出
 *  -XX:+HeapDumpOnOutOfMemoryError
 *  -XX:HeapDumpPath=E:/dump/jvm_heap_error.dump
 *      # 注意这个文件路径必须提前创建, 如果不存在该路径会提示: Unable to create E:/dump/jvm_heap_error.dump: No such file or directory
 *
 * Class 类的加载/卸载
 *  -XX:-TraceClassLoading
 *  -XX:-TraceClassUnloading
 *
 * GC 垃圾收集器
 *  吞吐量优先垃圾收集器: -XX:+UseParallelGC -XX:+UseParallelOldGC
 *  并发低停顿垃圾收集器: -XX:+UseConcMarkSweepGC (新生代 ParNew [-XX:+UseParNewGC] + 老年代 CMS + 备用 Serial Old 的收集器)
 *  并发与并行的垃圾收集器: -XX:+UseG1GC
 *
 *  串行垃圾收集器: Serial (复制算法), Serial Old (MSC) (标记-整理算法)
 *  并行垃圾收集器: ParNew (复制算法), Parallel Scavenge (复制算法), Parallel Old (标记-整理算法)
 *  并发标记扫描垃圾收集器: Concurrent Mark Sweep (CMS) (标记-清除算法)
 *  G1 垃圾收集器: Garbage First G1 (分代收集算法)
 *
 *  JDK 6 默认:
 *      ...
 *  JDK 7 默认:
 *      ...
 *  JDK 8 默认:
 *      -XX:+UseParallelGC
 *      -XX:+UseParallelOldGC
 *  JDK 9 默认:
 *      -XX:+UseG1GC
 *
 *  垃圾收集器                   新生代                 老年代                 备用
 *  -XX:+UseSerialGC            Serial                Serial Old (MSC)
 *  -XX:+UseParNewGC            ParNew                Serial Old (MSC)
 *  -XX:+UseParallelGC          Parallel Scavenge     Serial Old (MSC)
 *  -XX:+UseParallelOldGC       Parallel Scavenge     Parallel Old
 *  -XX:+UseConcMarkSweepGC     ParNew                CMS                   Serial Old (MSC)
 *  -XX:+UseG1GC
 *
 *  参数:
 *      ...
 *
 * GC 日志
 *  -XX:+PrintGCDetails # 输出 GC 详细日志
 *  -XX:+PrintGCTimeStamps # 输出 GC 的基准时间
 *  -XX:+PrintGCDateStamps # 输出 GC 的日期时间
 *  -XX:+PrintHeapAtGC # 输出 GC 前后输出堆的信息
 *  -Xloggc:E:/log/gc.log # GC 日志文件的输出路径
 *
 */
public class Memory
{

//    byte [] _1_KB = new byte[1024]; // 1KB

//    byte [] _0_5_MB = new byte[1024 * 512]; // 0.5MB

//    byte [] _1_MB = new byte[1024 * 1024]; // 1MB

    public static void main (String [] args)
    {

        defaultMemoryOOM ();

    }

    /**
     * 默认内存
     *  不配置任何 JVM 参数
     *
     * 第一版测试
     *  -Xmx3072M -Xms1024M -Xss1M
     *  -XX:-OmitStackTraceInFastThrow
     *  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=E:/dump/jvm_heap_error.dump
     *  -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -Xloggc:E:/log/gc.log
     *
     * ...
     *
     */
    protected static void defaultMemoryOOM ()
    {
        List<byte []> bytes = new ArrayList<>();

        for (int i = 1; i <= 10 * 1024; i++)
        {
            byte [] _0_5_MB = new byte[1024 * 512]; // 0.5MB

            bytes.add(_0_5_MB);

            if(i % 512 == 0)
            {
                try
                {
                    Thread.sleep(10 * 1000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 直接内存, 堆外内存
     *
     * -Xmx20M -XX:MaxDirectMemorySize=10M
     */
    protected static void directMemoryOOM ()
    {
        try
        {
            Field unsafeField = Unsafe.class.getDeclaredFields()[0];
            unsafeField.setAccessible(true);

            Unsafe unsafe = (Unsafe) unsafeField.get(null);

            while (true)
            {
                unsafe.allocateMemory(1024 * 1024); // 1MB
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}

/**
 JDK Version = 1.8.0_152

 执行 java -XX:+PrintFlagsFinal 命令

[Global flags]
    uintx AdaptiveSizeDecrementScaleFactor          = 4                                   {product}
    uintx AdaptiveSizeMajorGCDecayTimeScale         = 10                                  {product}
    uintx AdaptiveSizePausePolicy                   = 0                                   {product}
    uintx AdaptiveSizePolicyCollectionCostMargin    = 50                                  {product}
    uintx AdaptiveSizePolicyInitializingSteps       = 20                                  {product}
    uintx AdaptiveSizePolicyOutputInterval          = 0                                   {product}
    uintx AdaptiveSizePolicyWeight                  = 10                                  {product}
    uintx AdaptiveSizeThroughPutPolicy              = 0                                   {product}
    uintx AdaptiveTimeWeight                        = 25                                  {product}
    bool AdjustConcurrency                         = false                               {product}
    bool AggressiveOpts                            = false                               {product}
    intx AliasLevel                                = 3                                   {C2 product}
    bool AlignVector                               = false                               {C2 product}
    intx AllocateInstancePrefetchLines             = 1                                   {product}
    intx AllocatePrefetchDistance                  = 192                                 {product}
    intx AllocatePrefetchInstr                     = 3                                   {product}
    intx AllocatePrefetchLines                     = 4                                   {product}
    intx AllocatePrefetchStepSize                  = 64                                  {product}
    intx AllocatePrefetchStyle                     = 1                                   {product}
    bool AllowJNIEnvProxy                          = false                               {product}
    bool AllowNonVirtualCalls                      = false                               {product}
    bool AllowParallelDefineClass                  = false                               {product}
    bool AllowUserSignalHandlers                   = false                               {product}
    bool AlwaysActAsServerClassMachine             = false                               {product}
    bool AlwaysCompileLoopMethods                  = false                               {product}
    bool AlwaysLockClassLoader                     = false                               {product}
    bool AlwaysPreTouch                            = false                               {product}
    bool AlwaysRestoreFPU                          = false                               {product}
    bool AlwaysTenure                              = false                               {product}
    bool AssertOnSuspendWaitFailure                = false                               {product}
    bool AssumeMP                                  = false                               {product}
    intx AutoBoxCacheMax                           = 128                                 {C2 product}
    uintx AutoGCSelectPauseMillis                   = 5000                                {product}
    intx BCEATraceLevel                            = 0                                   {product}
    intx BackEdgeThreshold                         = 100000                              {pd product}
    bool BackgroundCompilation                     = true                                {pd product}
    uintx BaseFootPrintEstimate                     = 268435456                           {product}
    intx BiasedLockingBulkRebiasThreshold          = 20                                  {product}
    intx BiasedLockingBulkRevokeThreshold          = 40                                  {product}
    intx BiasedLockingDecayTime                    = 25000                               {product}
    intx BiasedLockingStartupDelay                 = 4000                                {product}
    bool BindGCTaskThreadsToCPUs                   = false                               {product}
    bool BlockLayoutByFrequency                    = true                                {C2 product}
    intx BlockLayoutMinDiamondPercentage           = 20                                  {C2 product}
    bool BlockLayoutRotateLoops                    = true                                {C2 product}
    bool BranchOnRegister                          = false                               {C2 product}
    bool BytecodeVerificationLocal                 = false                               {product}
    bool BytecodeVerificationRemote                = true                                {product}
    bool C1OptimizeVirtualCallProfiling            = true                                {C1 product}
    bool C1ProfileBranches                         = true                                {C1 product}
    bool C1ProfileCalls                            = true                                {C1 product}
    bool C1ProfileCheckcasts                       = true                                {C1 product}
    bool C1ProfileInlinedCalls                     = true                                {C1 product}
    bool C1ProfileVirtualCalls                     = true                                {C1 product}
    bool C1UpdateMethodData                        = true                                {C1 product}
    intx CICompilerCount                          := 3                                   {product}
    bool CICompilerCountPerCPU                     = true                                {product}
    bool CITime                                    = false                               {product}
    bool CMSAbortSemantics                         = false                               {product}
    uintx CMSAbortablePrecleanMinWorkPerIteration   = 100                                 {product}
    intx CMSAbortablePrecleanWaitMillis            = 100                                 {manageable}
    uintx CMSBitMapYieldQuantum                     = 10485760                            {product}
    uintx CMSBootstrapOccupancy                     = 50                                  {product}
    bool CMSClassUnloadingEnabled                  = true                                {product}
    uintx CMSClassUnloadingMaxInterval              = 0                                   {product}
    bool CMSCleanOnEnter                           = true                                {product}
    bool CMSCompactWhenClearAllSoftRefs            = true                                {product}
    uintx CMSConcMarkMultiple                       = 32                                  {product}
    bool CMSConcurrentMTEnabled                    = true                                {product}
    uintx CMSCoordinatorYieldSleepCount             = 10                                  {product}
    bool CMSDumpAtPromotionFailure                 = false                               {product}
    bool CMSEdenChunksRecordAlways                 = true                                {product}
    uintx CMSExpAvgFactor                           = 50                                  {product}
    bool CMSExtrapolateSweep                       = false                               {product}
    uintx CMSFullGCsBeforeCompaction                = 0                                   {product}
    uintx CMSIncrementalDutyCycle                   = 10                                  {product}
    uintx CMSIncrementalDutyCycleMin                = 0                                   {product}
    bool CMSIncrementalMode                        = false                               {product}
    uintx CMSIncrementalOffset                      = 0                                   {product}
    bool CMSIncrementalPacing                      = true                                {product}
    uintx CMSIncrementalSafetyFactor                = 10                                  {product}
    uintx CMSIndexedFreeListReplenish               = 4                                   {product}
    intx CMSInitiatingOccupancyFraction            = -1                                  {product}
    uintx CMSIsTooFullPercentage                    = 98                                  {product}
    double CMSLargeCoalSurplusPercent                = 0.950000                            {product}
    double CMSLargeSplitSurplusPercent               = 1.000000                            {product}
    bool CMSLoopWarn                               = false                               {product}
    uintx CMSMaxAbortablePrecleanLoops              = 0                                   {product}
    intx CMSMaxAbortablePrecleanTime               = 5000                                {product}
    uintx CMSOldPLABMax                             = 1024                                {product}
    uintx CMSOldPLABMin                             = 16                                  {product}
    uintx CMSOldPLABNumRefills                      = 4                                   {product}
    uintx CMSOldPLABReactivityFactor                = 2                                   {product}
    bool CMSOldPLABResizeQuicker                   = false                               {product}
    uintx CMSOldPLABToleranceFactor                 = 4                                   {product}
    bool CMSPLABRecordAlways                       = true                                {product}
    uintx CMSParPromoteBlocksToClaim                = 16                                  {product}
    bool CMSParallelInitialMarkEnabled             = true                                {product}
    bool CMSParallelRemarkEnabled                  = true                                {product}
    bool CMSParallelSurvivorRemarkEnabled          = true                                {product}
    uintx CMSPrecleanDenominator                    = 3                                   {product}
    uintx CMSPrecleanIter                           = 3                                   {product}
    uintx CMSPrecleanNumerator                      = 2                                   {product}
    bool CMSPrecleanRefLists1                      = true                                {product}
    bool CMSPrecleanRefLists2                      = false                               {product}
    bool CMSPrecleanSurvivors1                     = false                               {product}
    bool CMSPrecleanSurvivors2                     = true                                {product}
    uintx CMSPrecleanThreshold                      = 1000                                {product}
    bool CMSPrecleaningEnabled                     = true                                {product}
    bool CMSPrintChunksInDump                      = false                               {product}
    bool CMSPrintEdenSurvivorChunks                = false                               {product}
    bool CMSPrintObjectsInDump                     = false                               {product}
    uintx CMSRemarkVerifyVariant                    = 1                                   {product}
    bool CMSReplenishIntermediate                  = true                                {product}
    uintx CMSRescanMultiple                         = 32                                  {product}
    uintx CMSSamplingGrain                          = 16384                               {product}
    bool CMSScavengeBeforeRemark                   = false                               {product}
    uintx CMSScheduleRemarkEdenPenetration          = 50                                  {product}
    uintx CMSScheduleRemarkEdenSizeThreshold        = 2097152                             {product}
    uintx CMSScheduleRemarkSamplingRatio            = 5                                   {product}
    double CMSSmallCoalSurplusPercent                = 1.050000                            {product}
    double CMSSmallSplitSurplusPercent               = 1.100000                            {product}
    bool CMSSplitIndexedFreeListBlocks             = true                                {product}
    intx CMSTriggerInterval                        = -1                                  {manageable}
    uintx CMSTriggerRatio                           = 80                                  {product}
    intx CMSWaitDuration                           = 2000                                {manageable}
    uintx CMSWorkQueueDrainThreshold                = 10                                  {product}
    bool CMSYield                                  = true                                {product}
    uintx CMSYieldSleepCount                        = 0                                   {product}
    uintx CMSYoungGenPerWorker                      = 67108864                            {pd product}
    uintx CMS_FLSPadding                            = 1                                   {product}
    uintx CMS_FLSWeight                             = 75                                  {product}
    uintx CMS_SweepPadding                          = 1                                   {product}
    uintx CMS_SweepTimerThresholdMillis             = 10                                  {product}
    uintx CMS_SweepWeight                           = 75                                  {product}
    bool CheckEndorsedAndExtDirs                   = false                               {product}
    bool CheckJNICalls                             = false                               {product}
    bool ClassUnloading                            = true                                {product}
    bool ClassUnloadingWithConcurrentMark          = true                                {product}
    intx ClearFPUAtPark                            = 0                                   {product}
    bool ClipInlining                              = true                                {product}
    uintx CodeCacheExpansionSize                    = 65536                               {pd product}
    uintx CodeCacheMinimumFreeSpace                 = 512000                              {product}
    bool CollectGen0First                          = false                               {product}
    bool CompactFields                             = true                                {product}
    intx CompilationPolicyChoice                   = 3                                   {product}
    ccstrlist CompileCommand                            =                                     {product}
    ccstr CompileCommandFile                        =                                     {product}
    ccstrlist CompileOnly                               =                                     {product}
    intx CompileThreshold                          = 10000                               {pd product}
    bool CompilerThreadHintNoPreempt               = true                                {product}
    intx CompilerThreadPriority                    = -1                                  {product}
    intx CompilerThreadStackSize                   = 0                                   {pd product}
    uintx CompressedClassSpaceSize                  = 1073741824                          {product}
    uintx ConcGCThreads                             = 0                                   {product}
    intx ConditionalMoveLimit                      = 3                                   {C2 pd product}
    intx ContendedPaddingWidth                     = 128                                 {product}
    bool ConvertSleepToYield                       = true                                {pd product}
    bool ConvertYieldToSleep                       = false                               {product}
    bool CrashOnOutOfMemoryError                   = false                               {product}
    bool CreateMinidumpOnCrash                     = false                               {product}
    bool CriticalJNINatives                        = true                                {product}
    bool DTraceAllocProbes                         = false                               {product}
    bool DTraceMethodProbes                        = false                               {product}
    bool DTraceMonitorProbes                       = false                               {product}
    bool Debugging                                 = false                               {product}
    uintx DefaultMaxRAMFraction                     = 4                                   {product}
    intx DefaultThreadPriority                     = -1                                  {product}
    intx DeferPollingPageLoopCount                 = -1                                  {product}
    intx DeferThrSuspendLoopCount                  = 4000                                {product}
    bool DeoptimizeRandom                          = false                               {product}
    bool DisableAttachMechanism                    = false                               {product}
    bool DisableExplicitGC                         = false                               {product}
    bool DisplayVMOutputToStderr                   = false                               {product}
    bool DisplayVMOutputToStdout                   = false                               {product}
    bool DoEscapeAnalysis                          = true                                {C2 product}
    bool DontCompileHugeMethods                    = true                                {product}
    bool DontYieldALot                             = false                               {pd product}
    ccstr DumpLoadedClassList                       =                                     {product}
    bool DumpReplayDataOnError                     = true                                {product}
    bool DumpSharedSpaces                          = false                               {product}
    bool EagerXrunInit                             = false                               {product}
    intx EliminateAllocationArraySizeLimit         = 64                                  {C2 product}
    bool EliminateAllocations                      = true                                {C2 product}
    bool EliminateAutoBox                          = true                                {C2 product}
    bool EliminateLocks                            = true                                {C2 product}
    bool EliminateNestedLocks                      = true                                {C2 product}
    intx EmitSync                                  = 0                                   {product}
    bool EnableContended                           = true                                {product}
    bool EnableResourceManagementTLABCache         = true                                {product}
    bool EnableSharedLookupCache                   = true                                {product}
    bool EnableTracing                             = false                               {product}
    uintx ErgoHeapSizeLimit                         = 0                                   {product}
    ccstr ErrorFile                                 =                                     {product}
    ccstr ErrorReportServer                         =                                     {product}
    double EscapeAnalysisTimeout                     = 20.000000                           {C2 product}
    bool EstimateArgEscape                         = true                                {product}
    bool ExitOnOutOfMemoryError                    = false                               {product}
    bool ExplicitGCInvokesConcurrent               = false                               {product}
    bool ExplicitGCInvokesConcurrentAndUnloadsClasses  = false                               {product}
    bool ExtendedDTraceProbes                      = false                               {product}
    ccstr ExtraSharedClassListFile                  =                                     {product}
    bool FLSAlwaysCoalesceLarge                    = false                               {product}
    uintx FLSCoalescePolicy                         = 2                                   {product}
    double FLSLargestBlockCoalesceProximity          = 0.990000                            {product}
    bool FailOverToOldVerifier                     = true                                {product}
    bool FastTLABRefill                            = true                                {product}
    intx FenceInstruction                          = 0                                   {ARCH product}
    intx FieldsAllocationStyle                     = 1                                   {product}
    bool FilterSpuriousWakeups                     = true                                {product}
    ccstr FlightRecorderOptions                     =                                     {product}
    bool ForceNUMA                                 = false                               {product}
    bool ForceTimeHighResolution                   = false                               {product}
    intx FreqInlineSize                            = 325                                 {pd product}
    double G1ConcMarkStepDurationMillis              = 10.000000                           {product}
    uintx G1ConcRSHotCardLimit                      = 4                                   {product}
    uintx G1ConcRSLogCacheSize                      = 10                                  {product}
    intx G1ConcRefinementGreenZone                 = 0                                   {product}
    intx G1ConcRefinementRedZone                   = 0                                   {product}
    intx G1ConcRefinementServiceIntervalMillis     = 300                                 {product}
    uintx G1ConcRefinementThreads                   = 0                                   {product}
    intx G1ConcRefinementThresholdStep             = 0                                   {product}
    intx G1ConcRefinementYellowZone                = 0                                   {product}
    uintx G1ConfidencePercent                       = 50                                  {product}
    uintx G1HeapRegionSize                          = 0                                   {product}
    uintx G1HeapWastePercent                        = 5                                   {product}
    uintx G1MixedGCCountTarget                      = 8                                   {product}
    intx G1RSetRegionEntries                       = 0                                   {product}
    uintx G1RSetScanBlockSize                       = 64                                  {product}
    intx G1RSetSparseRegionEntries                 = 0                                   {product}
    intx G1RSetUpdatingPauseTimePercent            = 10                                  {product}
    intx G1RefProcDrainInterval                    = 10                                  {product}
    uintx G1ReservePercent                          = 10                                  {product}
    uintx G1SATBBufferEnqueueingThresholdPercent    = 60                                  {product}
    intx G1SATBBufferSize                          = 1024                                {product}
    intx G1UpdateBufferSize                        = 256                                 {product}
    bool G1UseAdaptiveConcRefinement               = true                                {product}
    uintx GCDrainStackTargetSize                    = 64                                  {product}
    uintx GCHeapFreeLimit                           = 2                                   {product}
    uintx GCLockerEdenExpansionPercent              = 5                                   {product}
    bool GCLockerInvokesConcurrent                 = false                               {product}
    uintx GCLogFileSize                             = 8192                                {product}
    uintx GCPauseIntervalMillis                     = 0                                   {product}
    uintx GCTaskTimeStampEntries                    = 200                                 {product}
    uintx GCTimeLimit                               = 98                                  {product}
    uintx GCTimeRatio                               = 99                                  {product}
    uintx HeapBaseMinAddress                        = 2147483648                          {pd product}
    bool HeapDumpAfterFullGC                       = false                               {manageable}
    bool HeapDumpBeforeFullGC                      = false                               {manageable}
    bool HeapDumpOnOutOfMemoryError                = false                               {manageable}
    ccstr HeapDumpPath                              =                                     {manageable}
    uintx HeapFirstMaximumCompactionCount           = 3                                   {product}
    uintx HeapMaximumCompactionInterval             = 20                                  {product}
    uintx HeapSizePerGCThread                       = 87241520                            {product}
    bool IgnoreEmptyClassPaths                     = false                               {product}
    bool IgnoreUnrecognizedVMOptions               = false                               {product}
    uintx IncreaseFirstTierCompileThresholdAt       = 50                                  {product}
    bool IncrementalInline                         = true                                {C2 product}
    uintx InitialBootClassLoaderMetaspaceSize       = 4194304                             {product}
    uintx InitialCodeCacheSize                      = 2555904                             {pd product}
    uintx InitialHeapSize                          := 134217728                           {product}
    uintx InitialRAMFraction                        = 64                                  {product}
    uintx InitialSurvivorRatio                      = 8                                   {product}
    uintx InitialTenuringThreshold                  = 7                                   {product}
    uintx InitiatingHeapOccupancyPercent            = 45                                  {product}
    bool Inline                                    = true                                {product}
    ccstr InlineDataFile                            =                                     {product}
    intx InlineSmallCode                           = 2000                                {pd product}
    bool InlineSynchronizedMethods                 = true                                {C1 product}
    bool InsertMemBarAfterArraycopy                = true                                {C2 product}
    intx InteriorEntryAlignment                    = 16                                  {C2 pd product}
    intx InterpreterProfilePercentage              = 33                                  {product}
    bool JNIDetachReleasesMonitors                 = true                                {product}
    bool JavaMonitorsInStackTrace                  = true                                {product}
    intx JavaPriority10_To_OSPriority              = -1                                  {product}
    intx JavaPriority1_To_OSPriority               = -1                                  {product}
    intx JavaPriority2_To_OSPriority               = -1                                  {product}
    intx JavaPriority3_To_OSPriority               = -1                                  {product}
    intx JavaPriority4_To_OSPriority               = -1                                  {product}
    intx JavaPriority5_To_OSPriority               = -1                                  {product}
    intx JavaPriority6_To_OSPriority               = -1                                  {product}
    intx JavaPriority7_To_OSPriority               = -1                                  {product}
    intx JavaPriority8_To_OSPriority               = -1                                  {product}
    intx JavaPriority9_To_OSPriority               = -1                                  {product}
    bool LIRFillDelaySlots                         = false                               {C1 pd product}
    uintx LargePageHeapSizeThreshold                = 134217728                           {product}
    uintx LargePageSizeInBytes                      = 0                                   {product}
    bool LazyBootClassLoader                       = true                                {product}
    intx LiveNodeCountInliningCutoff               = 40000                               {C2 product}
    bool LogCommercialFeatures                     = false                               {product}
    intx LoopMaxUnroll                             = 16                                  {C2 product}
    intx LoopOptsCount                             = 43                                  {C2 product}
    intx LoopUnrollLimit                           = 60                                  {C2 pd product}
    intx LoopUnrollMin                             = 4                                   {C2 product}
    bool LoopUnswitching                           = true                                {C2 product}
    bool ManagementServer                          = false                               {product}
    uintx MarkStackSize                             = 4194304                             {product}
    uintx MarkStackSizeMax                          = 536870912                           {product}
    uintx MarkSweepAlwaysCompactCount               = 4                                   {product}
    uintx MarkSweepDeadRatio                        = 1                                   {product}
    intx MaxBCEAEstimateLevel                      = 5                                   {product}
    intx MaxBCEAEstimateSize                       = 150                                 {product}
    uintx MaxDirectMemorySize                       = 0                                   {product}
    bool MaxFDLimit                                = true                                {product}
    uintx MaxGCMinorPauseMillis                     = 4294967295                          {product}
    uintx MaxGCPauseMillis                          = 4294967295                          {product}
    uintx MaxHeapFreeRatio                          = 100                                 {manageable}
    uintx MaxHeapSize                              := 2126512128                          {product}
    intx MaxInlineLevel                            = 9                                   {product}
    intx MaxInlineSize                             = 35                                  {product}
    intx MaxJNILocalCapacity                       = 65536                               {product}
    intx MaxJavaStackTraceDepth                    = 1024                                {product}
    intx MaxJumpTableSize                          = 65000                               {C2 product}
    intx MaxJumpTableSparseness                    = 5                                   {C2 product}
    intx MaxLabelRootDepth                         = 1100                                {C2 product}
    intx MaxLoopPad                                = 11                                  {C2 product}
    uintx MaxMetaspaceExpansion                     = 5451776                             {product}
    uintx MaxMetaspaceFreeRatio                     = 70                                  {product}
    uintx MaxMetaspaceSize                          = 4294901760                          {product}
    uintx MaxNewSize                               := 708837376                           {product}
    intx MaxNodeLimit                              = 75000                               {C2 product}
    uint64_t MaxRAM                                    = 0                                   {pd product}
    uintx MaxRAMFraction                            = 4                                   {product}
    intx MaxRecursiveInlineLevel                   = 1                                   {product}
    uintx MaxTenuringThreshold                      = 15                                  {product}
    intx MaxTrivialSize                            = 6                                   {product}
    intx MaxVectorSize                             = 32                                  {C2 product}
    uintx MetaspaceSize                             = 21807104                            {pd product}
    bool MethodFlushing                            = true                                {product}
    uintx MinHeapDeltaBytes                        := 524288                              {product}
    uintx MinHeapFreeRatio                          = 0                                   {manageable}
    intx MinInliningThreshold                      = 250                                 {product}
    intx MinJumpTableSize                          = 10                                  {C2 pd product}
    uintx MinMetaspaceExpansion                     = 339968                              {product}
    uintx MinMetaspaceFreeRatio                     = 40                                  {product}
    uintx MinRAMFraction                            = 2                                   {product}
    uintx MinSurvivorRatio                          = 3                                   {product}
    uintx MinTLABSize                               = 2048                                {product}
    intx MonitorBound                              = 0                                   {product}
    bool MonitorInUseLists                         = false                               {product}
    intx MultiArrayExpandLimit                     = 6                                   {C2 product}
    bool MustCallLoadClassInternal                 = false                               {product}
    uintx NUMAChunkResizeWeight                     = 20                                  {product}
    uintx NUMAInterleaveGranularity                 = 2097152                             {product}
    uintx NUMAPageScanRate                          = 256                                 {product}
    uintx NUMASpaceResizeRate                       = 1073741824                          {product}
    bool NUMAStats                                 = false                               {product}
    ccstr NativeMemoryTracking                      = off                                 {product}
    bool NeedsDeoptSuspend                         = false                               {pd product}
    bool NeverActAsServerClassMachine              = false                               {pd product}
    bool NeverTenure                               = false                               {product}
    uintx NewRatio                                  = 2                                   {product}
    uintx NewSize                                  := 44564480                            {product}
    uintx NewSizeThreadIncrease                     = 5320                                {pd product}
    intx NmethodSweepActivity                      = 10                                  {product}
    intx NmethodSweepCheckInterval                 = 5                                   {product}
    intx NmethodSweepFraction                      = 16                                  {product}
    intx NodeLimitFudgeFactor                      = 2000                                {C2 product}
    uintx NumberOfGCLogFiles                        = 0                                   {product}
    intx NumberOfLoopInstrToAlign                  = 4                                   {C2 product}
    intx ObjectAlignmentInBytes                    = 8                                   {lp64_product}
    uintx OldPLABSize                               = 1024                                {product}
    uintx OldPLABWeight                             = 50                                  {product}
    uintx OldSize                                  := 89653248                            {product}
    bool OmitStackTraceInFastThrow                 = true                                {product}
    ccstrlist OnError                                   =                                     {product}
    ccstrlist OnOutOfMemoryError                        =                                     {product}
    intx OnStackReplacePercentage                  = 140                                 {pd product}
    bool OptimizeFill                              = true                                {C2 product}
    bool OptimizePtrCompare                        = true                                {C2 product}
    bool OptimizeStringConcat                      = true                                {C2 product}
    bool OptoBundling                              = false                               {C2 pd product}
    intx OptoLoopAlignment                         = 16                                  {pd product}
    bool OptoScheduling                            = false                               {C2 pd product}
    uintx PLABWeight                                = 75                                  {product}
    bool PSChunkLargeArrays                        = true                                {product}
    intx ParGCArrayScanChunk                       = 50                                  {product}
    uintx ParGCDesiredObjsFromOverflowList          = 20                                  {product}
    bool ParGCTrimOverflow                         = true                                {product}
    bool ParGCUseLocalOverflow                     = false                               {product}
    uintx ParallelGCBufferWastePct                  = 10                                  {product}
    uintx ParallelGCThreads                         = 4                                   {product}
    bool ParallelGCVerbose                         = false                               {product}
    uintx ParallelOldDeadWoodLimiterMean            = 50                                  {product}
    uintx ParallelOldDeadWoodLimiterStdDev          = 80                                  {product}
    bool ParallelRefProcBalancingEnabled           = true                                {product}
    bool ParallelRefProcEnabled                    = false                               {product}
    bool PartialPeelAtUnsignedTests                = true                                {C2 product}
    bool PartialPeelLoop                           = true                                {C2 product}
    intx PartialPeelNewPhiDelta                    = 0                                   {C2 product}
    uintx PausePadding                              = 1                                   {product}
    intx PerBytecodeRecompilationCutoff            = 200                                 {product}
    intx PerBytecodeTrapLimit                      = 4                                   {product}
    intx PerMethodRecompilationCutoff              = 400                                 {product}
    intx PerMethodTrapLimit                        = 100                                 {product}
    bool PerfAllowAtExitRegistration               = false                               {product}
    bool PerfBypassFileSystemCheck                 = false                               {product}
    intx PerfDataMemorySize                        = 32768                               {product}
    intx PerfDataSamplingInterval                  = 50                                  {product}
    ccstr PerfDataSaveFile                          =                                     {product}
    bool PerfDataSaveToFile                        = false                               {product}
    bool PerfDisableSharedMem                      = false                               {product}
    intx PerfMaxStringConstLength                  = 1024                                {product}
    intx PreInflateSpin                            = 10                                  {pd product}
    bool PreferInterpreterNativeStubs              = false                               {pd product}
    intx PrefetchCopyIntervalInBytes               = 576                                 {product}
    intx PrefetchFieldsAhead                       = 1                                   {product}
    intx PrefetchScanIntervalInBytes               = 576                                 {product}
    bool PreserveAllAnnotations                    = false                               {product}
    bool PreserveFramePointer                      = false                               {pd product}
    uintx PretenureSizeThreshold                    = 0                                   {product}
    bool PrintAdaptiveSizePolicy                   = false                               {product}
    bool PrintCMSInitiationStatistics              = false                               {product}
    intx PrintCMSStatistics                        = 0                                   {product}
    bool PrintClassHistogram                       = false                               {manageable}
    bool PrintClassHistogramAfterFullGC            = false                               {manageable}
    bool PrintClassHistogramBeforeFullGC           = false                               {manageable}
    bool PrintCodeCache                            = false                               {product}
    bool PrintCodeCacheOnCompilation               = false                               {product}
    bool PrintCommandLineFlags                     = false                               {product}
    bool PrintCompilation                          = false                               {product}
    bool PrintConcurrentLocks                      = false                               {manageable}
    intx PrintFLSCensus                            = 0                                   {product}
    intx PrintFLSStatistics                        = 0                                   {product}
    bool PrintFlagsFinal                          := true                                {product}
    bool PrintFlagsInitial                         = false                               {product}
    bool PrintGC                                   = false                               {manageable}
    bool PrintGCApplicationConcurrentTime          = false                               {product}
    bool PrintGCApplicationStoppedTime             = false                               {product}
    bool PrintGCCause                              = true                                {product}
    bool PrintGCDateStamps                         = false                               {manageable}
    bool PrintGCDetails                            = false                               {manageable}
    bool PrintGCID                                 = false                               {manageable}
    bool PrintGCTaskTimeStamps                     = false                               {product}
    bool PrintGCTimeStamps                         = false                               {manageable}
    bool PrintHeapAtGC                             = false                               {product rw}
    bool PrintHeapAtGCExtended                     = false                               {product rw}
    bool PrintHeapAtSIGBREAK                       = true                                {product}
    bool PrintJNIGCStalls                          = false                               {product}
    bool PrintJNIResolving                         = false                               {product}
    bool PrintOldPLAB                              = false                               {product}
    bool PrintOopAddress                           = false                               {product}
    bool PrintPLAB                                 = false                               {product}
    bool PrintParallelOldGCPhaseTimes              = false                               {product}
    bool PrintPromotionFailure                     = false                               {product}
    bool PrintReferenceGC                          = false                               {product}
    bool PrintSafepointStatistics                  = false                               {product}
    intx PrintSafepointStatisticsCount             = 300                                 {product}
    intx PrintSafepointStatisticsTimeout           = -1                                  {product}
    bool PrintSharedArchiveAndExit                 = false                               {product}
    bool PrintSharedDictionary                     = false                               {product}
    bool PrintSharedSpaces                         = false                               {product}
    bool PrintStringDeduplicationStatistics        = false                               {product}
    bool PrintStringTableStatistics                = false                               {product}
    bool PrintTLAB                                 = false                               {product}
    bool PrintTenuringDistribution                 = false                               {product}
    bool PrintTieredEvents                         = false                               {product}
    bool PrintVMOptions                            = false                               {product}
    bool PrintVMQWaitTime                          = false                               {product}
    bool PrintWarnings                             = true                                {product}
    uintx ProcessDistributionStride                 = 4                                   {product}
    bool ProfileInterpreter                        = true                                {pd product}
    bool ProfileIntervals                          = false                               {product}
    intx ProfileIntervalsTicks                     = 100                                 {product}
    intx ProfileMaturityPercentage                 = 20                                  {product}
    bool ProfileVM                                 = false                               {product}
    bool ProfilerPrintByteCodeStatistics           = false                               {product}
    bool ProfilerRecordPC                          = false                               {product}
    uintx PromotedPadding                           = 3                                   {product}
    uintx QueuedAllocationWarningCount              = 0                                   {product}
    uintx RTMRetryCount                             = 5                                   {ARCH product}
    bool RangeCheckElimination                     = true                                {product}
    intx ReadPrefetchInstr                         = 0                                   {ARCH product}
    bool ReassociateInvariants                     = true                                {C2 product}
    bool ReduceBulkZeroing                         = true                                {C2 product}
    bool ReduceFieldZeroing                        = true                                {C2 product}
    bool ReduceInitialCardMarks                    = true                                {C2 product}
    bool ReduceSignalUsage                         = false                               {product}
    intx RefDiscoveryPolicy                        = 0                                   {product}
    bool ReflectionWrapResolutionErrors            = true                                {product}
    bool RegisterFinalizersAtInit                  = true                                {product}
    bool RelaxAccessControlCheck                   = false                               {product}
    ccstr ReplayDataFile                            =                                     {product}
    bool RequireSharedSpaces                       = false                               {product}
    uintx ReservedCodeCacheSize                     = 251658240                           {pd product}
    bool ResizeOldPLAB                             = true                                {product}
    bool ResizePLAB                                = true                                {product}
    bool ResizeTLAB                                = true                                {pd product}
    bool RestoreMXCSROnJNICalls                    = false                               {product}
    bool RestrictContended                         = true                                {product}
    bool RewriteBytecodes                          = true                                {pd product}
    bool RewriteFrequentPairs                      = true                                {pd product}
    intx SafepointPollOffset                       = 256                                 {C1 pd product}
    intx SafepointSpinBeforeYield                  = 2000                                {product}
    bool SafepointTimeout                          = false                               {product}
    intx SafepointTimeoutDelay                     = 10000                               {product}
    bool ScavengeBeforeFullGC                      = true                                {product}
    intx SelfDestructTimer                         = 0                                   {product}
    uintx SharedBaseAddress                         = 0                                   {product}
    ccstr SharedClassListFile                       =                                     {product}
    uintx SharedMiscCodeSize                        = 122880                              {product}
    uintx SharedMiscDataSize                        = 4194304                             {product}
    uintx SharedReadOnlySize                        = 16777216                            {product}
    uintx SharedReadWriteSize                       = 16777216                            {product}
    bool ShowMessageBoxOnError                     = false                               {product}
    intx SoftRefLRUPolicyMSPerMB                   = 1000                                {product}
    bool SpecialEncodeISOArray                     = true                                {C2 product}
    bool SplitIfBlocks                             = true                                {C2 product}
    intx StackRedPages                             = 1                                   {pd product}
    intx StackShadowPages                          = 6                                   {pd product}
    bool StackTraceInThrowable                     = true                                {product}
    intx StackYellowPages                          = 3                                   {pd product}
    bool StartAttachListener                       = false                               {product}
    intx StarvationMonitorInterval                 = 200                                 {product}
    bool StressLdcRewrite                          = false                               {product}
    uintx StringDeduplicationAgeThreshold           = 3                                   {product}
    uintx StringTableSize                           = 60013                               {product}
    bool SuppressFatalErrorMessage                 = false                               {product}
    uintx SurvivorPadding                           = 3                                   {product}
    uintx SurvivorRatio                             = 8                                   {product}
    intx SuspendRetryCount                         = 50                                  {product}
    intx SuspendRetryDelay                         = 5                                   {product}
    intx SyncFlags                                 = 0                                   {product}
    ccstr SyncKnobs                                 =                                     {product}
    intx SyncVerbose                               = 0                                   {product}
    uintx TLABAllocationWeight                      = 35                                  {product}
    uintx TLABRefillWasteFraction                   = 64                                  {product}
    uintx TLABSize                                  = 0                                   {product}
    bool TLABStats                                 = true                                {product}
    uintx TLABWasteIncrement                        = 4                                   {product}
    uintx TLABWasteTargetPercent                    = 1                                   {product}
    uintx TargetPLABWastePct                        = 10                                  {product}
    uintx TargetSurvivorRatio                       = 50                                  {product}
    uintx TenuredGenerationSizeIncrement            = 20                                  {product}
    uintx TenuredGenerationSizeSupplement           = 80                                  {product}
    uintx TenuredGenerationSizeSupplementDecay      = 2                                   {product}
    intx ThreadPriorityPolicy                      = 0                                   {product}
    bool ThreadPriorityVerbose                     = false                               {product}
    uintx ThreadSafetyMargin                        = 52428800                            {product}
    intx ThreadStackSize                           = 0                                   {pd product}
    uintx ThresholdTolerance                        = 10                                  {product}
    intx Tier0BackedgeNotifyFreqLog                = 10                                  {product}
    intx Tier0InvokeNotifyFreqLog                  = 7                                   {product}
    intx Tier0ProfilingStartPercentage             = 200                                 {product}
    intx Tier23InlineeNotifyFreqLog                = 20                                  {product}
    intx Tier2BackEdgeThreshold                    = 0                                   {product}
    intx Tier2BackedgeNotifyFreqLog                = 14                                  {product}
    intx Tier2CompileThreshold                     = 0                                   {product}
    intx Tier2InvokeNotifyFreqLog                  = 11                                  {product}
    intx Tier3BackEdgeThreshold                    = 60000                               {product}
    intx Tier3BackedgeNotifyFreqLog                = 13                                  {product}
    intx Tier3CompileThreshold                     = 2000                                {product}
    intx Tier3DelayOff                             = 2                                   {product}
    intx Tier3DelayOn                              = 5                                   {product}
    intx Tier3InvocationThreshold                  = 200                                 {product}
    intx Tier3InvokeNotifyFreqLog                  = 10                                  {product}
    intx Tier3LoadFeedback                         = 5                                   {product}
    intx Tier3MinInvocationThreshold               = 100                                 {product}
    intx Tier4BackEdgeThreshold                    = 40000                               {product}
    intx Tier4CompileThreshold                     = 15000                               {product}
    intx Tier4InvocationThreshold                  = 5000                                {product}
    intx Tier4LoadFeedback                         = 3                                   {product}
    intx Tier4MinInvocationThreshold               = 600                                 {product}
    bool TieredCompilation                         = true                                {pd product}
    intx TieredCompileTaskTimeout                  = 50                                  {product}
    intx TieredRateUpdateMaxTime                   = 25                                  {product}
    intx TieredRateUpdateMinTime                   = 1                                   {product}
    intx TieredStopAtLevel                         = 4                                   {product}
    bool TimeLinearScan                            = false                               {C1 product}
    bool TraceBiasedLocking                        = false                               {product}
    bool TraceClassLoading                         = false                               {product rw}
    bool TraceClassLoadingPreorder                 = false                               {product}
    bool TraceClassPaths                           = false                               {product}
    bool TraceClassResolution                      = false                               {product}
    bool TraceClassUnloading                       = false                               {product rw}
    bool TraceDynamicGCThreads                     = false                               {product}
    bool TraceGen0Time                             = false                               {product}
    bool TraceGen1Time                             = false                               {product}
    ccstr TraceJVMTI                                =                                     {product}
    bool TraceLoaderConstraints                    = false                               {product rw}
    bool TraceMetadataHumongousAllocation          = false                               {product}
    bool TraceMonitorInflation                     = false                               {product}
    bool TraceParallelOldGCTasks                   = false                               {product}
    intx TraceRedefineClasses                      = 0                                   {product}
    bool TraceSafepointCleanupTime                 = false                               {product}
    bool TraceSharedLookupCache                    = false                               {product}
    bool TraceSuspendWaitFailures                  = false                               {product}
    intx TrackedInitializationLimit                = 50                                  {C2 product}
    bool TransmitErrorReport                       = false                               {product}
    bool TrapBasedNullChecks                       = false                               {pd product}
    bool TrapBasedRangeChecks                      = false                               {C2 pd product}
    intx TypeProfileArgsLimit                      = 2                                   {product}
    uintx TypeProfileLevel                          = 111                                 {pd product}
    intx TypeProfileMajorReceiverPercent           = 90                                  {C2 product}
    intx TypeProfileParmsLimit                     = 2                                   {product}
    intx TypeProfileWidth                          = 2                                   {product}
    intx UnguardOnExecutionViolation               = 0                                   {product}
    bool UnlinkSymbolsALot                         = false                               {product}
    bool Use486InstrsOnly                          = false                               {ARCH product}
    bool UseAES                                    = true                                {product}
    bool UseAESIntrinsics                          = true                                {product}
    intx UseAVX                                    = 2                                   {ARCH product}
    bool UseAdaptiveGCBoundary                     = false                               {product}
    bool UseAdaptiveGenerationSizePolicyAtMajorCollection  = true                                {product}
    bool UseAdaptiveGenerationSizePolicyAtMinorCollection  = true                                {product}
    bool UseAdaptiveNUMAChunkSizing                = true                                {product}
    bool UseAdaptiveSizeDecayMajorGCCost           = true                                {product}
    bool UseAdaptiveSizePolicy                     = true                                {product}
    bool UseAdaptiveSizePolicyFootprintGoal        = true                                {product}
    bool UseAdaptiveSizePolicyWithSystemGC         = false                               {product}
    bool UseAddressNop                             = true                                {ARCH product}
    bool UseAltSigs                                = false                               {product}
    bool UseAutoGCSelectPolicy                     = false                               {product}
    bool UseBMI1Instructions                       = true                                {ARCH product}
    bool UseBMI2Instructions                       = true                                {ARCH product}
    bool UseBiasedLocking                          = true                                {product}
    bool UseBimorphicInlining                      = true                                {C2 product}
    bool UseBoundThreads                           = true                                {product}
    bool UseCLMUL                                  = true                                {ARCH product}
    bool UseCMSBestFit                             = true                                {product}
    bool UseCMSCollectionPassing                   = true                                {product}
    bool UseCMSCompactAtFullCollection             = true                                {product}
    bool UseCMSInitiatingOccupancyOnly             = false                               {product}
    bool UseCRC32Intrinsics                        = true                                {product}
    bool UseCodeCacheFlushing                      = true                                {product}
    bool UseCompiler                               = true                                {product}
    bool UseCompilerSafepoints                     = true                                {product}
    bool UseCompressedClassPointers               := true                                {lp64_product}
    bool UseCompressedOops                        := true                                {lp64_product}
    bool UseConcMarkSweepGC                        = false                               {product}
    bool UseCondCardMark                           = false                               {C2 product}
    bool UseCountLeadingZerosInstruction           = true                                {ARCH product}
    bool UseCountTrailingZerosInstruction          = true                                {ARCH product}
    bool UseCountedLoopSafepoints                  = false                               {C2 product}
    bool UseCounterDecay                           = true                                {product}
    bool UseDivMod                                 = true                                {C2 product}
    bool UseDynamicNumberOfGCThreads               = false                               {product}
    bool UseFPUForSpilling                         = true                                {C2 product}
    bool UseFastAccessorMethods                    = false                               {product}
    bool UseFastEmptyMethods                       = false                               {product}
    bool UseFastJNIAccessors                       = true                                {product}
    bool UseFastStosb                              = true                                {ARCH product}
    bool UseG1GC                                   = false                               {product}
    bool UseGCLogFileRotation                      = false                               {product}
    bool UseGCOverheadLimit                        = true                                {product}
    bool UseGCTaskAffinity                         = false                               {product}
    bool UseHeavyMonitors                          = false                               {product}
    bool UseInlineCaches                           = true                                {product}
    bool UseInterpreter                            = true                                {product}
    bool UseJumpTables                             = true                                {C2 product}
    bool UseLWPSynchronization                     = true                                {product}
    bool UseLargePages                             = false                               {pd product}
    bool UseLargePagesInMetaspace                  = false                               {product}
    bool UseLargePagesIndividualAllocation        := false                               {pd product}
    bool UseLockedTracing                          = false                               {product}
    bool UseLoopCounter                            = true                                {product}
    bool UseLoopInvariantCodeMotion                = true                                {C1 product}
    bool UseLoopPredicate                          = true                                {C2 product}
    bool UseMathExactIntrinsics                    = true                                {C2 product}
    bool UseMaximumCompactionOnSystemGC            = true                                {product}
    bool UseMembar                                 = false                               {pd product}
    bool UseMontgomeryMultiplyIntrinsic            = true                                {C2 product}
    bool UseMontgomerySquareIntrinsic              = true                                {C2 product}
    bool UseMulAddIntrinsic                        = true                                {C2 product}
    bool UseMultiplyToLenIntrinsic                 = true                                {C2 product}
    bool UseNUMA                                   = false                               {product}
    bool UseNUMAInterleaving                       = false                               {product}
    bool UseNewLongLShift                          = false                               {ARCH product}
    bool UseOSErrorReporting                       = false                               {pd product}
    bool UseOldInlining                            = true                                {C2 product}
    bool UseOnStackReplacement                     = true                                {pd product}
    bool UseOnlyInlinedBimorphic                   = true                                {C2 product}
    bool UseOptoBiasInlining                       = true                                {C2 product}
    bool UsePSAdaptiveSurvivorSizePolicy           = true                                {product}
    bool UseParNewGC                               = false                               {product}
    bool UseParallelGC                            := true                                {product}
    bool UseParallelOldGC                          = true                                {product}
    bool UsePerfData                               = true                                {product}
    bool UsePopCountInstruction                    = true                                {product}
    bool UseRDPCForConstantTableBase               = false                               {C2 product}
    bool UseRTMDeopt                               = false                               {ARCH product}
    bool UseRTMLocking                             = false                               {ARCH product}
    bool UseSHA                                    = false                               {product}
    bool UseSHA1Intrinsics                         = false                               {product}
    bool UseSHA256Intrinsics                       = false                               {product}
    bool UseSHA512Intrinsics                       = false                               {product}
    intx UseSSE                                    = 4                                   {product}
    bool UseSSE42Intrinsics                        = true                                {product}
    bool UseSerialGC                               = false                               {product}
    bool UseSharedSpaces                           = false                               {product}
    bool UseSignalChaining                         = true                                {product}
    bool UseSquareToLenIntrinsic                   = true                                {C2 product}
    bool UseStoreImmI16                            = false                               {ARCH product}
    bool UseStringDeduplication                    = false                               {product}
    bool UseSuperWord                              = true                                {C2 product}
    bool UseTLAB                                   = true                                {pd product}
    bool UseThreadPriorities                       = true                                {pd product}
    bool UseTypeProfile                            = true                                {product}
    bool UseTypeSpeculation                        = true                                {C2 product}
    bool UseUTCFileTimestamp                       = true                                {product}
    bool UseUnalignedLoadStores                    = true                                {ARCH product}
    bool UseVMInterruptibleIO                      = false                               {product}
    bool UseXMMForArrayCopy                        = true                                {product}
    bool UseXmmI2D                                 = false                               {ARCH product}
    bool UseXmmI2F                                 = false                               {ARCH product}
    bool UseXmmLoadAndClearUpper                   = true                                {ARCH product}
    bool UseXmmRegToRegMoveAll                     = true                                {ARCH product}
    bool VMThreadHintNoPreempt                     = false                               {product}
    intx VMThreadPriority                          = -1                                  {product}
    intx VMThreadStackSize                         = 0                                   {pd product}
    intx ValueMapInitialSize                       = 11                                  {C1 product}
    intx ValueMapMaxLoopSize                       = 8                                   {C1 product}
    intx ValueSearchLimit                          = 1000                                {C2 product}
    bool VerifyMergedCPBytecodes                   = true                                {product}
    bool VerifySharedSpaces                        = false                               {product}
    intx WorkAroundNPTLTimedWaitHang               = 1                                   {product}
    uintx YoungGenerationSizeIncrement              = 20                                  {product}
    uintx YoungGenerationSizeSupplement             = 80                                  {product}
    uintx YoungGenerationSizeSupplementDecay        = 8                                   {product}
    uintx YoungPLABSize                             = 4096                                {product}
    bool ZeroTLAB                                  = false                               {product}
    intx hashCode                                  = 5                                   {product}
    用法: java [-options] class [args...]
    (执行类)
    或  java [-options] -jar jarfile [args...]
    (执行 jar 文件)
    其中选项包括:
    -d32          使用 32 位数据模型 (如果可用)
    -d64          使用 64 位数据模型 (如果可用)
    -server       选择 "server" VM
    默认 VM 是 server.

    -cp <目录和 zip/jar 文件的类搜索路径>
    -classpath <目录和 zip/jar 文件的类搜索路径>
    用 ; 分隔的目录, JAR 档案
    和 ZIP 档案列表, 用于搜索类文件。
    -D<名称>=<值>
              设置系统属性
                      -verbose:[class|gc|jni]
    启用详细输出
    -version      输出产品版本并退出
    -version:<值>
              警告: 此功能已过时, 将在
                      未来发行版中删除。
                      需要指定的版本才能运行
                      -showversion  输出产品版本并继续
                      -jre-restrict-search | -no-jre-restrict-search
                      警告: 此功能已过时, 将在
                      未来发行版中删除。
                      在版本搜索中包括/排除用户专用 JRE
                      -? -help      输出此帮助消息
                      -X            输出非标准选项的帮助
                      -ea[:<packagename>...|:<classname>]
    -enableassertions[:<packagename>...|:<classname>]
    按指定的粒度启用断言
    -da[:<packagename>...|:<classname>]
    -disableassertions[:<packagename>...|:<classname>]
    禁用具有指定粒度的断言
    -esa | -enablesystemassertions
    启用系统断言
    -dsa | -disablesystemassertions
    禁用系统断言
    -agentlib:<libname>[=<选项>]
    加载本机代理库 <libname>, 例如 -agentlib:hprof
    另请参阅 -agentlib:jdwp=help 和 -agentlib:hprof=help
    -agentpath:<pathname>[=<选项>]
    按完整路径名加载本机代理库
    -javaagent:<jarpath>[=<选项>]
    加载 Java 编程语言代理, 请参阅 java.lang.instrument
    -splash:<imagepath>
              使用指定的图像显示启动屏幕
                      有关详细信息, 请参阅 http://www.oracle.com/technetwork/java/javase/documentation/index.html。
**/