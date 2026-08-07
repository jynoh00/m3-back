package com.example.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    private static final int CONNECT_TIMEOUT_MILLIS = 3000;
    private static final int READ_TIMEOUT_MILLIS = 5000;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(READ_TIMEOUT_MILLIS);

        return new RestTemplate(requestFactory);
    }
}