package com.payneteasy.grpc.longpolling.server;

import com.google.common.util.concurrent.SettableFuture;
import io.grpc.Status;
import io.grpc.internal.LogId;
import io.grpc.internal.ServerTransport;
import io.grpc.internal.TransportTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class LongPollingServerTransport implements ServerTransport {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingServerTransport.class);

    private final ScheduledExecutorService executorService;

    private final LogId logId = LogId.allocate(getClass().getName());

    public LongPollingServerTransport(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void shutdown() {
        LOG.trace("shutdown()");
    }

    @Override
    public void shutdownNow(Status reason) {
        LOG.trace("shutdownNow({})", reason);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        LOG.trace("getScheduledExecutorService()");
        return executorService;
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
        LOG.trace("getLogId()");
        return logId;
    }
}
