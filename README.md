# [EasyRpc](https://mdui.org)

## 1.1.介绍 

简单易用的轻量级rpc框架，快速便捷，持续优化中。

## 1.2.软件架构  

### 1.2.1.描述  
基于SpringBoot，使用Netty进行通讯，使用Protobuff进行序列化。
### 1.2.2.架构图
![输入图片说明](https://images.gitee.com/uploads/images/2020/1109/220354_6fd236a1_1738997.png "20201109.png")
### 1.2.3.特性  
- 支持服务单机或集群多版本多分组发布及客户端多分组多版本注入，支持全局和注解配置，注入服务更灵活    
- 服务端和客户端心跳保活及自动重连，收发消息既稳定又及时   
- 支持服务自动发现及服务注册  
- 不可用服务投票机制，客户端与服务端连接断开，开始发起投票，如果在规定时间内，存在任何一个客户端还和服务端正常连接， 
表示连接可能由于网络波动断开，此时继续进行重连操作；如果规定时间内，可用投票数为0，表示服务端已经不可用，则从注册中心移除该服务，
并从客户端缓存中移除该服务端对应的客户端对象，等服务恢复，会在下次服务调用时，重新生成。如果对该服务投票期间(默认10秒）， 
另一个客户端也检测到该服务不可用，那么不会重新对该服务发起投票，只会获取最终投票结果（不保证一定能获取到上一轮投票结果），
来决定是否重连或者删除本地客户端对象。

## 1.3.使用说明  

1. 在SpringBoot启动类添加@EnableEasyrpc，默认扫描启动类所在包及以下的包；  
2. 通过在服务类上使用@Exporter注解可直接暴露服务；  
3. 通过@Fetcher注解可直接远程引入服务到接口属性，直接调用接口属性像在本地一样使用远程服务。  
4. jdk1.8+
## 1.4.注意事项

- 服务端必须实现接口，客户端必须通过该接口进行远程调用；  
- @Fetcher注解必须加在受Spring管理的Bean的属性上，才能引入远程服务对象；  
- 服务端在application.properties中使用easyrpc.server.port=xxx属性，指定服务暴露端口；  
- 客户端使用easyrpc.client.remoteUrl=127.0.0.1:8888,127.0.0.1:8889属性，指定全局服务引用地址，也可以通过@Fetcher注解的url属性指定，后者会覆盖前者，并且如果指定多个地址，会随机选取一个连接远程服务。如果使用注册中心，remoteUrl使用本机真实ip，不能使用localhost或127.0.0.1  

## 1.5.配置  

### 1.5.1.服务端配置  
- easyrpc.server.enable:是否开启服务端功能，默认false 
- easyrpc.server.port:指定服务暴露端口  
- easyrpc.server.service.version:指定远程服务版本号  
- easyrpc.server.service.group:指定远程服务分组  

### 1.5.2.客户端配置 
- easyrpc.client.enable:是否开启客户端功能，默认false
- easyrpc.client.remoteUrl:指定远程服务地址,格式(ip1:port1,ip2:port2)  
- easyrpc.client.service.version:指定远程服务版本号  
- easyrpc.client.service.group:指定远程服务分组  
- easyrpc.client.service.scan:将指定包下的服务接口生成代理bean，受spring管理，可以通过spring属性注入方式引入远程服务，
如果设置此属性，可以无需使用@Fetcher注解，该属性需要在全局设置version和group
- easyrpc.client.rpcTimeout:远程调用超时时间,默认5000毫秒  
- easyrpc.client.retryInterval:客户端重连间隔，默认5秒   
- easyrpc.client.retryCount:客户端重连次数，默认不限次数  

### 1.5.3.注册中心配置
Easyrpc基于redis作为注册中心，目前支持单机及哨兵模式。 
- easyrpc.registry.enable:是否开启注册中心支持，默认false  
- easyrpc.registry.voteWait:投票等待结果时间，默认10秒   
- easyrpc.registry.voteInterval:投票间隔时间，默认30秒  
- easyrpc.registry.redis.host:redis ip地址
- easyrpc.registry.redis.port:redis端口  
- easyrpc.registry.redis.password:redis密码   
- easyrpc.registry.redis.timeout:redis连接超时时间，单位毫秒    
- easyrpc.registry.redis.pool.xxx:支持redis.clients.jedis.JedisPoolConfig类的所有属性 
- easyrpc.registry.redis.mode:redis模式，包括single(单机模式)，sentinel（哨兵模式），默认单机模式
- easyrpc.registry.redis.masterName：redis哨兵配置中的<master-name>
- easyrpc.registry.redis.sentinelUrl:redis哨兵地址，格式<url_1:port_1,url_2:port_2>，例：127.0.0.1:57003,127.0.0.1:57004

### 1.5.4.全局配置
- easyrpc.auto:是否开启自动配置，默认为true  
- easyrpc.net.host:指定本机地址，此参数值优先级比hostPre高，如果未指定，则遵守hostPre配置规则    
- easyrpc.net.hostPre:指定符合前缀的第一个ip作为本机地址(多网卡），如果未获取到sitelocal类型的ip，则从linklocal类型获取，如果仍
未获取到，则返回loopback地址  
## 1.6.待更新特性  
- 扩展点机制  
- 接口调用数据视图  
  
## 1.7.使用示例
- 使用示例请clone仓库easyrpcserver和easyrpcclient

## 1.8.相关实现说明
### 1.8.1.分布式锁
- 由于存在多个线程同时使用注册中心进行服务更新，为了保证服务更新操作的原子性，所以使用分布式锁，
锁过期时间为30秒，为了防止更新操作时间过长导致的在此期间分布式锁失效，所以引入监视机制，每10秒更新一次
锁存活时间。
- 目前在哨兵模式下不保证分布式锁一定能有效，在主节点突然宕机的情况下，如果此时分布式锁还未复制到从节点，
从节点被选举为主节点之后，分布式锁可能会被其他客户端获取到，此时服务地址更新可能会失效  
### 1.8.2.服务下线投票机制
- 如果某个服务不可用，导致与客户端的断连（包含服务端宕机及偶发的网络不通），此时客户端会检测到断连通知，
立刻将与此服务端连接的客户端对象标记为断连状态，并在30秒内发起服务不可用投票（投票机制介绍在1.2.3），如果
投票结果为服务不可用，那么便直接从注册中心移除服务。
### 1.8.3.消息发送和接收
- 在easyrpc中，因为所有发送线程共用netty的Channel进行数据发送和接收，所以需要区分消息的来源，以便于将服务端
的响应消息正确传送回对应的线程，所以在easyrpc中抽象出一个Channel接口（非Netty中的Channel，下面说的都是此Channel）
来收发数据，每次发送当前线程都创建一个新的Channel对象，调用发送接口，然后选择Netty客户端发送消息，消息对象中设置了一个
消息标识，此标识全局唯一，并且与Channel对象关联，当客户端发送完消息，会返回一个ResultFuture来监视消息的状态，
通过ResultFuture获取返回结果，如果结果未返回，会在规定时间内阻塞当前线程。此当服务端响应消息时，会将发送消息的标识也返回，
然后通过消息标识获取Channel对象并设置返回结果，此时会唤醒之前阻塞的线程并获取结果，然后执行之后的消息处理逻辑。
### 1.8.4.消息大小
- 心跳消息在客户端空闲时10秒发送一次，大小为6个字节
- 可设置springboot日志级别为TRACE来查看实际调用服务的消息大小