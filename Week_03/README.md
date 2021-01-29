# 学习笔记

## 第五节课作业

1、按今天的课程要求，实现一个网关，
基础代码可以 fork:https://github.com/kimmking/JavaCourseCodes 02nio/nio02 文件夹下
实现以后，代码提交到 Github。 
1)周三作业:(必做)整合你上次作业的httpclient/okhttp; 

> 路径: gateway-w3/
> 入口main函数位于NettyGatewayApp.java


2)周三作业(可选):使用netty实现后端http访问(代替上一步骤); 

> 将inbound.HttpInboundHandler里的this.handler = new OkhttpOutboundHandler(proxyServer);改为this.handler = new NettyHttpClientOutboundHandler(proxyServer);

3)周日作业:(必做)实现过滤器 ~


4)周日作业(可选):实现路由


## 第六节课作业

1、(可选)跑一跑课上的各个例子，加深对多线程的理解 

2、(可选)完善网关的例子，试着调整其中的线程池参数