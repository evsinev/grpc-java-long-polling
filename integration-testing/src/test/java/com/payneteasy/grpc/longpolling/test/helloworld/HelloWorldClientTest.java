package com.payneteasy.grpc.longpolling.test.helloworld;

import com.payneteasy.grpc.longpolling.client.LongPollingChannelBuilder;
import com.payneteasy.grpc.longpolling.server.GrpcLongPollingServlet;
import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HelloWorldClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldClientTest.class);

    @Test(timeout = 10_000)
    public void test() {
        HelloWorldServer server = new HelloWorldServer(9096, new GrpcLongPollingServlet());
        server.start();

        ManagedChannel channel = LongPollingChannelBuilder.forTarget("http://localhost:9096/test").build();
        GreeterGrpc.GreeterBlockingStub service = GreeterGrpc
                .newBlockingStub(channel)
                .withDeadlineAfter(5, TimeUnit.SECONDS);

        HelloRequest request = HelloRequest.newBuilder().setName("hello").build();
        HelloReply reply = service.sayHello(request);
        LOG.debug("reply: {}", reply);
        Assert.assertEquals("test 2", reply.getMessage());

        server.shutdown();

    }
}
