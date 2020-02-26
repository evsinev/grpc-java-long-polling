package com.payneteasy.grpc.longpolling.server;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.InternalChannelz;
import io.grpc.InternalLogId;
import io.grpc.Status;
import io.grpc.internal.ServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

public class LongPollingServerTransport implements ServerTransport {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingServerTransport.class);

    private final ScheduledExecutorService executorService;

    private final InternalLogId logId = InternalLogId.allocate(getClass(), "");

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
    public ListenableFuture<InternalChannelz.SocketStats> getStats() {
        LOG.trace("getTransportStats()");
        SettableFuture<InternalChannelz.SocketStats> ret = SettableFuture.create();
        ret.set(null);
        return ret;
    }

    @Override
    public InternalLogId getLogId() {
        LOG.trace("getLogId()");
        return logId;
    }
}
