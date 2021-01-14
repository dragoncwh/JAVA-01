# 学习笔记

## 第一节课作业

1 (可选)、自己写一个简单的 Hello.java，里面需要涉及基本类型，四则运行，if 和
for，然后自己分析一下对应的字节码，有问题群里讨论。


2 (必做)、自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法， 此文件内容是一个 Hello.class 文件所有字节(x=255-x)处理后的文件。文件群里提供。
> 作业目录：Q2


3 (必做)、画一张图，展示 Xmx、Xms、Xmn、Metaspache、DirectMemory、Xss 这些内存参数的关系。
> 作业目录：Q3


4 (可选)、检查一下自己维护的业务系统的 JVM 参数配置，用 jstat 和 jstack、jmap 查看一下详情，并且自己独立分析一下大概情况，思考有没有不合理的地方，如何改进。


## 第二节课作业

1、本机使用 G1 GC 启动一个程序，仿照课上案例分析一下 JVM 情况
可以使用gateway-server-0.0.1-SNAPSHOT.jar 
注意关闭自适应参数:-XX:-UseAdaptiveSizePolicy

> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseSerialGC -jar target/gateway-server-0.0.1-SNAPSHOT.jar

> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseParallelGC -jar target/gateway-server-0.0.1-SNAPSHOT.jar

> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseConcMarkSweepGC -jar target/gateway-server-0.0.1-SNAPSHOT.jar

> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -jar target/gateway-server-0.0.1-SNAPSHOT.jar

使用jmap，jstat，jstack，以及可视化工具，查看jvm情况。 mac上可以用wrk，windows上可以按照superbenchmark压测 http://localhost:8088/api/hello 查看jvm。

（以下环境均为AdoptOpenJDK 11.0.7, macOS Catalina 10.15.7, 内存16GB, CPU i7 Quad-Core 2.8G）

### 1.1 使用-XX:+UseSerialGC

> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseSerialGC -jar gateway-server-0.0.1-SNAPSHOT.jar

#### 1.1.1 jmap

常用命令：
```
-heap 打印堆内存/内存池的配置和使用信息
-histo 类占用空间的直方图
-dump:format=b,file=xxx.hprof Dump内存
由于jdk9以后-heap被移除了，所以用jhsdb jmap --heap --pid XXX
```


```bash
$ jhsdb jmap --heap --pid 26681

Attaching to process ID 26681, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 11.0.2+9

using thread-local object allocation.
Mark Sweep Compact GC

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1073741824 (1024.0MB)		# 最大堆空间，默认情况下，如果内存<1GB，则为内存1/2，否则1/4
   NewSize                  = 357892096 (341.3125MB) 	# 新生代的大小，默认为堆的1/3
   MaxNewSize               = 357892096 (341.3125MB) 	# 最大新生代的大小
   OldSize                  = 715849728 (682.6875MB) 	# 老年代大小
   NewRatio                 = 2							# 老年代:新生代 = 2:1
   SurvivorRatio            = 8 						# Eden:S0:S1 = 8:1:1
   MetaspaceSize            = 21807104 (20.796875MB)	
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB 		# 最大Metaspace，由于Metaspace直接跟OS申请内存，所以这个上限很大
   G1HeapRegionSize         = 0 (0.0MB)					# 使用G1时，堆会分成很多个Region

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 322109440 (307.1875MB)
   used     = 181033144 (172.64665985107422MB)
   free     = 141076296 (134.54084014892578MB)
   56.20237146728764% used
Eden Space:
   capacity = 286326784 (273.0625MB)
   used     = 181033144 (172.64665985107422MB)
   free     = 105293640 (100.41584014892578MB)
   63.226059913416975% used
From Space:
   capacity = 35782656 (34.125MB)
   used     = 0 (0.0MB)
   free     = 35782656 (34.125MB)
   0.0% used
To Space:
   capacity = 35782656 (34.125MB)
   used     = 0 (0.0MB)
   free     = 35782656 (34.125MB)
   0.0% used
tenured generation:
   capacity = 715849728 (682.6875MB)
   used     = 11488528 (10.956314086914062MB)
   free     = 704361200 (671.7311859130859MB)
   1.604879844279273% used

```

#### 1.1.2 jstat
```bash
$ jstat -gc 26681 1s 10	# 每秒钟采样一次堆内存信息，总共10次

 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT    CGC    CGCT     GCT   
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 176790.2  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115

```

