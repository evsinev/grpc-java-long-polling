package com.payneteasy.grpc.longpolling.client.http;

import io.grpc.Status;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

public class HttpClientExecutor implements IHttpClient {

    private final ExecutorService    executor;
    private final IHttpClient delegate;

    public HttpClientExecutor(ExecutorService executor, IHttpClient delegate) {
        this.executor = executor;
        this.delegate = delegate;
    }

    @Override
    public void sendMessage(InputStream aInputStream) {
        executor.execute(() -> delegate.sendMessage(aInputStream));
    }

    @Override
    public void cancelStream(Status aReason) {
        executor.execute(() -> delegate.cancelStream(aReason));
    }

}
