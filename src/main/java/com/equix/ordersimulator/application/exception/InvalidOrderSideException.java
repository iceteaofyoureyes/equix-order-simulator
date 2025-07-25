package com.equix.ordersimulator.application.exception;

public class InvalidOrderSideException extends AppException {
    public InvalidOrderSideException() {
        super(ErrorCode.INVALID_ORDER_SIDE);
        setMessage(ErrorCode.INVALID_ORDER_SIDE.getMessage());
    }
    public InvalidOrderSideException(String message) {
        super(ErrorCode.INVALID_ORDER_SIDE);
        setMessage(message);
    }
}
