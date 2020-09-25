package com.tiger.easyrpc.demo.consumer;

import com.tiger.easyrpc.core.annotation.Fetcher;
import com.tiger.easyrpc.demo.interfaces.ITest;
import org.springframework.stereotype.Component;

@Component
public class ConsumerTest {
    @Fetcher(url = "127.0.0.1:16388")
    private ITest iTest;

    public void test(){
        String a = iTest.test("a");
        System.out.println(a);
    }
}
