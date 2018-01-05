package com.payneteasy.grpc.longpolling.server.servlet;

import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.common.TransportId;
import com.payneteasy.grpc.longpolling.server.LongPollingServerTransport;
import io.grpc.internal.ServerListener;
import io.grpc.internal.ServerTransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransportRegistryImpl implements ITransportRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(TransportRegistryImpl.class);

    private final ServerListener listener;
    private final Map<TransportId, TransportHolder> transports;

    public TransportRegistryImpl(ServerListener listener) {
        this.listener = listener;
        transports = Collections.synchronizedMap(new ConcurrentHashMap<>());
    }

    @Override
    public TransportHolder getOrCreateTransportListener(TransportId transportId, LongPollingServerTransport aServerTransport) {
        return transports.computeIfAbsent(transportId, id -> {
            LOG.debug("Creating new TransportHolder for {}", id);
            return new TransportHolder(this.listener.transportCreated(aServerTransport), this);
        });
    }

    @Override
    public void removeTransport(TransportId aTransportId) {
        transports.remove(aTransportId);
    }

    @Override
    public void enqueueMessage(StreamId aStreamId, InputStream aMessage) {
        TransportHolder holder = transports.get(aStreamId.getTransportId());
        if(holder != null) {
            holder.addMessage(aMessage);
        } else {
            LOG.warn("No transport for {}", aStreamId);
        }
    }
}
