package com.inventorymanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NAME_EMPTY("NAME_EMPTY","Tên không thể để trống", HttpStatus.BAD_REQUEST),
    INVALID_KEY("INVALID_KEY","Uncategorized error", HttpStatus.BAD_REQUEST),
    SUPPLIER_EMPTY("SUPPLIER_EMPTY","Nhà cung cấp đang để trống", HttpStatus.BAD_REQUEST),
    EMPLOYEE_EMPTY("EMPLOYEE_EMPTY","Nhân viên phụ trách đang trống", HttpStatus.BAD_REQUEST),
    DELIVERY_DATE("DELIVERY_DATE", "Ngày giao hàng đang trống", HttpStatus.BAD_REQUEST),
    PURCHASE_ORDER_EMPTY("PURCHASE_ORDER_EMPTY", "Sản phẩm trong đơn hàng rỗng", HttpStatus.BAD_REQUEST),;
    ErrorCode(String codeMessage, String message, HttpStatus code) {
        this.codeMessage = codeMessage;
        this.message = message;
        this.statusCode = code;
    }
    private final String codeMessage;
    private final String message;
    private final HttpStatus statusCode;
}
