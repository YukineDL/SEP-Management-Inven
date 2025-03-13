package com.inventorymanagement.exception;

import com.inventorymanagement.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalException {
    private static final String MIN_ATTRIBUTE = "min";
    private static final String MAX_ATTRIBUTE = "max";
    private static final String VALUE_ATTRIBUTE = "value";

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
    @ExceptionHandler(value = InventoryException.class)
    ResponseEntity<Object> handleAppException(InventoryException inventoryException){
        return new ResponseEntity<>(
                ApiResponse.builder()
                        .code(inventoryException.getCode())
                        .message(inventoryException.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {

        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation =
                    exception.getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

            log.info("attributes : {}", attributes.toString());

        } catch (IllegalArgumentException e) {

        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getStatusCode());
        apiResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        String value = String.valueOf(attributes.get(VALUE_ATTRIBUTE));
        String max = String.valueOf(attributes.get(MAX_ATTRIBUTE));
        log.info("max : {} min : {} value : {}", max, minValue, value);

        if(value != null && !"null".equals(value)){
            log.info("value : {}", value);
            return message.replace("{" + VALUE_ATTRIBUTE + "}", value);
        } else if(max != null && !"null".equals(max)){
            log.info("max : {}", max);
            return message.replace("{" + MAX_ATTRIBUTE + "}", max);
        }
        log.info("min : {}", minValue);
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
