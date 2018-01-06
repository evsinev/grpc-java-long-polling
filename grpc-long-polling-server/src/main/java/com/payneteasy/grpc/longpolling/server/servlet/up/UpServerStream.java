package com.payneteasy.grpc.longpolling.server.servlet.up;

import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.common.SlotSender;
import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.common.TransportId;
import com.payneteasy.grpc.longpolling.server.base.AbstractNoopServerStream;
import com.payneteasy.grpc.longpolling.server.servlet.ITransportRegistry;
import io.grpc.Metadata;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class UpServerStream extends AbstractNoopServerStream {

    private static final Logger LOG = LoggerFactory.getLogger(UpServerStream.class);

    private final    ITransportRegistry                        transportRegistry;
    private final    TransportId                               transportId;
    private final    StreamId                                  streamId;
    private final    SlotSender<SingleMessageProducer>         slotSender;

    public UpServerStream(ITransportRegistry aTransportRegistry, StreamId aStreamId) {
        super(LOG);
        transportRegistry = aTransportRegistry;
        transportId       = aStreamId.getTransportId();
        streamId          = aStreamId;
        slotSender        = new SlotSender<>(LOG, aMessage -> listener.messagesAvailable(aMessage));
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
        transportRegistry.removeTransport(transportId);
    }

    @Override
    public void cancel(Status status) {
        LOG.trace("cancel({})", status);
        transportRegistry.removeTransport(transportId);
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
        LOG.trace("writeMessage({})", aMessage);
        transportRegistry.enqueueMessage(streamId, aMessage);
    }

}
