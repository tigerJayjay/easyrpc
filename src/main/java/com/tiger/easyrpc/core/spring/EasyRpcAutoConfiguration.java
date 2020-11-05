package com.tiger.easyrpc.core.spring;


import com.tiger.easyrpc.common.PropertiesUtils;
import com.tiger.easyrpc.core.ApplicationConfig;
import com.tiger.easyrpc.core.ConsumerConfig;
import com.tiger.easyrpc.core.ProviderConfig;
import com.tiger.easyrpc.core.RegistryConfig;
import com.tiger.easyrpc.core.util.SpringBeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Easyrpc自动配置类
 */
@Configuration(
        proxyBeanMethods = false
)
@EnableConfigurationProperties
@ConditionalOnProperty(
        prefix = "easyrpc",
        name = {"auto"},
        havingValue = "true",
        matchIfMissing = true
)
public class EasyRpcAutoConfiguration {

    @Bean
    public PropertiesUtils propertiesUtils(){
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        return propertiesUtils;
    }

    @Bean
    public SpringBeanUtils springBeanUtils(){
        SpringBeanUtils springBeanUtils = new SpringBeanUtils();
        return springBeanUtils;
    }

    @Bean
    public ApplicationConfig applicationConfig(){
        return new ApplicationConfig();
    }

    @Configuration(
            proxyBeanMethods = false
    )
    @ConditionalOnProperty(
            prefix = "easyrpc",
            name = {"server.enable"},
            havingValue = "true"
    )
    static class ServerAutoConfiguration{
        @Bean
        @ConditionalOnMissingBean(ExporterResolver.class)
        public ExporterResolver exporterResolver(){
            ExporterResolver exporterResolver = new ExporterResolver();
            return  exporterResolver;
        }


        @Bean
        public ProviderConfig getProviderConfig(){
            ProviderConfig providerConfig = new ProviderConfig();
            return providerConfig;
        }

    }

    @Configuration(
            proxyBeanMethods = false
    )
    @ConditionalOnProperty(
            prefix = "easyrpc",
            name = {"client.enable"},
            havingValue = "true"
    )
    static class ClientAutoConfiguration{
        @Bean
        @ConditionalOnMissingBean(FetcherResolver.class)
        public FetcherResolver fetcherResolver(){
            FetcherResolver fetcherResolver = new FetcherResolver();
            return  fetcherResolver;
        }
    }

    @Bean
    public ConsumerConfig getConsumerConfig(){
        ConsumerConfig consumerConfig = new ConsumerConfig();
        return consumerConfig;
    }

    @Configuration(
            proxyBeanMethods = false
    )
    @ConditionalOnProperty(
            prefix = "easyrpc",
            name = {"registry.enable"},
            havingValue = "true"
    )
    static class RegistryConfiguration{

        @Bean
        public RegistryConfig getRegistryConfig(){
            RegistryConfig registryConfig = new RegistryConfig();
            return registryConfig;
        }
    }

}