- Timestamp： JVM启动时间
- S0C/S0U/S1C/S1U: S0/S1 Capacity/Usage
- EC/EU: Eden Capacity/Usage
- OC/OU: Old generation capacity/usage
- MC/MU: Metaspace capacity/usage
- CCSC/CCSU: Compressed class space capacity/usage
- YGC/YGCT: Young GC count/time
- FGC/FGCT: Full GC count/time
- CGC/CGCT: Concurrent GC count/time
- GCT: GC time


#### 1.1.3 jstack
dump线程信息
常用命令
```
-F 强制执行thread dump，可在Java进程卡死时使用，可能需要系统权限
-m 混合模式（mixed mode），将Java帧和native帧一起输出，可能需要系统权限
-l 长列表模式，将线程相关的locks信息一起输出
```

#### 1.1.4 jcmd
```
jcmd pid help
```

#### 1.1.5 使用wrk后，观察堆的使用情况
```
$ wrk -t12 -c20 -d30s http://localhost:8088/api/hello 		# 使用12个线程，20个线程进行压测30秒

$ jstat -gc 26681 1s 20
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT    CGC    CGCT     GCT   
34944.0 34944.0  0.0    0.0   279616.0 185178.7  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 185178.7  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 185178.7  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0    0.0   279616.0 196365.8  699072.0   11219.3   35328.0 34349.4 4352.0 4026.2      0    0.000   2      0.115   -          -    0.115
34944.0 34944.0  0.0   10021.0 279616.0   0.0     699072.0   11219.3   44160.0 42672.4 5504.0 4982.6      1    0.324   2      0.115   -          -    0.440
34944.0 34944.0  0.0   10021.0 279616.0 154838.5  699072.0   11219.3   44160.0 42672.4 5504.0 4982.6      1    0.324   2      0.115   -          -    0.440
34944.0 34944.0 8360.6  0.0   279616.0 171894.6  699072.0   11219.3   44416.0 43097.8 5504.0 4983.6      2    0.340   2      0.115   -          -    0.455
34944.0 34944.0 8540.2  0.0   279616.0 51074.2   699072.0   11219.3   44928.0 43554.5 5504.0 4983.6      4    0.362   2      0.115   -          -    0.477
34944.0 34944.0 8399.4  0.0   279616.0 166267.5  699072.0   11219.3   45184.0 43628.4 5504.0 4986.6      6    0.382   2      0.115   -          -    0.497
34944.0 34944.0  0.0   8412.7 279616.0   0.0     699072.0   11219.3   45184.0 43642.7 5504.0 4986.6      9    0.413   2      0.115   -          -    0.528
34944.0 34944.0  0.0   8412.4 279616.0 140494.0  699072.0   11219.3   45184.0 43647.0 5504.0 4986.6     11    0.434   2      0.115   -          -    0.549
34944.0 34944.0 8409.7  0.0   279616.0   0.0     699072.0   11219.3   45184.0 43647.0 5504.0 4986.6     14    0.467   2      0.115   -          -    0.583
34944.0 34944.0 139.2   0.0   279616.0 69820.7   699072.0   19505.9   45184.0 43647.0 5504.0 4986.6     16    0.496   2      0.115   -          -    0.611
34944.0 34944.0  0.0    54.4  279616.0   0.0     699072.0   19569.8   45184.0 43647.5 5504.0 4986.6     19    0.501   2      0.115   -          -    0.616
34944.0 34944.0  0.0    50.9  279616.0 203775.0  699072.0   19573.0   45184.0 43647.5 5504.0 4986.6     21    0.503   2      0.115   -          -    0.619
34944.0 34944.0  66.7   0.0   279616.0 121987.7  699072.0   19573.3   45184.0 43647.5 5504.0 4986.6     24    0.507   2      0.115   -          -    0.622
34944.0 34944.0  0.0    51.3  279616.0   0.0     699072.0   19588.1   45184.0 43647.5 5504.0 4986.6     27    0.510   2      0.115   -          -    0.626
34944.0 34944.0  0.0    55.6  279616.0 201408.7  699072.0   19588.1   45184.0 43647.5 5504.0 4986.6     29    0.513   2      0.115   -          -    0.628
34944.0 34944.0  62.2   0.0   279616.0 233140.2  699072.0   19589.4   45184.0 43650.4 5504.0 4987.1     32    0.516   2      0.115   -          -    0.631
34944.0 34944.0  0.0    57.6  279616.0 152467.3  699072.0   19589.4   45184.0 43650.4 5504.0 4987.1     35    0.520   2      0.115   -          -    0.635
```

