package com.inventorymanagement.exception;

import com.inventorymanagement.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<Object> handlingException(RuntimeException exception){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
        apiResponse.setMessage(ExceptionMessage.messages.get(ExceptionMessage.INTERNAL_SERVER_ERROR));
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(value = ResponseStatusException.class)
    ResponseEntity<Object> handleNoAuthorization(ResponseStatusException exception){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode((HttpStatus) exception.getStatusCode());
        apiResponse.setMessage(ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION));
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }
}
