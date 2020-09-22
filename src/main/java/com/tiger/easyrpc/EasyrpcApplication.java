package com.tiger.easyrpc;

import com.tiger.easyrpc.config.annotation.EnableEasyrpc;
import com.tiger.easyrpc.demo.interfaces.ITest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@EnableEasyrpc
@SpringBootApplication
public class EasyrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyrpcApplication.class, args);
    }

}
