package com.payneteasy.grpc.longpolling.server.servlet;

public class ServletOptions {

    private final long readTimeout;

    public ServletOptions(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getReadTimeout() {
        return (int) readTimeout;
    }
}
