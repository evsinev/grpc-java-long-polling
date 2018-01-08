package com.payneteasy.grpc.longpolling.server.servlet.registry;

import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.server.servlet.up.UpServerStream;

import java.io.IOException;

public class StreamHolder {

    private final UpServerStream upStream;
    private final MessagesHolder messagesHolder;
    private final StreamId       streamId;

    public StreamHolder(UpServerStream upStream, MessagesHolder messagesHolder, StreamId aId) {
        this.upStream = upStream;
        this.messagesHolder = messagesHolder;
        streamId = aId;
    }

    public UpServerStream getUpStream() {
        return upStream;
    }

    public MessagesContainer awaitMessages(long aTimeout) throws IOException, InterruptedException {
        return messagesHolder.awaitMessages(aTimeout);
    }

    public boolean hasMessages() {
        return messagesHolder.hasMessages();
    }

    @Override
    public String toString() {
        return "StreamHolder{"
                + "id=" + streamId.getStreamId()
                + ", messages=" + messagesHolder.size()
                + '}';
    }

    public boolean isActive() {
        return messagesHolder.isActive();
    }
}
