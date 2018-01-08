package com.payneteasy.grpc.longpolling.client.http;

public class HttpStatus {

    public static final int OK = 200;
    public static final int GONE = 410;

    private final int code;
    private final String message;

    public HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean wasNotOk() {
        return code != OK;
    }

    @Override
    public String toString() {
        return "HttpStatus{"
                + "code=" + code
                + ", message='" + message + '\''
                + '}';
    }
}
