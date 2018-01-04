package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.client.TransportId;
import io.grpc.Status;
import io.grpc.internal.ManagedClientTransport;

import java.util.concurrent.ScheduledExecutorService;

public class TransportHttpServiceExecutor implements ITransportHttpService {

    private final ScheduledExecutorService executor;
    private final ITransportHttpService delegate;

    public TransportHttpServiceExecutor(ScheduledExecutorService executor, ITransportHttpService delegate) {
        this.executor = executor;
        this.delegate = delegate;
    }

    @Override
    public void sendClose(TransportId aTransportId) {
        executor.execute(() -> delegate.sendClose(aTransportId));

    }

    @Override
    public void sendOpenTransport(ManagedClientTransport.Listener aListener) {
        executor.execute(() -> delegate.sendOpenTransport(aListener));
    }

    @Override
    public void cancelStream(Status aReason) {
        executor.execute(() -> delegate.cancelStream(aReason));
    }
}
