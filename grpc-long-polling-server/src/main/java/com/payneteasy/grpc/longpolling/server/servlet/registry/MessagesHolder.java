package com.payneteasy.grpc.longpolling.server.servlet.registry;

import com.payneteasy.grpc.longpolling.common.MessagesContainer;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessagesHolder {

    private final ArrayBlockingQueue<InputStream> messages;

    public MessagesHolder() {
        messages = new ArrayBlockingQueue<>(10);
    }

    public void addMessage(InputStream aMessage) {
        messages.add(aMessage);
    }

    public MessagesContainer awaitMessages(long aTimeToWait) throws InterruptedException, IOException {
        MessagesContainer.Builder builder = new MessagesContainer.Builder();

        // block and waiting for any message
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

    public boolean hasMessages() {
        return !messages.isEmpty();
    }

}
