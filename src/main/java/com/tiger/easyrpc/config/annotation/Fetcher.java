package com.tiger.easyrpc.config.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Fetcher {
    String[] url();
    String name() default "";
}
