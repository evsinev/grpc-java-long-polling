package com.payneteasy.grpc.longpolling.client.http;

import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

public class StreamHttpServiceExecutor implements IStreamHttpService {

    private final ExecutorService    executor;
    private final IStreamHttpService delegate;

    public StreamHttpServiceExecutor(ExecutorService executor, IStreamHttpService delegate) {
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

    @Override
    public void setClientStreamListener(ClientStreamListener aListener) {
        executor.execute(() -> delegate.setClientStreamListener(aListener));
    }
}
