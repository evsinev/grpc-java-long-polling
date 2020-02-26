package com.payneteasy.grpc.longpolling.server;

import io.grpc.InternalChannelz;
import io.grpc.InternalInstrumented;
import io.grpc.internal.InternalServer;
import io.grpc.internal.ServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.SocketAddress;

public class LongPollingServer implements InternalServer {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingServer.class);

    private volatile ServerListener listener;

    @Override
    public void start(ServerListener aListener) throws IOException {
        LOG.trace("start({})", aListener);
        listener = aListener;
    }

    @Override
    public void shutdown() {
        LOG.trace("shutdown()");
    }

    public ServerListener waitForServerListener() {
        return listener;
    }

    @Override
    public SocketAddress getListenSocketAddress() {
        return null;
    }

    @Nullable
    @Override
    public InternalInstrumented<InternalChannelz.SocketStats> getListenSocketStats() {
        return null;
    }

}
