# 学习笔记

## 第三节课作业

1 使用 GCLogAnalysis.java 自己演练一遍串行/并行/CMS/G1的案例。 

(JDK 1.8, macOS Catalina 10.15.7, 内存16GB, CPU i7 Quad-Core 2.8G)

256m

| | 生成对象次数 | YGC | avg YGCT | FGC | FGCT | Pause Count | Pause total time | YG Allocated memory | YG peak | OG Allocated memory | OG peak |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| SerialGC | 4500/OOM | 7 | 22.9ms | 34 | 17.9ms | 41 | 770ms | 76.81mb | 76.81mb | 170.69mb | 170.69mb |
| ParallelGC | OOM | | | | | | | | | | |
| CMS GC | 4572 | 21 | 27.1ms | 1 | 50ms | n/a | 630ms | 153.56 mb | 153.56 mb | 341.38 mb | 319.08 mb |
| G1 GC | OOM | | | | | | | | | | |


512m

| | 生成对象次数 | YGC | YGCT | FGC | FGCT | Pause Count | Pause total time | YG Allocated memory | YG peak | OG Allocated memory | OG peak |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| SerialGC | 8577 | 14 | 18.6ms | 7 | 48.6ms | 21 | 600ms | 153.56mb | 153.56mb | 341.38mb | 327.93mb |
| ParallelGC | 7853 | 28 | 9.64ms | 9 | 37.8ms | 37 | 610ms | 160 mb | 159.99mb | 341.5 mb | 323.71 mb |
| CMS GC | 7388 | 21 | 27.1 | 1 | 50.0ms | n/a | 630ms | 153.56 mb | 153.56 mb | 341.38 mb | 319.08 mb |
| G1 GC | 9126 | 27 | 5.93ms | n/a | n/a | n/a | 400ms | 143 mb | 137 mb | 487 mb | 419.3 mb |


1g

| | 生成对象次数 | YGC | YGCT | FGC | FGCT | Pause Count | Pause total time | YG Allocated memory | YG peak | OG Allocated memory | OG peak |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| SerialGC | 8659 | 16 | 22.5ms | 3 | 66.7ms | 19 | 560ms | 234.69 mb | 234.69 mb | 521.45 mb | 431.87 mb |
| ParallelGC | 9541 | 19 | 17.4 | 4 | 42.5ms | 23 | 500ms | 266.5 mb | 266.5 mb | 509.5 mb | 409.93 mb |
| CMS GC | 8230 | 30 | 17.0ms | n/a | n/a | n/a | 510ms | 76.81 mb | 76.81 mb | 675.06 mb | 649.66 mb |
| G1 GC | 10330 | 11 | 10.0ms | n/a | n/a | n/a | 170ms | 358 mb | 329 mb | 696 mb | 538.4 mb |


2g

| | 生成对象次数 | YGC | YGCT | FGC | FGCT | Pause Count | Pause total time | YG Allocated memory | YG peak | OG Allocated memory | OG peak |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| SerialGC | 9267 | 16 | 22.5ms | 4 | 67.5ms | 20 | 630ms | 222 mb | 222 mb | 513.36 mb | 513.33 mb |
| ParallelGC | 10315 | 11 | 33.6ms | 4 | 32.5ms | 15 | 500ms | 545 mb | 529.42 mb | 594.5 mb | 492.34 mb |
| CMS GC | 9116 | 35 | 16.6ms | 0 | n/a | n/a | 580ms | 76.81 mb | 76.81 mb | 798.66 mb | 776.31 mb |
| G1 GC | 8755 | 10 | 15.0ms | n/a | n/a | 10 | 150ms | 942 mb | 268 mb | 1.26 gb | 402.1 mb |


4g

| | 生成对象次数 | YGC | YGCT | FGC | FGCT | Pause Count | Pause total time | YG Allocated memory | YG peak | OG Allocated memory | OG peak |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| SerialGC | 8675 | 16 | 22.5ms | 3 | 63.3ms | 19 | 550ms | 234.88 mb | 234.87 mb | 521.83 mb | 426.62 mb |
| ParallelGC | 9747 | 9 | 63.3ms | 3 | 30ms | 12 | 660ms | 947 mb | 737.5 mb | 515 mb | 321.86 mb |
| CMS GC | 9360 | 35 | 17.1ms | 0 | n/a | n/a | 600ms | 76.81 mb | 76.81 mb | 781.33 mb | 756 mb |
| G1 GC | 11 | 16.4ms | n/a | n/a | 11 | 180ms | 415 mb | 398 mb | 2.81 gb | 487.2 mb |


2 使用压测工具(wrk或sb)，演练gateway-server-0.0.1-SNAPSHOT.jar示例。 


3 (选做)如果自己本地有可以运行的项目，可以按照2的方式进行演练。


4 (必做)根据上述自己对于1和2的演示，写一段对于不同GC和堆内存的总结，提交到 github。

> 1) GC 256m: 由于堆内存太小，后面的时间基本全是在做Full GC
> 2) 相同堆大小的情况下，平均Young GC的时间: Serial GC > Parallel GC > CMS GC > G1 GC
> 3) 由于程序比较简单，且运行时间比较短，程序吞吐量基本跟GC时间成反比
> 4) 堆越大，分配给Young区的空间越大，YGC的频率越小，但是YGC STW停顿时间越长


## 第四节课作业

1、(可选)运行课上的例子，以及 Netty 的例子，分析相关现象。

2、(必做)写一段代码，使用 HttpClient 或 OkHttp 访问 http://localhost:8801，代 码提交到Github。

> 目录： class4_q2

