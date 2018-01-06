package com.payneteasy.grpc.longpolling.server.servlet;

import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.common.TransportId;
import com.payneteasy.grpc.longpolling.server.LongPollingServerTransport;
import io.grpc.internal.ServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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
    public TransportHolder getOrCreateTransportListener(TransportId transportId) {
        return transports.computeIfAbsent(transportId, id -> {
            LOG.debug("Creating new TransportHolder for {}", id);
            return new TransportHolder(this.listener.transportCreated(serverTransport), this);
        });
    }

    @Override
    public void removeTransport(TransportId aTransportId) {
        TransportHolder holder = transports.get(aTransportId);
        if(holder != null) {
            holder.markAsDisabled();
        }
    }

    @Override
    public void enqueueMessage(StreamId aStreamId, InputStream aMessage) {
        LOG.debug("Adding message to the queue for {}", aStreamId);
        TransportHolder holder = transports.get(aStreamId.getTransportId());
        if(holder != null) {
            holder.addMessage(aMessage);
        } else {
            LOG.warn("No transport for {}", aStreamId);
        }
    }

    @Override
    public TransportHolder getReadyMessages(StreamId aStreamId, String method) throws InterruptedException, IOException {
        TransportHolder holder = getOrCreateTransportListener(aStreamId.getTransportId());
        holder.getOrCreateStream(aStreamId, method);
        return holder;
    }
}
