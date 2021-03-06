package com.payneteasy.grpc.longpolling.server.servlet.down;

import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.grpc.longpolling.server.servlet.MethodCall;
import com.payneteasy.grpc.longpolling.server.servlet.ServletOptions;
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
    private final ServletOptions     options;

    public DownServletHandler(ITransportRegistry registry, ServletOptions aOptions) {
        this.registry = registry;
        options = aOptions;
    }

    // todo use async servlet for waiting messages
    public void handle(MethodCall aMethod, HttpServletResponse aResponse) throws IOException, InterruptedException {
        TransportHolder transportHolder = registry.findTransportHolder(aMethod.getStreamId().getTransportId());
        StreamHolder      streamHolder = transportHolder.getOrCreateUpStream(aMethod.getStreamId(), aMethod.getMethod());

        if(streamHolder.isActive()) {
            MessagesContainer messages = streamHolder.awaitMessages(options.getReadTimeout());
            if(!messages.isEmpty()) {
                LOG.debug("{}: Write messages ...", aMethod.getStreamId());
                messages.writeToOutput(aResponse.getOutputStream());
            } else {
                LOG.debug("{}: No messages", aMethod.getStreamId());
            }
        } else {
            LOG.debug("{}: Stream is inactive. Sending SC_GONE ...", aMethod.getStreamId());
            aResponse.sendError(HttpServletResponse.SC_GONE);
        }

    }
}
