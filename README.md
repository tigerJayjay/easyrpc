# [EasyRpc](https://mdui.org)

## 1.1.介绍 

简单易用的轻量级rpc框架，支持并发，快速便捷，持续优化中。

## 1.2.软件架构  

### 1.2.1.描述  
基于SpringBoot，使用Netty进行通讯，使用Protobuff进行序列化。
### 1.2.2.架构图
![输入图片说明](https://images.gitee.com/uploads/images/2020/1028/232925_5b4108af_1738997.png "20201028.png")
### 1.2.3.特性  
1.支持服务单机或集群多版本多分组发布及客户端多分组多版本注入，支持全局和注解配置，注入服务更灵活    
2.服务端和客户端心跳保活及自动重连，收发消息既稳定又及时   
3.支持服务自动发现及服务注册  
4.不可用服务投票机制，客户端与服务端连接断开，开始发起投票，如果在规定时间内，存在任何一个客户端还和服务端正常连接，  
  表示连接可能由于网络波动断开，此时进行重连；如果规定时间内，可用投票数为0，表示服务端已经不可用，则从注册中心移除该  
  服务，并从客户端缓存中移除该服务端对应的客户端对象，等服务恢复，会在下次服务调用时，重新生成。
## 1.3.使用说明  

1.在SpringBoot启动类添加@EnableEasyrpc，默认扫描启动类所在包及以下的包；  
2.通过在服务类上使用@Exporter注解可直接暴露服务；  
3.通过@Fetcher注解可直接远程引入服务到接口属性，直接调用接口属性像在本地一样使用远程服务。  
4.jdk1.8+
## 1.4.注意事项

1.服务端必须实现接口，客户端必须通过该接口进行远程调用；  
2.@Fetcher注解必须加在受Spring管理的Bean的属性上，才能引入远程服务对象；  
3.服务端在application.properties中使用easyrpc.server.port=xxx属性，指定服务暴露端口；  
4.客户端使用easyrpc.client.remoteUrl=127.0.0.1:8888,127.0.0.1:8889属性，指定全局服务引用地址，也可以通过@Fetcher注解的url属性指定，后者会覆盖前者，并且如果指定多个地址，会随机选取一个连接远程服务。如果使用注册中心，remoteUrl使用本机真实ip，不能使用localhost或127.0.0.1  

## 1.5.application.properties配置  

### 1.5.1.服务端配置  
easyrpc.server.enable:是否开启服务端功能，默认false
easyrpc.server.port:指定服务暴露端口  
easyrpc.server.service.version:指定远程服务版本号  
easyrpc.server.service.group:指定远程服务分组  

### 1.5.2.客户端配置 
easyrpc.client.enable:是否开启客户端功能，默认false
easyrpc.client.remoteUrl:指定远程服务地址,格式(ip1:port1,ip2:port2)  
easyrpc.client.service.version:指定远程服务版本号  
easyrpc.client.service.group:指定远程服务分组  
easyrpc.client.rpcTimeout:远程调用超时时间,默认5000毫秒

### 1.5.3.注册中心配置
Easyrpc基于redis作为注册中心，目前只支持单机，后续添加集群，主从及哨兵支持。  
easyrpc.registry.redis.host:redis ip地址
easyrpc.registry.redis.port:redis端口  
easyrpc.registry.redis.password:redis密码   
easyrpc.registry.redis.timeout:redis连接超时时间，单位毫秒    
easyrpc.registry.redis.pool.*:支持redispool所有配置    

### 1.5.4.全局配置
easyrpc.auto:是否开启自动配置，默认为true
## 1.6.待更新特性  
1.扩展点机制  
3.接口调用数据视图  
  
## 1.7.使用示例
使用示例请clone仓库easyrpcserver和easyrpcclient

