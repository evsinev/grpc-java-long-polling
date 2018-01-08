package com.payneteasy.grpc.longpolling.server.servlet.up;

import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.server.servlet.MethodCall;
import com.payneteasy.grpc.longpolling.server.servlet.registry.ITransportRegistry;
import com.payneteasy.grpc.longpolling.server.servlet.registry.StreamHolder;
import com.payneteasy.grpc.longpolling.server.servlet.registry.TransportHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.payneteasy.grpc.longpolling.common.SingleMessageProducer.readFully;

public class UpServletHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UpServletHandler.class);

    private final ITransportRegistry         transportRegistry;

    public UpServletHandler(ITransportRegistry aRegistry) {
        transportRegistry = aRegistry;
    }

    public void handle(HttpServletRequest aRequest, MethodCall aMethod) throws IOException {
        LOG.debug("Finding 'UP' transport: {}", aMethod.getStreamId().getTransportId());
        TransportHolder transportHolder = transportRegistry.findTransportHolder(aMethod.getStreamId().getTransportId());

        LOG.debug("Finding 'UP' stream   : {}", aMethod.getStreamId());
        SingleMessageProducer message = readFully(getClass(), aRequest.getInputStream());

        StreamHolder streamHolder = transportHolder.getOrCreateUpStream(aMethod.getStreamId(), aMethod.getMethod());
        UpServerStream upStream = streamHolder.getUpStream();
        upStream.sendToGrpc(message);
    }
}
