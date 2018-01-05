package com.payneteasy.grpc.longpolling.server.servlet;

import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.server.servlet.up.UpServerStream;
import io.grpc.Attributes;
import io.grpc.Metadata;
import io.grpc.internal.ServerTransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class TransportHolder {

    private static final Logger LOG = LoggerFactory.getLogger(TransportHolder.class);

    private final ServerTransportListener        listener;
    private final ArrayBlockingQueue<InputStream> messages;
    private final Map<StreamId, UpServerStream>   streams;
    private final ITransportRegistry              registry;

    public TransportHolder(ServerTransportListener aListener, ITransportRegistry aRegistry) {
        listener = aListener;
        messages = new ArrayBlockingQueue<>(10);
        streams = new ConcurrentHashMap<>();
        registry = aRegistry;
    }

    public ServerTransportListener getTransportListener() {
        return listener;
    }

    public void addMessage(InputStream aMessage) {
        messages.add(aMessage);
    }

    public UpServerStream getOrCreateStream(StreamId aMethodStreamId, String aMethod) {
        return streams.computeIfAbsent(aMethodStreamId, id -> {
            LOG.debug("Creating 'UP' stream {}", id);
            UpServerStream stream = new UpServerStream(registry, aMethodStreamId);
            listener.streamCreated(stream, aMethod, new Metadata());
            listener.transportReady(Attributes.EMPTY);
            return stream;
        });
    }
}
