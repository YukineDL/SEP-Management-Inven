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
    public static String PRODUCT_NAME_EXISTED = "PRODUCT_NAME_EXISTED";
    public static String PRODUCT_NOT_EXISTED = "PRODUCT_NOT_EXISTED";
    public static String SUPPLIER_NAME_EMPTY = "SUPPLIER_NAME_EMPTY";
    public static String SUPPLIER_NOT_EXIST = "SUPPLIER_NOT_EXIST";
    public static String PURCHASE_ORDER_NOT_EXIST = "PURCHASE_ORDER_NOT_EXIST";
    public static String PURCHASE_ORDER_STATUS_INCORRECT = "PURCHASE_ORDER_STATUS_INCORRECT";
    public static String PURCHASE_ORDER_APPROVED = "PURCHASE_ORDER_APPROVED";
    public static String INVALID_CREATE_INVENTORY_RECEIPT = "INVALID_CREATE_INVENTORY_RECEIPT";

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
        messages.put(PRODUCT_NAME_EXISTED, "tên sản phẩm đã tồn tại");
        messages.put(PRODUCT_NOT_EXISTED, "Sản phẩm không tồn tại");
        messages.put(SUPPLIER_NAME_EMPTY, "tên nhà cung cấp không được để trống");
        messages.put(SUPPLIER_NOT_EXIST, "Nhà cung cấp không tồn tại");
        messages.put(PURCHASE_ORDER_NOT_EXIST, "Đơn hàng không tồn tại");
        messages.put(PURCHASE_ORDER_STATUS_INCORRECT, "Đơn hàng chưa được duyệt hoặc đã bị từ chối");
        messages.put(PURCHASE_ORDER_APPROVED, "Đơn mua hàng đã được phê duyệt ");
        messages.put(INVALID_CREATE_INVENTORY_RECEIPT, "Đang có phiếu nhập tồn tại với phiếu mua hàng này, không thể tạo mới");
    }
}
