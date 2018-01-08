package com.payneteasy.grpc.longpolling.server.servlet.registry;

import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.server.servlet.up.UpServerStream;
import io.grpc.Attributes;
import io.grpc.Metadata;
import io.grpc.internal.ServerTransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransportHolder {

    private static final Logger LOG = LoggerFactory.getLogger(TransportHolder.class);

    private volatile boolean                         transportEnabled;

    private final    ServerTransportListener         listener;
    private final    Map<StreamId, StreamHolder>     streams;

    public TransportHolder(ServerTransportListener aListener) {
        listener         = aListener;
        streams          = new ConcurrentHashMap<>();
        transportEnabled = true;
    }

    public StreamHolder getOrCreateUpStream(StreamId aMethodStreamId, String aMethod) {
        return getOrCreateUpStream(aMethodStreamId, aMethod, aListener -> LOG.debug("UP Stream: do nothing"));
    }

    private StreamHolder getOrCreateUpStream(StreamId aMethodStreamId
            , String aMethod
            , UpServerStream.IActionAfterMessageAvailable aActionAfterMessageAvailable) {
        return streams.computeIfAbsent(aMethodStreamId, id -> {
            LOG.debug("Creating 'UP' stream {}", id);
            MessagesHolder messagesHolder = new MessagesHolder();
            UpServerStream upStream = new UpServerStream(this, messagesHolder, aActionAfterMessageAvailable);
            listener.streamCreated(upStream, aMethod, new Metadata());
            listener.transportReady(Attributes.EMPTY);
            return new StreamHolder(upStream, messagesHolder);
        });
    }

    public StreamHolder createTapStream(StreamId aMethodStreamId, String aMethod) {
        return getOrCreateUpStream(aMethodStreamId, aMethod, aListener -> {
            LOG.debug("Half closed");
            aListener.halfClosed();
        });
    }

    public void markAsDisabled() {
        transportEnabled = false;
    }

    public boolean isActive() {
        for (StreamHolder streamHolder : streams.values()) {
            if(streamHolder.hasMessages()) {
                return true;
            }
        }
        return transportEnabled;
    }

}
