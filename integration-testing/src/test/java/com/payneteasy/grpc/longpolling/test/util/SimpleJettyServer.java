package com.payneteasy.grpc.longpolling.test.util;

import com.payneteasy.grpc.longpolling.client.LongPollingChannelBuilder;
import com.payneteasy.grpc.longpolling.server.LongPollingServer;
import com.payneteasy.grpc.longpolling.server.servlet.LongPollingDispatcherServlet;
import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.internal.ServerListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;

public class SimpleJettyServer {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleJettyServer.class);

    private final Server server;

    public SimpleJettyServer(int aPort, BindableService aBindableService) {
        this(aPort, createServlet(aBindableService));
    }

    private static HttpServlet createServlet(BindableService aBindableService) {
        LongPollingServer pollingServer = ServerUtils.createLongPollingServer(aBindableService);
        ServerListener serverListener = pollingServer.waitForServerListener();
        return new LongPollingDispatcherServlet(serverListener);
    }

    public SimpleJettyServer(int aPort, HttpServlet aServlet) {
        server = new Server(aPort);

        ServletContextHandler context = new ServletContextHandler(server, "/test", ServletContextHandler.NO_SESSIONS);

        ServletHolder servletHolder = new ServletHolder(aServlet);
        servletHolder.setAsyncSupported(true);
        context.addServlet(servletHolder, "/*");

    }

    public ManagedChannel createClientChannel() {
        return LongPollingChannelBuilder.forTarget("http://localhost:9096/test").build();
    }

    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void shutdown() {
        LOG.debug("Shutdown");
        try {
            server.stop();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
