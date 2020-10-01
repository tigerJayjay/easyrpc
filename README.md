# easyrpc  

#### 介绍  

简单易用的轻量级rpc框架，支持万级并发，快速便捷，持续优化中。

#### 软件架构  

基于SpringBoot，使用Netty进行通讯，使用Protobuff进行序列化。

#### 使用说明  

1.在SpringBoot启动类添加@EnableEasyrpc，默认扫描启动类所在包及以下的包；  
2.通过在服务类上使用@Exporter注解可直接暴露服务；  
3.通过@Fetcher注解可直接远程引入服务到接口属性，直接调用接口属性像在本地一样使用远程服务。

#### 注意事项

1.服务端必须实现接口，客户端必须通过该接口进行远程调用；  
2.@Fetcher注解必须加在受Spring管理的Bean的属性上，才能引入远程服务对象；
3.服务端在application.properties中使用easyrpc.server.port=xxx属性，指定服务暴露端口；
4.客户端使用easyrpc.client.remoteUrl=127.0.0.1:8888,127.0.0.1:8889属性，指定全局服务引用地址，也可以通过@Fetcher注解的url属性指定，后者会覆盖前者，并且如果指定多个地址，会随机选取一个连接远程服务。

#### application.properties配置  

服务端:  
easyrpc.server.port:指定服务暴露端口  
客户端:  
easyrpc.client.remoteUrl:指定远程服务地址,格式(ip1:port1,ip2:port2)  
easyrpc.client.service.version:指定远程服务版本号  
easyrpc.client.service.group:指定远程服务分组  
  
  


