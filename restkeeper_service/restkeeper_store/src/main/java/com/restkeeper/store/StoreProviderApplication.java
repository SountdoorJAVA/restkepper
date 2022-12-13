package com.restkeeper.store;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.restkeeper.store.mapper")
@SpringBootApplication(scanBasePackages = {"com.restkeeper"})
public class StoreProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreProviderApplication.class,args);
    }
}
