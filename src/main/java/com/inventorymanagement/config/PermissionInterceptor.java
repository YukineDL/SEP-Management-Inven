package com.inventorymanagement.config;

import com.inventorymanagement.constant.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class PermissionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){
        if (this.checkIgnoreUrlInterceptor(request.getRequestURI())) {
            return true;
        }
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return true;
    }
    private boolean checkIgnoreUrlInterceptor(String url){
        if (url == null) {
            return false;
        }
        return url.contains(Constants.SWAGGER_UI_PATH)
                || url.contains(Constants.API_DOCS_PATH)
                || url.contains(Constants.AUTHENTICATED_PATH)
                || url.contains(Constants.BRAND_PATH)
                || url.contains(Constants.CATEGORY_PATH)
                || url.contains(Constants.PRODUCT_PATH)
                || url.matches(Constants.PRODUCT_PATH_CODE)
                || url.matches(Constants.PRODUCT_PATH_CATEGORY)
                || url.contains(Constants.PRODUCT_DEPEND_CATEGORY_PATH);
    }
}
