package com.tiger.easyrpc.core.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Exporter {
    String version() default "";
    String group() default "";
}
