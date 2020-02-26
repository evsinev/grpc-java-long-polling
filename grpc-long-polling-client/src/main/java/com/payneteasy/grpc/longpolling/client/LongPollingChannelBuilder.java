package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.client.util.ConnectionOptions;
import com.payneteasy.grpc.longpolling.common.TransportId;
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
import java.util.concurrent.ExecutorService;

import static io.grpc.internal.GrpcUtil.getThreadFactory;
import static java.util.concurrent.Executors.newCachedThreadPool;

public class LongPollingChannelBuilder extends AbstractManagedChannelImplBuilder<LongPollingChannelBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingChannelBuilder.class);

    private static final ExecutorService EXECUTOR = newCachedThreadPool(getThreadFactory("long-polling-%d", true));

    private final URL  baseUrl;
    private       long connectionTimeout      = 60_000;
    private       long readTimeout            = 120_000;

    public LongPollingChannelBuilder(InetSocketAddress aAddress, URL aBaseUrl) {
        super(aAddress, getAuthorityFromAddress(aAddress));
        baseUrl = aBaseUrl;
    }

    public static LongPollingChannelBuilder forTarget(String aBaseUrl) {
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
        return new LongPollingClientTransportFactory(EXECUTOR, TransportId.generateNew(), new ConnectionOptions(baseUrl, connectionTimeout, readTimeout));
    }

    public LongPollingChannelBuilder setConnectionTimeout(int aConnectionTimeout) {
        connectionTimeout = aConnectionTimeout;
        return this;
    }

    public LongPollingChannelBuilder setReadTimeout(int aReadTimeout) {
        readTimeout = aReadTimeout;
        return this;
    }

    @Override
    public LongPollingChannelBuilder usePlaintext() {
        LOG.trace("usePlaintext ()");
        return this;
    }

}
