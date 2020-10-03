# easyrpc  

### 1.1.介绍  

简单易用的轻量级rpc框架，支持万级并发，快速便捷，持续优化中。

### 1.2.软件架构  

基于SpringBoot，使用Netty进行通讯，使用Protobuff进行序列化。

### 1.3.使用说明  

1.在SpringBoot启动类添加@EnableEasyrpc，默认扫描启动类所在包及以下的包；  
2.通过在服务类上使用@Exporter注解可直接暴露服务；  
3.通过@Fetcher注解可直接远程引入服务到接口属性，直接调用接口属性像在本地一样使用远程服务。

### 1.4.注意事项

1.服务端必须实现接口，客户端必须通过该接口进行远程调用；  
2.@Fetcher注解必须加在受Spring管理的Bean的属性上，才能引入远程服务对象；
3.服务端在application.properties中使用easyrpc.server.port=xxx属性，指定服务暴露端口；
4.客户端使用easyrpc.client.remoteUrl=127.0.0.1:8888,127.0.0.1:8889属性，指定全局服务引用地址，也可以通过@Fetcher注解的url属性指定，后者会覆盖前者，并且如果指定多个地址，会随机选取一个连接远程服务。

### 1.5.application.properties配置  

#### 1.5.1.服务端配置  
easyrpc.server.port:指定服务暴露端口  
easyrpc.server.service.version:指定远程服务版本号  
easyrpc.server.service.group:指定远程服务分组  

#### 1.5.2.客户端配置 
easyrpc.client.remoteUrl:指定远程服务地址,格式(ip1:port1,ip2:port2)  
easyrpc.client.service.version:指定远程服务版本号  
easyrpc.client.service.group:指定远程服务分组  
easyrpc.client.rpcTimeout:远程调用超时时间,默认5000毫秒
  
### 1.6
### 1.6.待更新特性  
1.扩展点机制  
2.支持注册中心  
  
  


