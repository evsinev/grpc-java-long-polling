package com.payneteasy.grpc.longpolling.server;

import io.grpc.ServerStreamTracer;
import io.grpc.internal.AbstractServerImplBuilder;
import io.grpc.internal.InternalServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class LongPollingServerBuilder extends AbstractServerImplBuilder<LongPollingServerBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingServerBuilder.class);

    private LongPollingServer pollingServer;


    @Override
    protected List<? extends InternalServer> buildTransportServers(List<? extends ServerStreamTracer.Factory> streamTracerFactories) {
        LOG.trace("buildTransportServers({})", streamTracerFactories);
        return Collections.singletonList(pollingServer);
    }

    public LongPollingServerBuilder longPollingServer(LongPollingServer aPollingServer) {
        pollingServer = aPollingServer;
        return this;
    }

    @Override
    public LongPollingServerBuilder useTransportSecurity(File certChain, File privateKey) {
        LOG.trace("useTransportSecurity({}, {})", certChain, privateKey);
        throw new UnsupportedOperationException("TLS not supported in LongPollingServerBuilder");
    }

    public static LongPollingServerBuilder forPort(int aPort) {
        LOG.debug("forPort({})", aPort);
        return new LongPollingServerBuilder();
    }

}
