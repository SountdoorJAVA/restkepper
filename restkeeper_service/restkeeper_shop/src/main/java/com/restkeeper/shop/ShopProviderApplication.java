package com.restkeeper.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.restkeeper.shop.mapper")
@SpringBootApplication(scanBasePackages = {"com.restkeeper"})
public class ShopProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopProviderApplication.class, args);
    }

}
