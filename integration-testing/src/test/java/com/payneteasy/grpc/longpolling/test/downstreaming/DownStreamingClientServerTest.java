package com.payneteasy.grpc.longpolling.test.downstreaming;

import com.payneteasy.grpc.longpolling.test.util.SimpleJettyServer;
import io.grpc.ManagedChannel;
import io.grpc.examples.downstreaming.DownHelloReply;
import io.grpc.examples.downstreaming.DownHelloRequest;
import io.grpc.examples.downstreaming.DownStreamingGreeterGrpc;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class DownStreamingClientServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(DownStreamingClientServerTest.class);


    @Test(timeout = 30_000)
    @Ignore
    public void test() throws InterruptedException {
        AtomicReference<DownHelloRequest> requestRef = new AtomicReference<>();
        SimpleJettyServer jettyServer = new SimpleJettyServer(9096, new DownStreamingServiceImpl(requestRef));
        jettyServer.start();
        try {
            ManagedChannel clientChannel = jettyServer.createClientChannel();
            DownStreamingGreeterGrpc.DownStreamingGreeterBlockingStub client = DownStreamingGreeterGrpc.newBlockingStub(clientChannel);
            DownHelloRequest hello = DownHelloRequest.newBuilder().setName("Hello").build();

            Iterator<DownHelloReply> replies = client.sayHelloStreaming(hello);
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
