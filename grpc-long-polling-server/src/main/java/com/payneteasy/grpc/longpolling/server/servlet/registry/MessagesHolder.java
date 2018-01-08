package com.payneteasy.grpc.longpolling.server.servlet.registry;

import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.grpc.longpolling.common.StreamId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessagesHolder {

    private static final Logger LOG = LoggerFactory.getLogger(MessagesHolder.class);

    private volatile State                           state;
    private final    StreamId                        streamId;
    private final    ArrayBlockingQueue<InputStream> messages;

    private enum State {
        ACTIVE, DISABLED
    }

    public MessagesHolder(StreamId aStreamId) {
        streamId = aStreamId;
        messages = new ArrayBlockingQueue<>(10);
        state    = State.ACTIVE;
    }

    public void addMessage(InputStream aMessage) {
        LOG.debug("{} Adding one message {} ...", streamId, aMessage);
        messages.add(aMessage);
    }

    public MessagesContainer awaitMessages(long aTimeToWait) throws InterruptedException, IOException {
        MessagesContainer.Builder builder = new MessagesContainer.Builder();

        // block and waiting for any message
        LOG.debug("{}: Waiting for messages ...", streamId);
        InputStream inputStream = messages.poll(aTimeToWait, TimeUnit.MILLISECONDS);

        if(inputStream != null) {
            builder.add(inputStream);
            // retrieves the rest of the queue
            while ( (inputStream = messages.poll(10, TimeUnit.MILLISECONDS)) != null) {
                builder.add(inputStream);
            }
        }
        return builder.build();
    }

    public boolean hasMessages() {
        return !messages.isEmpty();
    }

    public int size() {
        return messages.size();
    }

    public void markAsDisabled() {
         state = State.DISABLED;
    }

    public boolean isActive() {
        return state == State.ACTIVE || messages.size() > 0;
    }
}
