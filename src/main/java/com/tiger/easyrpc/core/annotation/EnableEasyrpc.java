package com.tiger.easyrpc.core.annotation;

import com.tiger.easyrpc.core.spring.EasyrpcComponentRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(EasyrpcComponentRegistrar.class)
public @interface EnableEasyrpc {
}
