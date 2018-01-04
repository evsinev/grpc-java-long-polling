package com.payneteasy.grpc.longpolling.server.servlet;

import com.google.common.base.Preconditions;
import com.payneteasy.grpc.longpolling.server.LongPollingServerTransport;
import com.payneteasy.grpc.longpolling.server.servlet.unary.UnaryHandler;
import io.grpc.internal.ServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LongPollingDispatcherServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingDispatcherServlet.class);

    private final ServerListener serverListener;
    private final UnaryHandler unaryServlet;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);


    public LongPollingDispatcherServlet(ServerListener aListener) {
        Preconditions.checkNotNull(aListener, "ServerListener must not be null");
        serverListener = aListener;
        unaryServlet = new UnaryHandler(aListener, new LongPollingServerTransport(executor));
    }

    @Override
    protected void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
        MethodCall call;
        try {
            call = MethodCall.parse(aRequest.getPathInfo());
        } catch (Exception e) {
            LOG.error("Error while parsing " + aRequest.getRequestURL(), e);
            aResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        LOG.debug("Method call: {}", call);

        switch (call.getType()) {
            case UNARY:
                unaryServlet.handle(aRequest, call, aResponse);
                break;

            default:
                aResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

//        AsyncContext asyncContext = aRequest.startAsync();
    }

    @Override
    public void destroy() {
        LOG.trace("destroy()");
        serverListener.serverShutdown();
    }

}
