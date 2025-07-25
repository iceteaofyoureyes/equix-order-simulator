package com.equix.ordersimulator.application.exception;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException() {
        super(ErrorCode.RESOURCE_NOT_FOUND);
        setMessage(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
    }
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND);
        setMessage(message);
    }
}
