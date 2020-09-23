package com.tiger.easyrpc;

import com.tiger.easyrpc.core.annotation.EnableEasyrpc;
import com.tiger.easyrpc.demo.consumer.ConsumerTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@EnableEasyrpc
@SpringBootApplication
public class EasyrpcApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(EasyrpcApplication.class, args);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ConsumerTest bean = run.getBean(ConsumerTest.class);
                bean.test();
            }
        }).start();
    }
}
