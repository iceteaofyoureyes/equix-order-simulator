package com.equix.ordersimulator.interfaces.config;

public enum HttpStatusCode {

    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    INTERNAL_SERVER_ERROR(500);

    public final int value;

    HttpStatusCode(int value) {
        this.value = value;
    }

    public static final String STATUS_OK = "200";
    public static final String STATUS_CREATED = "201";
    public static final String STATUS_ACCEPTED = "202";
    public static final String STATUS_NO_CONTENT = "204";
    public static final String STATUS_BAD_REQUEST = "400";
    public static final String STATUS_UNAUTHORIZED = "401";
    public static final String STATUS_FORBIDDEN = "403";
    public static final String STATUS_NOT_FOUND = "404";
    public static final String STATUS_CONFLICT = "409";
    public static final String STATUS_INTERNAL_SERVER_ERROR = "500";
}
