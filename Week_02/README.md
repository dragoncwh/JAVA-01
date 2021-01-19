# 学习笔记

## 第一节课作业

1 使用 GCLogAnalysis.java 自己演练一遍串行/并行/CMS/G1的案例。 

(JDK 1.8, macOS Catalina 10.15.7, 内存16GB, CPU i7 Quad-Core 2.8G)

256m

| | 生成对象次数 | YGC | YGCT | FGC | FGCT |
| --- | --- | --- | --- | --- | --- |
| SerialGC | 4500/OOM | 7 | ~150ms | >35 | >650ms |
| ParallelGC | OOM | | | | |
| CMS GC | ~4.5k | 8 | ~115ms | 18 | |
| G1 GC | OOM | | | | |


512m

| | 生成对象次数 | YGC | YGCT | FGC | FGCT |
| --- | --- | --- | --- | --- | --- |
| SerialGC | ~8.6k | 16 | ~300ms | 8 | ~350ms |
| ParallelGC | ~7.7k | 29 | ~290ms | 8 | ~240ms |
| CMS GC | ~8.9k | 18 | ~262ms | 7 | |
| G1 GC | ~9.1k | N/A | N/A | N/A | N/A |


1g

| | 生成对象次数 | YGC | YGCT | FGC | FGCT |
| --- | --- | --- | --- | --- | --- |
| SerialGC | ~9k | 20 | ~450ms | 3 | ~120ms |
| ParallelGC | ~9.2k | 19 | ~335ms | 4 | ~150ms |
| CMS GC | ~8.5k | 30 | ~450ms | 2 | |
| G1 GC | ~10k | N/A | N/A | N/A | N/A |


2g

| | 生成对象次数 | YGC | YGCT | FGC | FGCT |
| --- | --- | --- | --- | --- | --- |
| SerialGC | ～8.9k | 19 | ~450ms | 3 | ~125ms |
| ParallelGC | ~10.5k | 11 | ~360ms | 3 | ~113ms |
| CMS GC | ~9.2k | 37 | ~560ms | 1 | |
| G1 GC | ~7.5k | N/A | N/A | N/A | N/A |


4g

| | 生成对象次数 | YGC | YGCT | FGC | FGCT |
| --- | --- | --- | --- | --- | --- |
| SerialGC | ~8.9k | 19 | ~450ms | 3 | ~125ms |
| ParallelGC | ~9.7k | 9 | ~340ms | 3 | ~85ms |
| CMS GC | ~9.3k | 37 | ~570ms | 1 | |
| G1 GC | ~6.8k | N/A | N/A | N/A | N/A |


2 使用压测工具(wrk或sb)，演练gateway-server-0.0.1-SNAPSHOT.jar示例。 

3 (选做)如果自己本地有可以运行的项目，可以按照2的方式进行演练。

4 (必做)根据上述自己对于1和2的演示，写一段对于不同GC和堆内存的总结，提交到 github。

> 1) SerialGC 256m: 由于堆内存太小，后面的时间基本全是在做Full GC
> 2) 相同堆大小的情况下，平均Young GC的时间: Serial GC > Parallel GC > CMS GC
> 3) 由于程序比较简单，且运行时间比较短，程序吞吐量基本跟GC时间成反比
> 4) 堆越大，分配给Young区的空间越大，YGC的频率越小，但是YGC STW停顿时间越长



