package com.payneteasy.grpc.longpolling.server.servlet;

import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.server.servlet.up.UpServerStream;
import io.grpc.Attributes;
import io.grpc.Metadata;
import io.grpc.internal.ServerTransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TransportHolder {

    private static final Logger LOG = LoggerFactory.getLogger(TransportHolder.class);

    private volatile boolean                         enabled;
    private final    ServerTransportListener         listener;
    private final    ArrayBlockingQueue<InputStream> messages;
    private final    Map<StreamId, UpServerStream>   streams;
    private final    ITransportRegistry              registry;

    public TransportHolder(ServerTransportListener aListener, ITransportRegistry aRegistry) {
        listener = aListener;
        messages = new ArrayBlockingQueue<>(10);
        streams = new ConcurrentHashMap<>();
        registry = aRegistry;
        enabled = true;
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

    public MessagesContainer getMessages(long aTimeToWait) throws InterruptedException, IOException {
        MessagesContainer.Builder builder = new MessagesContainer.Builder();
        // waiting for any message
        InputStream inputStream = messages.poll(aTimeToWait, TimeUnit.MILLISECONDS);
        if(inputStream != null) {
            builder.add(inputStream);
            // retrieves the rest of the queue
            while ( (inputStream = messages.poll()) != null) {
                builder.add(inputStream);
            }
        }
        return builder.build();
    }

    public void markAsDisabled() {
        enabled = false;
    }

    public boolean isActive() {
        return !messages.isEmpty() || enabled;
    }
}
