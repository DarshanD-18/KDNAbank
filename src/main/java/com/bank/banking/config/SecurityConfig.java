package com.bank.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            var roles = authentication.getAuthorities();

            if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_MANAGER"))) {
                response.sendRedirect("/manager/dashboard");
            } else {
                response.sendRedirect("/official/dashboard");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/login",
                            "/favicon.png",
                            "/favicon.ico",
                            "/official/request",
                            "/css/**",
                            "/js/**"
                    ).permitAll()
                .requestMatchers("/manager/**").hasRole("MANAGER")
                .requestMatchers("/official/**").hasRole("OFFICIAL")
                    .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler())
            );

        return http.build();
    }
}


