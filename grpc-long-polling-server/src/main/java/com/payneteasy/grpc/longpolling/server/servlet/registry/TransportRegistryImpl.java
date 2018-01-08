package com.payneteasy.grpc.longpolling.server.servlet.registry;

import com.payneteasy.grpc.longpolling.common.TransportId;
import com.payneteasy.grpc.longpolling.server.LongPollingServerTransport;
import io.grpc.internal.ServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransportRegistryImpl implements ITransportRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(TransportRegistryImpl.class);

    private final ServerListener                    listener;
    private final Map<TransportId, TransportHolder> transports;
    private final LongPollingServerTransport        serverTransport;

    public TransportRegistryImpl(ServerListener listener, LongPollingServerTransport aServerTransport) {
        this.listener = listener;
        transports = new ConcurrentHashMap<>();
        serverTransport = aServerTransport;
    }

    @Override
    public TransportHolder findTransportHolder(TransportId transportId) {
        return transports.computeIfAbsent(transportId, id -> {
            LOG.debug("Creating new TransportHolder for {}", id);
            return new TransportHolder(listener.transportCreated(serverTransport));
        });
    }

}
