package com.tiger.easyrpc;

import com.tiger.easyrpc.core.annotation.EnableEasyrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEasyrpc
@SpringBootApplication
public class EasyrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyrpcApplication.class, args);
    }

}
