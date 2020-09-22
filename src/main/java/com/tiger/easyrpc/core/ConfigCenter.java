package com.tiger.easyrpc.core;


import java.util.LinkedList;
import java.util.List;

public class ConfigCenter implements Closable{
    private List<Config> configs = new LinkedList<Config>();


    public void addConfig(Config config){
        configs.add(config);
    }

    public void close() {
        for(Config config:configs){
            config.close();
            config = null;
        }
    }
}
