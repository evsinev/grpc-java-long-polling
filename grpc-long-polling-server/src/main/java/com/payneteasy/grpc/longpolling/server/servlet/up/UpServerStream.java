package com.payneteasy.grpc.longpolling.server.servlet.up;

import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.common.SlotSender;
import com.payneteasy.grpc.longpolling.server.base.AbstractNoopServerStream;
import com.payneteasy.grpc.longpolling.server.servlet.registry.MessagesHolder;
import com.payneteasy.grpc.longpolling.server.servlet.registry.TransportHolder;
import io.grpc.KnownLength;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ServerStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class UpServerStream extends AbstractNoopServerStream {

    private static final Logger LOG = LoggerFactory.getLogger(UpServerStream.class);

    private final    SlotSender<SingleMessageProducer> slotSender;
    private final    MessagesHolder                    messages;
    private final    TransportHolder                   transportHolder;

    public interface IActionAfterMessageAvailable {
        void process(ServerStreamListener aListener);
    }

    public UpServerStream(TransportHolder aTransportHolder, MessagesHolder aMessages, IActionAfterMessageAvailable aActionAfterMessageAvailable) {
        super(LOG);
        transportHolder   = aTransportHolder;
        slotSender        = new SlotSender<>(LOG, aMessage -> {
            listener.messagesAvailable(aMessage);
            aActionAfterMessageAvailable.process(listener);
        });
        messages          = aMessages;
    }

    @Override
    public void writeHeaders(Metadata headers) {
        LOG.trace("writeHeaders({})", headers);
    }

    @Override
    public void close(Status status, Metadata trailers) {
        if(status != Status.OK) {
            LOG.warn("Transport closed: {}, {}", status, trailers);
        }
        transportHolder.markAsDisabled();
    }

    @Override
    public void cancel(Status status) {
        LOG.trace("cancel({})", status);
        transportHolder.markAsDisabled();
    }

    @Override
    public void request(int aMax) {
        LOG.trace("request({})", aMax);
        slotSender.onRequest(aMax);
    }

    public void sendToGrpc(SingleMessageProducer aOutputMessage) {
        LOG.trace("sendToGrpc({})", aOutputMessage);
        slotSender.onSendMessage(aOutputMessage);
    }

    @Override
    public void writeMessage(InputStream aMessage) {
        String size = "";
        if(aMessage instanceof KnownLength) {
            try {
                size = "size=" + aMessage.available() + ",";
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        LOG.trace("writeMessage({})", size + aMessage);
        messages.addMessage(aMessage);
    }

}
