package com.codingforhappy.appconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan(basePackages = {
        "com.codingforhappy.config;com.codingforhappy.dao.redis;com.codingforhappy.login;com.codingforhappy.service"
})
@EnableTransactionManagement
public class RootConfig {
}
