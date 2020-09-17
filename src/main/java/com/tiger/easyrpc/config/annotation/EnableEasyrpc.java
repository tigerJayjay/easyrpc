package com.tiger.easyrpc.config.annotation;

import com.tiger.easyrpc.config.spring.EasyrpcComponentRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(EasyrpcComponentRegistrar.class)
public @interface EnableEasyrpc {
}
