package com.tiger.easyrpc.core.annotation;

import com.tiger.easyrpc.core.spring.EasyrpcComponentRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启Easyrpc功能，放到Springboot启动类上
 * enableClient：是否开启客户端功能
 * enableServer: 是否开启服务端功能
 * 如果开启自动配置easyrpc.auto=true，则无需配置enableClient和enableServer，
 * 在配置文件中配置easyrpc.server.enable或easyrpc.server.enable即可
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(EasyrpcComponentRegistrar.class)
public @interface EnableEasyrpc {
    boolean enableClient() default false;
    boolean enableServer() default false;
}
