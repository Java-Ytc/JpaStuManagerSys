package com.example.jpastumanagersys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

public class ExcelConfig implements WebMvcConfigurer {
    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(excelTemplateResolver());
        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver excelTemplateResolver() {
        SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
        emailTemplateResolver.setPrefix("classpath:/templates/excel/");
        emailTemplateResolver.setSuffix(".xlsx");
        emailTemplateResolver.setTemplateMode(TemplateMode.XML);
        emailTemplateResolver.setCharacterEncoding("UTF-8");
        return emailTemplateResolver;
    }
}
