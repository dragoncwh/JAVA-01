# 学习笔记

## 第一节课作业

1 (可选)、自己写一个简单的 Hello.java，里面需要涉及基本类型，四则运行，if 和
for，然后自己分析一下对应的字节码，有问题群里讨论。


2 (必做)、自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法， 此文件内容是一个 Hello.class 文件所有字节(x=255-x)处理后的文件。文件群里提供。
作业目录：Q2


3 (必做)、画一张图，展示 Xmx、Xms、Xmn、Metaspache、DirectMemory、Xss 这些内存参数的关系。
作业目录：Q3


4 (可选)、检查一下自己维护的业务系统的 JVM 参数配置，用 jstat 和 jstack、jmap 查看一下详情，并且自己独立分析一下大概情况，思考有没有不合理的地方，如何改进。


## 第二节课作业

1、本机使用 G1 GC 启动一个程序，仿照课上案例分析一下 JVM 情况
可以使用gateway-server-0.0.1-SNAPSHOT.jar 
注意关闭自适应参数:-XX:-UseAdaptiveSizePolicy

> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseSerialGC -jar target/gateway-server- 0.0.1-SNAPSHOT.jar
> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseParallelGC -jar target/gateway-server- 0.0.1-SNAPSHOT.jar
> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseConcMarkSweepGC -jar target/gateway-server-0.0.1-SNAPSHOT.jar
> java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -jar target/gateway-server-0.0.1-SNAPSHOT.jar

使用jmap，jstat，jstack，以及可视化工具，查看jvm情况。 mac上可以用wrk，windows上可以按照superbenchmark压测 http://localhost:8088/api/hello 查看jvm。