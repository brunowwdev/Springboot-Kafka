package com.microservicos.icompras.pedidos.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.microservicos.icompras.pedidos.client")
public class ClientsConfig {

}
