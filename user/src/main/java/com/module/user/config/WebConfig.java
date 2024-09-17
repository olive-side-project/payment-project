package com.module.user.config;

import com.module.user.auth.dto.CryptoKeys;
import com.module.user.auth.dto.JWTKeys;
import com.module.user.interceptor.PaymentApiInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final PaymentApiInterceptor paymentApiInterceptor;

    @Bean
    @ConfigurationProperties(prefix = "web.mvc.jwt")
    public JWTKeys jwtKeys() {
        return new JWTKeys();
    }

    @Bean
    @ConfigurationProperties(prefix = "web.mvc.aes")
    public CryptoKeys cryptoKeys() {
        return new CryptoKeys();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowCredentials(true)
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition")
                .allowedOriginPatterns("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(paymentApiInterceptor);
    }
}
