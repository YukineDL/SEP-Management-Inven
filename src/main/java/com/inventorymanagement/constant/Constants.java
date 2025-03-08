package com.inventorymanagement.constant;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static String HEADER_AUTHORIZATION = "Authorization";
    public static boolean LOCK = true;
    public static boolean UNLOCK = false;
    public static String SWAGGER_UI_PATH = "/swagger-ui";
    public static String API_DOCS_PATH = "/api-docs";
    public static String AUTHENTICATED_PATH = "/authenticated";
    public static String SUCCESS = "Thành công";
    public static String CATEGORY_PATH = "/category/find-all";
    public static String BRAND_PATH = "/brand/find-all";
    public static String PRODUCT_PATH = "/products/find-all";
    public static final List<String> LIST_MANAGER = new ArrayList<>(List.of(RoleEnum.ADMIN.name(),
            RoleEnum.MANAGER.name()));
    public static String PRODUCT_PREFIX_CODE = "SKU-";
}
