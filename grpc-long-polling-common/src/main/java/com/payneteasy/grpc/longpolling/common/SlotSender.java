package com.payneteasy.grpc.longpolling.common;

import org.slf4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;

public class SlotSender<T> {


    public interface SendFunction<T> {
        void sendMessage(T aMessage);
    }

    private final    ArrayBlockingQueue<T>       messages;
    private final    ArrayBlockingQueue<Integer> slots;
    private final    SendFunction<T>             sendFunction;
    private final    Logger                      logger;

    public SlotSender(Logger aLogger, SendFunction<T> aSendFunction) {
        messages     = new ArrayBlockingQueue<>(10);
        slots        = new ArrayBlockingQueue<>(10);
        sendFunction = aSendFunction;
        logger       = aLogger;
    }

    public void onRequest(int aCount) {
        for(int i=0; i<aCount; i++) {
            slots.add(aCount);
        }
        sendFromQueue();
    }

    public void onSendMessage(T aMessage) {
        messages.add(aMessage);
        sendFromQueue();
    }

    private void sendFromQueue() {
        logger.debug("SlotSender: Walking through queue [size={}, slots={}] ...", messages.size(), slots.size());
        for(int i=0; !slots.isEmpty() && !messages.isEmpty(); i++) {
            Integer slot    = slots.poll();
            T       message = messages.poll();
            logger.debug("SlotSender: Sending message #{}, slot={}, message={}...", i, slot, message);
            if(message != null) {
                sendFunction.sendMessage(message);
            }
        }
    }

}
