package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.Closable;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 关闭接口实现管理器，当服务停止，执行所有关闭对象，用来释放资源或通知
 */
public class ApplicationCloseManager  {
    private static List<Closable> close = new ArrayList<Closable>();
    private static ApplicationCloseManager manager = new ApplicationCloseManager();


    private ApplicationCloseManager(){}
    public static ApplicationCloseManager getInstance(){
        return manager;
    }
    public void regist(Closable closable){
        close.add(closable);
    }
    public List<Closable> getClose(){
        return Collections.unmodifiableList(close);
    }
    static class EasyrpcServerCloseListener implements ApplicationListener<ContextClosedEvent>{
        @Override
        public void onApplicationEvent(ContextClosedEvent contextStoppedEvent) {
            close.forEach(item->{
                item.close();
            });
        }
    }
}
