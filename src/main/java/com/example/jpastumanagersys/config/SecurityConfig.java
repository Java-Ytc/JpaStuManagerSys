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

/**
 * SecurityConfig 类是 Spring Security 的配置类，用于配置应用程序的安全规则，
 * 包括请求授权、登录页面、退出登录等功能。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置密码编码器，使用 BCrypt 算法对用户密码进行加密。
     * BCrypt 是一种安全的密码哈希算法，它会自动生成盐值，增加密码的安全性。
     *
     * @return 返回一个 BCryptPasswordEncoder 实例。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 Spring Security 的过滤器链，定义请求的授权规则、登录页面、退出登录等功能。
     *
     * @param http                     HttpSecurity 对象，用于配置 HTTP 请求的安全规则。
     * @param customAuthenticationSuccessHandler 自定义的认证成功处理器，用于处理用户登录成功后的逻辑。
     * @return 返回一个 SecurityFilterChain 实例，包含配置好的安全过滤器链。
     * @throws Exception 如果在配置过程中出现异常，将抛出该异常。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception {
        // 配置请求的授权规则
        http.authorizeHttpRequests(this::configureAuthorizeRequests)
                // 配置表单登录功能
                .formLogin(form -> form
                        // 设置登录页面的 URL
                        .loginPage("/auth/login")
                        // 设置自定义的认证成功处理器
                        .successHandler(customAuthenticationSuccessHandler)
                        // 允许所有用户访问登录页面
                        .permitAll())
                // 配置退出登录功能
                .logout(this::configureLogout)
                // 禁用 CSRF 保护（在生产环境中建议启用）
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * 配置请求的授权规则，定义不同 URL 路径的访问权限。
     *
     * @param authorize 用于配置请求授权规则的对象。
     */
    private void configureAuthorizeRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        // 允许所有用户访问登录、注册和验证码相关的 URL
        authorize.requestMatchers("/auth/login", "/auth/register", "/captcha").permitAll()
                // 只有具有 ADMIN 角色的用户才能访问以 /admin/ 开头的 URL
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 只有具有 TEACHER 角色的用户才能访问以 /teacher/ 开头的 URL
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                // 只有具有 STUDENT 角色的用户才能访问以 /student/ 开头的 URL
                .requestMatchers("/student/**").hasRole("STUDENT")
                // 其他所有请求都需要用户进行身份验证才能访问
                .anyRequest().authenticated();
    }

    /**
     * 配置退出登录功能，定义退出登录的 URL 和退出成功后的重定向 URL。
     *
     * @param logout 用于配置退出登录功能的对象。
     */
    private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
        // 设置退出登录的 URL
        logout.logoutUrl("/auth/logout")
                // 设置退出登录成功后重定向的 URL
                .logoutSuccessUrl("/auth/login")
                // 允许所有用户访问退出登录的 URL
                .permitAll();
    }
}