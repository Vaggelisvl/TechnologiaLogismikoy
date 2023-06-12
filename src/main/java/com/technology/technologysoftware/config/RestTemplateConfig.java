package com.technology.technologysoftware.config;

import com.technology.technologysoftware.interceptor.RestTemplateLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestTemplateConfig {
    @Value("${api.timeout.connect}")
    private int connectTimeout;
    @Value("${api.timeout.read}")
    private int readTimeout;
    private final RestTemplateLoggingInterceptor restLoggingInterceptor;

    public RestTemplateConfig(RestTemplateLoggingInterceptor restLoggingInterceptor) {
        this.restLoggingInterceptor = restLoggingInterceptor;
    }

    @Bean
    public RestTemplate restTemplate() {
        log.info("Configuring Rest Template");

        final SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(connectTimeout);
        simpleClientHttpRequestFactory.setReadTimeout(readTimeout);


        final RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory));
        restTemplate.getInterceptors().add(restLoggingInterceptor);

        return restTemplate;
    }
}
