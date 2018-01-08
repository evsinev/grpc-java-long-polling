package com.payneteasy.grpc.longpolling.client;

import com.google.common.util.concurrent.SettableFuture;
import com.payneteasy.grpc.longpolling.client.util.ConnectionOptions;
import com.payneteasy.grpc.longpolling.client.util.ServerEndPoint;
import com.payneteasy.grpc.longpolling.common.TransportId;
import io.grpc.*;
import io.grpc.internal.ClientStream;
import io.grpc.internal.ConnectionClientTransport;
import io.grpc.internal.LogId;
import io.grpc.internal.TransportTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class LongPollingClientTransport implements ConnectionClientTransport {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientTransport.class);

    private final LogId logId = LogId.allocate(getClass().getName());

    private final TransportId transportId;
    private final ConnectionOptions connectionFactory;
    private final ExecutorService executor;
    private final AtomicBoolean   transportActive = new AtomicBoolean(true);

    public LongPollingClientTransport(ExecutorService aExecutor, ConnectionOptions aConnectionFactory, TransportId aId) {
        connectionFactory = aConnectionFactory;
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
        return () -> executor.execute(listener::transportReady);
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

        ServerEndPoint endPoint = new ServerEndPoint(connectionFactory, transportId.generateNextStreamId(), method);

        switch (method.getType()) {
            case UNARY:
                return new LongPollingClientStreamUnary(executor, endPoint);

            case BIDI_STREAMING:
                return new LongPollingClientStreamBidi(executor, endPoint, transportActive);

            case SERVER_STREAMING:
                return new LongPollingClientStreamTap(executor, endPoint, transportActive);

            default:
                throw new IllegalStateException("Unsupported " + method.getType());
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
