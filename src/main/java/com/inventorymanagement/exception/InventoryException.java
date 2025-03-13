package com.inventorymanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryException extends Exception {
    private HttpStatus code;
    private String codeMessage;
    private String message;

    public InventoryException(String codeMessage, String message){
        this.codeMessage = codeMessage;
        this.message = message;
    }

}
