package com.payneteasy.grpc.longpolling.test.helloworld;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;

public class HelloWorldServer {


    private final Server server;

    public HelloWorldServer(int aPort, HttpServlet aServlet) {
        server = new Server(aPort);

        ServletContextHandler context = new ServletContextHandler(server, "/test", ServletContextHandler.NO_SESSIONS);

        ServletHolder servletHolder = new ServletHolder(aServlet);
        servletHolder.setAsyncSupported(true);
        context.addServlet(servletHolder, "/*");

    }

    public void start() {
        try {
            server.start();
//            server.dumpStdErr();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void shutdown() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
