package com.payneteasy.grpc.longpolling.server;

import io.grpc.internal.InternalServer;
import io.grpc.internal.ServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    @Override
    public int getPort() {
        LOG.trace("getPort()");
        return -1;
    }

    public ServerListener waitForServerListener() {
        return listener;
    }
}
