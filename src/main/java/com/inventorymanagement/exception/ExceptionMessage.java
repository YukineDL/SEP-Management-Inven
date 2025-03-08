package com.inventorymanagement.exception;

import java.util.HashMap;
import java.util.Map;

public class ExceptionMessage {
    public static Map<String, String> messages = new HashMap<>();
    public static String EXISTED_EMPLOYEE = "EXISTED_EMPLOYEE";
    public static String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static String USERNAME_INCORRECT = "USERNAME_INCORRECT";
    public static String EMPLOYEE_EXISTED = "EMPLOYEE_EXISTED";
    public static String EMPLOYEE_NOT_EXISTED = "EMPLOYEE_NOT_EXISTED";
    public static String NO_PERMISSION = "NO_PERMISSION";
    public static String ACCOUNT_BANNED = "ACCOUNT_BANNED";
    public static String CATEGORY_EXISTED = "CATEGORY_EXISTED";
    public static String CATEGORY_NAME_EMPTY = "CATEGORY_NOT_EXISTED";
    public static String CATEGORY_NOT_EXISTED = "CATEGORY_NOT_EXISTED";
    public static String BRAND_NOT_EXISTED = "BRAND_NOT_EXISTED";
    public static String BRAND_EXISTED = "BRAND_EXISTED";
    public static String BRAND_NAME_EMPTY = "BRAND_NAME_EMPTY";
    public static String PRODUCT_NAME_EMPTY = "PRODUCT_NAME_EMPTY";
    public static String PRODUCT_UNIT_EMPTY = "PRODUCT_UNIT_EMPTY";
    public static String PRODUCT_SELLING_PRICE_EMPTY = "PRODUCT_SELLING_PRICE_EMPTY";
    public static String PRODUCT_CATEGORY_CODE_EMPTY = "PRODUCT_CATEGORY_CODE_EMPTY";
    public static String PRODUCT_BRAND_CODE_EMPTY = "PRODUCT_BRAND_CODE_EMPTY";

    static {
        messages.put(EXISTED_EMPLOYEE, "Nhân viên đã tồn tại");
        messages.put(INTERNAL_SERVER_ERROR,"Có lỗi xảy ra trong hệ thống !");
        messages.put(USERNAME_INCORRECT, "tài khoản hoặc mật khẩu không đúng");
        messages.put(EMPLOYEE_EXISTED,"Nhân viên đã tồn tại");
        messages.put(EMPLOYEE_NOT_EXISTED, "Nhân viên không tồn tại");
        messages.put(NO_PERMISSION,"Bạn không có quyền truy cập ");
        messages.put(ACCOUNT_BANNED, "Tài khoản đã bị khóa");
        messages.put(CATEGORY_EXISTED, "Danh mục sản phẩm đã tồn tại");
        messages.put(CATEGORY_NAME_EMPTY, "Tên danh mục không được để trống ");
        messages.put(CATEGORY_EXISTED, "Danh mục không tồn tại trong hệ thống ");
        messages.put(BRAND_EXISTED, "Hãng đã tồn tại ");
        messages.put(BRAND_NOT_EXISTED, "Hãng này không tồn tại");
        messages.put(BRAND_NAME_EMPTY, "Tên hãng không được để trống ");
        messages.put(PRODUCT_NAME_EMPTY, "Tên sản phẩm không được để trống ");
        messages.put(PRODUCT_UNIT_EMPTY, "Đơn vị tính cảu sản phẩm không thể để trống");
        messages.put(PRODUCT_SELLING_PRICE_EMPTY, "Giá bán sản phẩm đang để trống ");
        messages.put(PRODUCT_CATEGORY_CODE_EMPTY, "Danh mục sản phẩm đang để trống ");
        messages.put(PRODUCT_BRAND_CODE_EMPTY, "Hãng sản phẩm đang để trống ");


    }
}
