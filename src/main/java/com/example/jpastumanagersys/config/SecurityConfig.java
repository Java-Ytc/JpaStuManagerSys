package com.example.jpastumanagersys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception {
        http.authorizeHttpRequests(this::configureAuthorizeRequests)
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll())
                .logout(this::configureLogout)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    private void configureAuthorizeRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize.requestMatchers("/auth/login", "/auth/register", "/captcha").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .anyRequest().authenticated();
    }

    private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
        logout.permitAll();
    }
}