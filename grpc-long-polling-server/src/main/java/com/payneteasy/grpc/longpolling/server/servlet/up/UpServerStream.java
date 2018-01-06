package com.payneteasy.grpc.longpolling.server.servlet.up;

import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.common.TransportId;
import com.payneteasy.grpc.longpolling.server.base.AbstractNoopServerStream;
import com.payneteasy.grpc.longpolling.server.servlet.ITransportRegistry;
import io.grpc.Metadata;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class UpServerStream extends AbstractNoopServerStream {

    private static final Logger LOG = LoggerFactory.getLogger(UpServerStream.class);

    private final    ITransportRegistry                        transportRegistry;
    private final    TransportId                               transportId;
    private final    StreamId                                  streamId;
    private final    ArrayBlockingQueue<SingleMessageProducer> grpcInboundQueue;
    private final    ArrayBlockingQueue<Integer>               grpcSlots;

    public UpServerStream(ITransportRegistry aTransportRegistry, StreamId aStreamId) {
        super(LOG);
        transportRegistry = aTransportRegistry;
        transportId       = aStreamId.getTransportId();
        streamId          = aStreamId;
        grpcInboundQueue  = new ArrayBlockingQueue<>(10);
        grpcSlots         = new ArrayBlockingQueue<>(10);
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
        for(int i=0; i<aMax; i++) {
            grpcSlots.add(aMax);
        }
        sendToGrpcFromQueue();
    }

    private void sendToGrpcFromQueue() {
        LOG.debug("Walking through grpc inbound queue [size={}, slots={}] ...", grpcInboundQueue.size(), grpcSlots.size());
        for(int i=0; !grpcSlots.isEmpty() && !grpcInboundQueue.isEmpty(); i++) {
            Integer               slot    = grpcSlots.poll();
            SingleMessageProducer message = grpcInboundQueue.poll();
            LOG.debug("Sending message #{}, slot={}, message={}...", i, slot, message);
            if(message != null) {
                listener.messagesAvailable(message);
            }
        }
    }

    public void sendToGrpc(SingleMessageProducer aOutputMessage) {
        LOG.debug("Adding a message to grpc inbound queue [size={}, slots={}] ...", grpcInboundQueue.size(), grpcSlots.size());
        grpcInboundQueue.add(aOutputMessage);
        sendToGrpcFromQueue();
    }

    @Override
    public void writeMessage(InputStream aMessage) {
        LOG.trace("writeMessage({})", aMessage);
        transportRegistry.enqueueMessage(streamId, aMessage);
    }

}
