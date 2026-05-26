package br.com.fiap.techchallenge.hospital.scheduling.security;

import br.com.fiap.techchallenge.hospital.shared.security.JwtAuthenticationEntryPoint;
import br.com.fiap.techchallenge.hospital.shared.security.JwtAuthenticationFilter;
import br.com.fiap.techchallenge.hospital.shared.security.JwtProperties;
import br.com.fiap.techchallenge.hospital.shared.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    JwtProperties jwtProperties(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs) {
        return new JwtProperties(secret, expirationMs);
    }

    @Bean
    JwtService jwtService(JwtProperties properties) {
        return new JwtService(properties);
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }
}
