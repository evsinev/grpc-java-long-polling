package com.payneteasy.grpc.longpolling.server.servlet.down;

import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.grpc.longpolling.server.servlet.ITransportRegistry;
import com.payneteasy.grpc.longpolling.server.servlet.MethodCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DownServletHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DownServletHandler.class);

    private final ITransportRegistry registry;

    public DownServletHandler(ITransportRegistry registry) {
        this.registry = registry;
    }

    public void handle(MethodCall aMethod, HttpServletRequest aRequest, HttpServletResponse aResponse) throws IOException {
        try {
            MessagesContainer messages = registry.getReadyMessages(aMethod.getStreamId(), aMethod.getMethod());
            // todo use async servlet for waiting messages
            if(!messages.isEmpty()) {
                messages.writeToOutput(aResponse.getOutputStream());
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupted", e);
        }
    }
}
