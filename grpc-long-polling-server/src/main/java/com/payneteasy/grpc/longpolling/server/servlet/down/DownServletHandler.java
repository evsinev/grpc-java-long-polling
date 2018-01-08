package com.payneteasy.grpc.longpolling.server.servlet.down;

import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.grpc.longpolling.server.servlet.MethodCall;
import com.payneteasy.grpc.longpolling.server.servlet.registry.ITransportRegistry;
import com.payneteasy.grpc.longpolling.server.servlet.registry.StreamHolder;
import com.payneteasy.grpc.longpolling.server.servlet.registry.TransportHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DownServletHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DownServletHandler.class);

    private final ITransportRegistry registry;

    public DownServletHandler(ITransportRegistry registry) {
        this.registry = registry;
    }

    // todo use async servlet for waiting messages
    public void handle(MethodCall aMethod, HttpServletResponse aResponse) throws IOException, InterruptedException {
        LOG.debug("Waiting for new messages ...");
        TransportHolder transportHolder = registry.findTransportHolder(aMethod.getStreamId().getTransportId());
        if(transportHolder.isActive()) {
            StreamHolder      streamHolder = transportHolder.getOrCreateUpStream(aMethod.getStreamId(), aMethod.getMethod());
            MessagesContainer messages     = streamHolder.awaitMessages(180_000);
            if(!messages.isEmpty()) {
                LOG.debug("Write messages ...");
                messages.writeToOutput(aResponse.getOutputStream());
            } else {
                LOG.debug("No messages");
            }
        } else {
            aResponse.sendError(HttpServletResponse.SC_GONE);
        }
    }
}
