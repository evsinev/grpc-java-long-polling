package com.payneteasy.grpc.longpolling.test.serverstreaming;

import com.payneteasy.grpc.longpolling.test.util.SimpleJettyServer;
import io.grpc.ManagedChannel;
import io.grpc.examples.serverstreaming.ServerStreamingGreeterGrpc;
import io.grpc.examples.serverstreaming.TapHelloReply;
import io.grpc.examples.serverstreaming.TapHelloRequest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class ServerStreamingClientServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStreamingClientServerTest.class);


    @Test(timeout = 30_000)
    @Ignore
    public void test() throws InterruptedException {
        AtomicReference<TapHelloRequest> requestRef = new AtomicReference<>();
        SimpleJettyServer jettyServer = new SimpleJettyServer(9096, new ServerStreamingServiceImpl(requestRef));
        jettyServer.start();
        try {
            ManagedChannel clientChannel = jettyServer.createClientChannel();
            ServerStreamingGreeterGrpc.ServerStreamingGreeterBlockingStub client = ServerStreamingGreeterGrpc.newBlockingStub(clientChannel);
            TapHelloRequest hello = TapHelloRequest.newBuilder().setName("Hello").build();

            Iterator<TapHelloReply> replies = client.sayHelloStreaming(hello);
            LOG.debug("Call is done");
            Assert.assertEquals("hello 1", replies.next().getMessage());
            LOG.debug("Got first");
            Assert.assertEquals("hello 2", replies.next().getMessage());
            LOG.debug("Test is done");
            clientChannel.shutdown();
        } finally {
            jettyServer.shutdown();
        }

    }
}
