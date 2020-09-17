package com.tiger.easyrpc.config.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface ExporterScan {
    String packages() default "./";
    Class[] classes() default {};
    String[] classStrs() default {};
}
