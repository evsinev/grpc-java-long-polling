package com.payneteasy.grpc.longpolling.server.servlet.registry;

import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.grpc.longpolling.server.servlet.up.UpServerStream;

import java.io.IOException;

public class StreamHolder {

    private final UpServerStream upStream;
    private final MessagesHolder messagesHolder;

    public StreamHolder(UpServerStream upStream, MessagesHolder messagesHolder) {
        this.upStream = upStream;
        this.messagesHolder = messagesHolder;
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
}
