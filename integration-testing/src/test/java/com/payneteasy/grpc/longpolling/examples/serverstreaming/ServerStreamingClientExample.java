package com.payneteasy.grpc.longpolling.examples.serverstreaming;

import com.payneteasy.grpc.longpolling.client.LongPollingChannelBuilder;
import io.grpc.ManagedChannel;
import io.grpc.examples.serverstreaming.ServerStreamingGreeterGrpc;
import io.grpc.examples.serverstreaming.TapHelloReply;
import io.grpc.examples.serverstreaming.TapHelloRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class ServerStreamingClientExample {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStreamingClientExample.class);
    
    public static void main(String[] args) {
        ManagedChannel channel = LongPollingChannelBuilder.forTarget("http://localhost:9095/test").build();

        ServerStreamingGreeterGrpc.ServerStreamingGreeterBlockingStub client
                = ServerStreamingGreeterGrpc.newBlockingStub(channel);

        for(int i=0; i<100; i++) {
            TapHelloRequest hello = TapHelloRequest.newBuilder().setName("Hello").build();
            Iterator<TapHelloReply> replies = client.sayHelloStreaming(hello);
            while (replies.hasNext()) {
                TapHelloReply next = replies.next();
                LOG.debug("{}: message = {}", i, next);

            }
        }
    }
}
