package com.payneteasy.grpc.longpolling.server.servlet.tap;

import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.server.servlet.MethodCall;
import com.payneteasy.grpc.longpolling.server.servlet.registry.ITransportRegistry;
import com.payneteasy.grpc.longpolling.server.servlet.registry.StreamHolder;
import com.payneteasy.grpc.longpolling.server.servlet.registry.TransportHolder;
import com.payneteasy.grpc.longpolling.server.servlet.up.UpServerStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.payneteasy.grpc.longpolling.common.SingleMessageProducer.readFully;

public class TapServletHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TapServletHandler.class);

    private final ITransportRegistry transportRegistry;

    public TapServletHandler(ITransportRegistry aTransportRegistry) {
        transportRegistry = aTransportRegistry;
    }

    public void handle(MethodCall aMethod, HttpServletRequest aRequest) throws IOException {
        // get input
        // send via slots
        // wait for message from grpc
        // write or timeout
        LOG.debug("Finding transport: {}", aMethod.getStreamId().getTransportId());
        TransportHolder transportHolder = transportRegistry.findTransportHolder(aMethod.getStreamId().getTransportId());

        LOG.debug("Finding 'TAP(UP)' stream   : {}", aMethod.getStreamId());

        StreamHolder streamHolder = transportHolder.createTapStream(aMethod.getStreamId(), aMethod.getMethod());
        UpServerStream upStream = streamHolder.getUpStream();

        SingleMessageProducer message = readFully(getClass(), aRequest.getInputStream());
        upStream.sendToGrpc(message);

    }
}
