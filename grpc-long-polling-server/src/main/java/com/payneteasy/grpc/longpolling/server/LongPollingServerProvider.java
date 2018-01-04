package com.payneteasy.grpc.longpolling.server;

import io.grpc.ServerBuilder;
import io.grpc.ServerProvider;

public class LongPollingServerProvider extends ServerProvider {
    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 5;
    }

    @Override
    protected ServerBuilder<?> builderForPort(int port) {
        return null;
    }
}
