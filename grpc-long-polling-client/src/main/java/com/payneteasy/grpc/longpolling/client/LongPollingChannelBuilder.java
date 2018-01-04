package com.payneteasy.grpc.longpolling.client;

import io.grpc.ManagedChannelBuilder;
import io.grpc.internal.AbstractManagedChannelImplBuilder;
import io.grpc.internal.ClientTransportFactory;
import io.grpc.internal.GrpcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LongPollingChannelBuilder extends AbstractManagedChannelImplBuilder<LongPollingChannelBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingChannelBuilder.class);

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    private final URL baseUrl;

    public LongPollingChannelBuilder(InetSocketAddress aAddress, URL aBaseUrl) {
        super(aAddress, getAuthorityFromAddress(aAddress));
        baseUrl = aBaseUrl;
    }

    public static ManagedChannelBuilder<?> forTarget(String aBaseUrl) {
        LOG.trace("forBaseUrl ({})", aBaseUrl);
        URL url;
        try {
            url = new URL(aBaseUrl);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Cannot parse url " + aBaseUrl, e);
        }
        return new LongPollingChannelBuilder(new InetSocketAddress(url.getHost(), url.getPort()), url);
    }

    @CheckReturnValue
    private static String getAuthorityFromAddress(SocketAddress address) {
        if (address instanceof InetSocketAddress) {
            InetSocketAddress inetAddress = (InetSocketAddress) address;
            return GrpcUtil.authorityFromHostAndPort(inetAddress.getHostString(), inetAddress.getPort());
        } else {
            return address.toString();
        }
    }

    @Override
    protected ClientTransportFactory buildTransportFactory() {
        LOG.trace("buildTransportFactory()");
        return new LongPollingClientTransportFactory(executor, TransportId.generateNew(), baseUrl);
    }

    @Override
    public LongPollingChannelBuilder usePlaintext(boolean skipNegotiation) {
        LOG.trace("usePlaintext ({})", skipNegotiation);
        return this;
    }
}
