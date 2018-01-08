package com.payneteasy.grpc.longpolling.client.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionOptions {

    private final long connectionTimeout;
    private final long readTimeout;
    private final URL  baseUrl;

    public ConnectionOptions(URL aBaseUrl, long connectionTimeout, long readTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        baseUrl = aBaseUrl;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void configure(HttpURLConnection aConnection) {
        aConnection.setConnectTimeout((int) connectionTimeout);
        aConnection.setReadTimeout((int) readTimeout);
    }
}
