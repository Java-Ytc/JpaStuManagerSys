package com.example.jpastumanagersys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(this::configureAuthorizeRequests).formLogin(this::configureFormLogin).logout(this::configureLogout).csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    private void configureAuthorizeRequests(org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize.requestMatchers("/auth/login", "/register", "/captcha").permitAll().requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/teacher/**").hasRole("TEACHER").requestMatchers("/student/**").hasRole("STUDENT").anyRequest().authenticated();
    }

    private void configureFormLogin(org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer<HttpSecurity> form) {
        form.loginPage("/auth/login").defaultSuccessUrl("/home").permitAll();
    }

    private void configureLogout(org.springframework.security.config.annotation.web.configurers.LogoutConfigurer<HttpSecurity> logout) {
        logout.permitAll();
    }
}