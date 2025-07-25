package com.equix.ordersimulator.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNKNOWN_EXCEPTION(9999, "Unexpected system error occurs!", HttpStatus.INTERNAL_SERVER_ERROR),
    RESOURCE_NOT_FOUND(4004, "Resource not found", HttpStatus.NOT_FOUND),
    INVALID_PAYLOAD(4000, "Invalid request Payload. Check property 'data' for more information", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_SIDE(4010, "Order Side must be SELL or BUY", HttpStatus.BAD_REQUEST),
    BUSINESS_RULE_EXCEPTION(4999, null, HttpStatus.BAD_REQUEST)
    ;


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
