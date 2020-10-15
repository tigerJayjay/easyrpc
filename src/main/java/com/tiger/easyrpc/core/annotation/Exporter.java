package com.tiger.easyrpc.core.annotation;

import java.lang.annotation.*;

/**
 * 放到需要暴露的服务实现类上
 * version:服务版本
 * group:服务分组
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Exporter {
    String version() default "";
    String group() default "";
}
