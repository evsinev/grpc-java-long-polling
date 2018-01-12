package com.payneteasy.grpc.longpolling.examples.helloworld;

import com.payneteasy.grpc.longpolling.client.LongPollingChannelBuilder;
import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HelloWorldClientExample {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldClientExample.class);

    public static void main(String[] args) {
        LOG.error("test {}", new Exception());
        ManagedChannel channel = LongPollingChannelBuilder.forTarget("http://localhost:9095/test").build();
        GreeterGrpc.GreeterBlockingStub service = GreeterGrpc
                .newBlockingStub(channel)
                .withDeadlineAfter(5, TimeUnit.SECONDS);

        HelloRequest request = HelloRequest.newBuilder().setName("hello").build();
        HelloReply reply = service.sayHello(request);
        LOG.debug("reply: {}", reply);
    }
}
