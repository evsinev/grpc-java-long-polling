package com.payneteasy.grpc.longpolling.test.serverstreaming;

import com.payneteasy.grpc.longpolling.test.util.SimpleJettyServer;
import com.payneteasy.tlv.HexUtil;
import io.grpc.ManagedChannel;
import io.grpc.examples.serverstreaming.ServerStreamingGreeterGrpc;
import io.grpc.examples.serverstreaming.TapHelloReply;
import io.grpc.examples.serverstreaming.TapHelloRequest;
import io.grpc.internal.IoUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class ServerStreamingClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStreamingClientTest.class);


    @Test(timeout = 10_000)
    public void test() throws InterruptedException {

        AtomicReference<String> requestHex = new AtomicReference<>();

        SimpleJettyServer jettyServer = new SimpleJettyServer(9096, new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

                byte[] bytes = IoUtils.toByteArray(req.getInputStream());
                requestHex.set(HexUtil.toFormattedHexString(bytes));

                byte[] output = HexUtil.parseHex("01 0a06 7465 7374 2031");
                resp.getOutputStream().write(output);
                LOG.debug("DOWN WRITTEN: {}", HexUtil.toFormattedHexString(output));
            }
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                byte[] output = HexUtil.parseHex("01 0a06 7465 7374 2032");
                resp.getOutputStream().write(output);
                LOG.debug("DOWN WRITTEN: {}", HexUtil.toFormattedHexString(output));
            }
        });
        jettyServer.start();
        try {
            ManagedChannel clientChannel = jettyServer.createClientChannel();
            ServerStreamingGreeterGrpc.ServerStreamingGreeterBlockingStub client = ServerStreamingGreeterGrpc.newBlockingStub(clientChannel);
            TapHelloRequest hello = TapHelloRequest.newBuilder().setName("Hello").build();

            Iterator<TapHelloReply> replies = client.sayHelloStreaming(hello);
            LOG.debug("Call is done");
            Assert.assertEquals("test 1", replies.next().getMessage());
            LOG.debug("Got first");
            Assert.assertEquals("test 2", replies.next().getMessage());
            Assert.assertEquals("[7] :  0A 05 48 65  6C 6C 6F", requestHex.get());
            LOG.debug("Test is done");
            clientChannel.shutdown();
        } finally {
            jettyServer.shutdown();
        }

    }
}
