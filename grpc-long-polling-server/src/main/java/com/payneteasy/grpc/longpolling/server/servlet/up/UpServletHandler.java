package com.payneteasy.grpc.longpolling.server.servlet.up;

import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.server.LongPollingServerTransport;
import com.payneteasy.grpc.longpolling.server.servlet.ITransportRegistry;
import com.payneteasy.grpc.longpolling.server.servlet.MethodCall;
import com.payneteasy.grpc.longpolling.server.servlet.TransportHolder;
import io.grpc.internal.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UpServletHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UpServletHandler.class);

    private final LongPollingServerTransport serverTransport;
    private final ITransportRegistry         transportRegistry;

    public UpServletHandler(ITransportRegistry aRegistry, LongPollingServerTransport aTransport) {
        serverTransport   = aTransport;
        transportRegistry = aRegistry;
    }


    public void handle(HttpServletRequest aRequest, MethodCall aMethod, HttpServletResponse aResponse) throws IOException {
        LOG.debug("Finding 'UP' transport: {}", aMethod.getStreamId().getTransportId());
        TransportHolder transportHolder = transportRegistry.getOrCreateTransportListener(aMethod.getStreamId().getTransportId(), serverTransport);

        LOG.debug("Finding 'UP' stream: {}", aMethod.getStreamId());
        UpServerStream stream = transportHolder.getOrCreateStream(aMethod.getStreamId(), aMethod.getMethod());

        byte[] buffer = IoUtils.toByteArray(aRequest.getInputStream());
        SingleMessageProducer message = new SingleMessageProducer(getClass().getSimpleName(), buffer);
        stream.sendToGrpc(message);
    }
}
