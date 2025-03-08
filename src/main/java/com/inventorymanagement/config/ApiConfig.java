package com.inventorymanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        PermissionInterceptor permissionInterceptor =
                ApplicationContextHolder.getBean(PermissionInterceptor.class);
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("*");
    }
}
