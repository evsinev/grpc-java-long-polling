package com.payneteasy.grpc.longpolling.server.servlet.down;

import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.grpc.longpolling.server.servlet.ITransportRegistry;
import com.payneteasy.grpc.longpolling.server.servlet.MethodCall;
import com.payneteasy.grpc.longpolling.server.servlet.TransportHolder;
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

    public void handle(MethodCall aMethod, HttpServletResponse aResponse) throws IOException {
        try {
            LOG.debug("Waiting for new messages ...");
            TransportHolder holder = registry.getReadyMessages(aMethod.getStreamId(), aMethod.getMethod());
            if(holder.isActive()) {
                MessagesContainer messages = holder.getMessages(180_000);
                // todo use async servlet for waiting messages
                if(!messages.isEmpty()) {
                    LOG.debug("Write messages ...");
                    messages.writeToOutput(aResponse.getOutputStream());
                } else {
                    LOG.debug("No messages");
                }
            } else {
                aResponse.sendError(HttpServletResponse.SC_GONE);
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupted", e);
        }
    }
}
