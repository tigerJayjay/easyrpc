package com.tiger.easyrpc.core.urlstrategy;

import org.junit.Assert;
import org.junit.Test;

public class RandomStrategyTest {
    @Test
    public void select(){
        RandomStrategy randomStrategy = new RandomStrategy();
        String[] urls = new String[]{"1","2"};
        String select = randomStrategy.select(urls);
        System.out.println(select);
        Assert.assertNotNull(select);
    }
}
