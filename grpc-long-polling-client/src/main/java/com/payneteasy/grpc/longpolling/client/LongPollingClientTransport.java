package com.payneteasy.grpc.longpolling.client;

import com.google.common.util.concurrent.SettableFuture;
import com.payneteasy.grpc.longpolling.client.http.ITransportHttpService;
import com.payneteasy.grpc.longpolling.common.TransportId;
import io.grpc.*;
import io.grpc.internal.ClientStream;
import io.grpc.internal.ConnectionClientTransport;
import io.grpc.internal.LogId;
import io.grpc.internal.TransportTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class LongPollingClientTransport implements ConnectionClientTransport {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientTransport.class);

    private final LogId logId = LogId.allocate(getClass().getName());

    private final ITransportHttpService httpService;
    private final TransportId transportId;
    private final URL baseUrl;
    private final ExecutorService executor;
    private final AtomicBoolean   transportActive = new AtomicBoolean(true);

    public LongPollingClientTransport(ExecutorService aExecutor, URL aBaseUrl, TransportId aId, ITransportHttpService httpService) {
        this.httpService = httpService;
        baseUrl = aBaseUrl;
        transportId = aId;
        executor = aExecutor;
    }

    @Override
    public Attributes getAttributes() {
        LOG.trace("getAttributes()");
        return Attributes.EMPTY;
    }

    @Nullable
    @Override
    public Runnable start(Listener listener) {
        LOG.trace("start({})", listener);
        return () -> httpService.sendOpenTransport(listener);
    }

    @Override
    public void shutdown(Status reason) {
        LOG.trace("shutdown({})", reason);
        transportActive.set(false);
    }

    @Override
    public void shutdownNow(Status reason) {
        LOG.trace("shutdownNow({})", reason);
        transportActive.set(false);
    }

    @Override
    public ClientStream newStream(MethodDescriptor<?, ?> method, Metadata headers, CallOptions callOptions) {
        LOG.trace("newStream({}, {}, {}, {})", method.getFullMethodName(), method.getType(), headers, callOptions);
        if(method.getType() == MethodDescriptor.MethodType.UNARY) {
            return new LongPollingClientStreamUnary(executor, baseUrl, transportId.generateNextStreamId(), method, headers, callOptions);
        } else {
            return new LongPollingClientStreamBidi(executor, baseUrl, transportId.generateNextStreamId(), method, headers, callOptions, transportActive);
        }
    }


    @Override
    public void ping(PingCallback callback, Executor executor) {
        LOG.trace("ping({}, {})", callback, executor);

    }

    @Override
    public Future<TransportTracer.Stats> getTransportStats() {
        LOG.trace("getTransportStats()");
        SettableFuture<TransportTracer.Stats> ret = SettableFuture.create();
        ret.set(null);
        return ret;
    }

    @Override
    public LogId getLogId() {
        return logId;
    }
}
