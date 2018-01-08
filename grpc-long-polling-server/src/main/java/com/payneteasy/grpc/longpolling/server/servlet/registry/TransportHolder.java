package com.payneteasy.grpc.longpolling.server.servlet.registry;

import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.common.TransportId;
import com.payneteasy.grpc.longpolling.server.servlet.up.UpServerStream;
import io.grpc.Attributes;
import io.grpc.Metadata;
import io.grpc.internal.ServerTransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class TransportHolder {

    private static final Logger LOG = LoggerFactory.getLogger(TransportHolder.class);

    private volatile AtomicReference<TransportState> transportState;
    private volatile long                            lastAccessTime;

    private final    ServerTransportListener         listener;
    private final    Map<StreamId, StreamHolder>     streams;
    private final    TransportId                     transportId;

    private enum TransportState {
          IDLE
        , ACTIVE
    }

    public TransportHolder(ServerTransportListener aListener, TransportId aId) {
        listener         = aListener;
        streams          = new ConcurrentHashMap<>();
        transportState   = new AtomicReference<>(TransportState.IDLE);
        transportId      = aId;
    }

    public StreamHolder getOrCreateUpStream(StreamId aMethodStreamId, String aMethod) {
        return getOrCreateUpStream(aMethodStreamId, aMethod, aListener -> LOG.debug("UP Stream: do nothing"));
    }

    private StreamHolder getOrCreateUpStream(StreamId aMethodStreamId
            , String aMethod
            , UpServerStream.IActionAfterMessageAvailable aActionAfterMessageAvailable) {
        return streams.computeIfAbsent(aMethodStreamId, id -> {
            LOG.debug("Creating 'UP' stream {}", id);
            MessagesHolder messagesHolder = new MessagesHolder(aMethodStreamId);
            UpServerStream upStream = new UpServerStream(messagesHolder, aActionAfterMessageAvailable);
            listener.streamCreated(upStream, aMethod, new Metadata());
            if(transportState.compareAndSet(TransportState.IDLE, TransportState.ACTIVE)) {
                listener.transportReady(Attributes.EMPTY);
            }
            return new StreamHolder(upStream, messagesHolder, aMethodStreamId);
        });
    }

    public StreamHolder createTapStream(StreamId aMethodStreamId, String aMethod) {
        return getOrCreateUpStream(aMethodStreamId, aMethod, aListener -> {
            LOG.debug("Half closed");
            aListener.halfClosed();
        });
    }

    public boolean isActive() {
        for (StreamHolder streamHolder : streams.values()) {
            if(streamHolder.isActive() ) {
                return true;
            }
        }
        return transportState.get() == TransportState.IDLE;
    }

    public void updateAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (StreamHolder streamHolder : streams.values()) {
            if(streamHolder.hasMessages()) {
                sb.append(streamHolder);
                sb.append(", ");
            }
        }
        return "TransportHolder{"
                + "  " + transportId
                + ", streams=" + streams.size()
                + ", state=" + transportState
                + ", lastAccessTime=" + new Date(lastAccessTime)
                + ", steamsWithMessages=" + sb
                ;
    }
}
