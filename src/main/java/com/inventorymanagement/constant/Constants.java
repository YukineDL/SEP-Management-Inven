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
    public static String CATEGORY_PATH = "/category/find";
    public static String BRAND_PATH = "/brand/find-all";
    public static String PRODUCT_PATH = "/products/find-all";
    public static String PRODUCT_PATH_CODE = "/products/[^/]+";
    public static String PRODUCT_DEPEND_CATEGORY_PATH = "/products/get-list-products-category";
    public static String PRODUCT_PATH_CATEGORY = "/products/category/[^/]+";
    public static final List<String> LIST_MANAGER = new ArrayList<>(List.of(RoleEnum.ADMIN.name(),
            RoleEnum.MANAGER.name()));
    public static String PRODUCT_PREFIX_CODE = "SKU-";
    public static String PURCHASE_ORDER_CODE = "PU";
    public static String INVENTORY_RECEIPT_CODE = "IR";
    public static String INVENTORY_DELIVERY_CODE = "ID";
    public static String INVENTORY_SHEET_CODE = "IS";
    public static String ORDER_CODE = "OR";
    public static String RETURN_FROM_CODE = "RF";
    public static String FAIL = "FAIL";
    public static String SUCCESS = "SUCCESS";
    public static String PROCESSING = "PROCESSING";
    public static String WAITING_DELIVERY = "WAITING_DELIVERY";
    public static String RECEIVE_DELIVERY = "RECEIVE_DELIVERY";
    public static String STATUS_IMPORT_WAITING = "WAITING_IMPORT";
    public static String STATUS_IMPORT_SUCCESS = "IMPORT_SUCCESS";
    public static String BATCH_NUMBER_AVAILABLE = "AVAILABLE";
    public static String BATCH_NUMBER_OUT_OF_STOCK = "OUT_OF_STOCK";
}
