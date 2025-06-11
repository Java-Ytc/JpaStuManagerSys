package com.example.jpastumanagersys.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CaptchaFilter extends OncePerRequestFilter {
    private final String captchaParam = "captcha";
    private final String loginUrl = "/auth/login";
    private final AuthenticationFailureHandler failureHandler;

    public CaptchaFilter(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (loginUrl.equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
            HttpSession session = request.getSession();
            String sessionCaptcha = (String) session.getAttribute("captcha");
            String inputCaptcha = request.getParameter(captchaParam);

            if (sessionCaptcha == null || !sessionCaptcha.equals(inputCaptcha)) {
                failureHandler.onAuthenticationFailure(request, response, new CaptchaException("验证码错误"));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
