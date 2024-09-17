package com.module.user.config;

import com.module.user.auth.dto.CryptoKeys;
import com.module.user.auth.dto.JWTKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JwtConfig implements WebMvcConfigurer {
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
}
