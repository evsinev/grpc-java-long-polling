package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.client.http.ITransportHttpService;
import com.payneteasy.grpc.longpolling.client.http.TransportHttpServiceExecutor;
import com.payneteasy.grpc.longpolling.client.http.TransportHttpServiceNoop;
import com.payneteasy.grpc.longpolling.client.util.Urls;
import io.grpc.internal.ClientTransportFactory;
import io.grpc.internal.ConnectionClientTransport;
import io.grpc.internal.ProxyParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.SocketAddress;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;

public class LongPollingClientTransportFactory implements ClientTransportFactory {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientTransportFactory.class);

    private final ScheduledExecutorService executorService;
    private final TransportId              transportId;
    private final URL                      baseUrl;

    public LongPollingClientTransportFactory(ScheduledExecutorService aExecutor, TransportId aId, URL aBaseUrl) {
        executorService = aExecutor;
        transportId = aId;
        baseUrl = aBaseUrl;
    }

    @Override
    public ConnectionClientTransport newClientTransport(SocketAddress aAddress, String authority, @Nullable String userAgent, @Nullable ProxyParameters proxy) {
        LOG.trace("newClientTransport(address={}, authority:{}, userAgent:{}, proxy:{})", aAddress, authority, userAgent, proxy);
        URL transportUrl = Urls.appendPaths(baseUrl, "/transport");
        ITransportHttpService httpService = new TransportHttpServiceExecutor(executorService, new TransportHttpServiceNoop(transportId));
        return new LongPollingClientTransport(executorService, baseUrl, transportId, httpService);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        LOG.trace("getScheduledExecutorService()");
        return executorService;
    }

    @Override
    public void close() {
        LOG.trace("close(). Releasing all resources...");
    }
}
