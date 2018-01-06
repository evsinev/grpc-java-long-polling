package com.payneteasy.grpc.longpolling.test.helloworld;

import com.payneteasy.grpc.longpolling.client.LongPollingChannelBuilder;
import com.payneteasy.grpc.longpolling.server.LongPollingServer;
import com.payneteasy.grpc.longpolling.server.servlet.LongPollingDispatcherServlet;
import com.payneteasy.grpc.longpolling.test.util.ServerUtils;
import com.payneteasy.grpc.longpolling.test.util.SimpleJettyServer;
import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.internal.ServerListener;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HelloWorldClientServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldClientServerTest.class);

    @Test(timeout = 10_000)
    public void test() {
        LongPollingServer pollingServer = ServerUtils.createLongPollingServer(new GreeterImpl());
        ServerListener serverListener = pollingServer.waitForServerListener();

        SimpleJettyServer server = new SimpleJettyServer(9096, new LongPollingDispatcherServlet(serverListener));
        try {
            server.start();

            GreeterGrpc.GreeterBlockingStub service = createService();

            for(int i=0; i<10; i++) {
                HelloRequest request = HelloRequest.newBuilder().setName("send " + i).build();
                HelloReply reply = service.sayHello(request);
                LOG.debug("reply: {}", reply);
                Assert.assertEquals("Hello send " + i, reply.getMessage());
            }

        } finally {
            server.shutdown();
        }
    }

    private GreeterGrpc.GreeterBlockingStub createService() {
        ManagedChannel channel = LongPollingChannelBuilder.forTarget("http://localhost:9096/test").build();
        return GreeterGrpc
                .newBlockingStub(channel)
                .withDeadlineAfter(5, TimeUnit.SECONDS);
    }
}
