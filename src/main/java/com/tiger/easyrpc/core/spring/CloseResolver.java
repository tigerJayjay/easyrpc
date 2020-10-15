package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.Closable;
import com.tiger.easyrpc.core.util.PathResolverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * 扫描实现Closable接口的类，并放入ApplicationCloseManager
 */
public class CloseResolver implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(CloseResolver.class);
    private final String scanPack = "com.tiger.easyrpc.core.close";
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        List<Class> classes = PathResolverUtils.resolverByInterface(scanPack, Closable.class);
        classes.forEach(item->{
            Object o = null;
            try {
                o = item.newInstance();
            } catch (InstantiationException  | IllegalAccessException e) {
                logger.error("解析关闭实例对象异常！",e);
            }
            if(o instanceof Closable){
                Closable closable = (Closable)o;
                ApplicationCloseManager.getInstance().regist(closable);
            }
        });
    }
}
