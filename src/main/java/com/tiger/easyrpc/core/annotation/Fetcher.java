package com.tiger.easyrpc.core.annotation;

import java.lang.annotation.*;

/**
 * 远程引入服务注解
 * url：远程服务地址 ip1:port1,ip2,port2
 * versin: 远程服务版本
 * group: 远程服务分组
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Fetcher {
    String url() default "";
    String version() default "";
    String group() default "";
}
