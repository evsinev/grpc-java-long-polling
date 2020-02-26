package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.client.util.ConnectionOptions;
import com.payneteasy.grpc.longpolling.common.TransportId;
import io.grpc.ChannelLogger;
import io.grpc.internal.ClientTransportFactory;
import io.grpc.internal.ConnectionClientTransport;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.SharedResourceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class LongPollingClientTransportFactory implements ClientTransportFactory {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientTransportFactory.class);

    private final ExecutorService          executorService;
    private final TransportId              transportId;
    private final ConnectionOptions        connectionOptions;

    private final ScheduledExecutorService timeoutService = SharedResourceHolder.get(GrpcUtil.TIMER_SERVICE);
    private       boolean                  closed         = false;

    public LongPollingClientTransportFactory(ExecutorService aExecutor, TransportId aId, ConnectionOptions aOptions) {
        executorService = aExecutor;
        transportId = aId;
        connectionOptions = aOptions;
    }

    @Override
    public ConnectionClientTransport newClientTransport(SocketAddress serverAddress, ClientTransportOptions options, ChannelLogger channelLogger) {
        LOG.trace("newClientTransport(address={}, options:{})", serverAddress, options);
        return new LongPollingClientTransport(executorService, connectionOptions, transportId);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        LOG.trace("getScheduledExecutorService()");
        return timeoutService;
    }

    @Override
    public void close() {
        LOG.trace("close(). Releasing all resources...");
        if(!closed) {
            SharedResourceHolder.release(GrpcUtil.TIMER_SERVICE, timeoutService);
        }
        closed = true;
    }
}