### 1.2 -XX:+UseParallelGC

> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseParallelGC -jar gateway-server-0.0.1-SNAPSHOT.jar

#### 1.2.1 jmap

```bash
Attaching to process ID 34864, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 11.0.2+9

using thread-local object allocation.
Parallel GC with 8 thread(s)								# Parallel GC: 8个线程

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1073741824 (1024.0MB)
   NewSize                  = 357564416 (341.0MB)			# different from SerialGC
   MaxNewSize               = 357564416 (341.0MB)
   OldSize                  = 716177408 (683.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 268435456 (256.0MB)
   used     = 182738288 (174.27281188964844MB)
   free     = 85697168 (81.72718811035156MB)
   68.07531714439392% used
From Space:
   capacity = 44564480 (42.5MB)
   used     = 0 (0.0MB)
   free     = 44564480 (42.5MB)
   0.0% used
To Space:
   capacity = 44564480 (42.5MB)
   used     = 0 (0.0MB)
   free     = 44564480 (42.5MB)
   0.0% used
PS Old Generation
   capacity = 716177408 (683.0MB)
   used     = 11099528 (10.585334777832031MB)
   free     = 705077880 (672.414665222168MB)
   1.549829396461498% used

```

### 1.3 -XX:+UseConcMarkSweepGC

> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseConcMarkSweepGC -jar gateway-server-0.0.1-SNAPSHOT.jar

#### 1.3.1 jmap

```bash
Attaching to process ID 35292, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 11.0.2+9

using thread-local object allocation.
Concurrent Mark-Sweep GC 									# Concurrent Mark-Sweep GC

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1073741824 (1024.0MB)
   NewSize                  = 357892096 (341.3125MB)		# different from SerialGC and Parallel GC
   MaxNewSize               = 357892096 (341.3125MB)
   OldSize                  = 715849728 (682.6875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 322109440 (307.1875MB)
   used     = 245387616 (234.01986694335938MB)
   free     = 76721824 (73.16763305664062MB)
   76.18144193476603% used
Eden Space:
   capacity = 286326784 (273.0625MB)
   used     = 236415936 (225.46380615234375MB)
   free     = 49910848 (47.59869384765625MB)
   82.56857171978713% used
From Space:
   capacity = 35782656 (34.125MB)
   used     = 8971680 (8.556060791015625MB)
   free     = 26810976 (25.568939208984375MB)
   25.072705614697803% used
To Space:
   capacity = 35782656 (34.125MB)
   used     = 0 (0.0MB)
   free     = 35782656 (34.125MB)
   0.0% used
concurrent mark-sweep generation:
   capacity = 715849728 (682.6875MB)
   used     = 0 (0.0MB)
   free     = 715849728 (682.6875MB)
   0.0% used

```

### 1.4 -XX:+UseG1GC

> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -jar gateway-server-0.0.1-SNAPSHOT.jar

#### 1.4.1 jmap

```bash
Attaching to process ID 35613, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 11.0.2+9

using thread-local object allocation.
Garbage-First (G1) GC with 8 thread(s) 								# G1 with 8 threads

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1073741824 (1024.0MB)
   NewSize                  = 1363144 (1.2999954223632812MB)
   MaxNewSize               = 643825664 (614.0MB)
   OldSize                  = 5452592 (5.1999969482421875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 1048576 (1.0MB)						# not 0

Heap Usage:
G1 Heap:
   regions  = 1024
   capacity = 1073741824 (1024.0MB)
   used     = 86328832 (82.32958984375MB)
   free     = 987412992 (941.67041015625MB)
   8.039999008178711% used
G1 Young Generation:
Eden Space:
   regions  = 65
   capacity = 85983232 (82.0MB)
   used     = 68157440 (65.0MB)
   free     = 17825792 (17.0MB)
   79.26829268292683% used
Survivor Space:
   regions  = 9
   capacity = 9437184 (9.0MB)
   used     = 9437184 (9.0MB)
   free     = 0 (0.0MB)
   100.0% used
G1 Old Generation:
   regions  = 9
   capacity = 978321408 (933.0MB)
   used     = 8734208 (8.32958984375MB)
   free     = 969587200 (924.67041015625MB)
   0.8927749028670954% used

```

