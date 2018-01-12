package com.payneteasy.grpc.longpolling.server.servlet.unary;

import com.payneteasy.grpc.longpolling.server.LongPollingServerTransport;
import com.payneteasy.grpc.longpolling.server.servlet.MethodCall;
import io.grpc.Attributes;
import io.grpc.Metadata;
import io.grpc.internal.IoUtils;
import io.grpc.internal.ServerListener;
import io.grpc.internal.ServerTransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class UnaryHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UnaryHandler.class);

    private final ServerListener listener;
    private final LongPollingServerTransport serverTransport;

    public UnaryHandler(ServerListener listener, LongPollingServerTransport aTransport) {
        this.listener = listener;
        serverTransport = aTransport;
    }

    public void handle(HttpServletRequest aRequest, MethodCall aMethod, HttpServletResponse aResponse) throws IOException {
        LOG.debug("Creating unary transport and stream: {}", aMethod.getStreamId());
        ServerTransportListener listener = this.listener.transportCreated(serverTransport);

        byte[] buffer = IoUtils.toByteArray(aRequest.getInputStream());
        UnaryServerStream stream = new UnaryServerStream(buffer, aResponse, aMethod.getStreamId());
        listener.streamCreated(stream, aMethod.getMethod(), new Metadata());
        listener.transportReady(Attributes.EMPTY);

        try {
            LOG.debug("Waiting while sending response to servlet output ...");
            if(!stream.waitDone(1, TimeUnit.MINUTES)) {
                LOG.error("No response from gRPC service");
                aResponse.sendError(HttpServletResponse.SC_BAD_GATEWAY);
            }
        } catch (InterruptedException e) {
            LOG.error("Cannot wait 1 min", e);
            Thread.currentThread().interrupt();
        } finally {
            listener.transportTerminated();
            LOG.debug("Transport {} terminated", aMethod.getStreamId().getTransportId());
        }
    }
}
