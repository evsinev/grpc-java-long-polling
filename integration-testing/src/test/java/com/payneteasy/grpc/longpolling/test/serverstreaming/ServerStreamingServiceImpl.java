package com.payneteasy.grpc.longpolling.test.serverstreaming;

import io.grpc.examples.serverstreaming.ServerStreamingGreeterGrpc;
import io.grpc.examples.serverstreaming.TapHelloReply;
import io.grpc.examples.serverstreaming.TapHelloRequest;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

public class ServerStreamingServiceImpl extends ServerStreamingGreeterGrpc.ServerStreamingGreeterImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStreamingServiceImpl.class);

    private final ArrayBlockingQueue<TapHelloRequest> queue;

    public ServerStreamingServiceImpl(ArrayBlockingQueue<TapHelloRequest> aQueue) {
        queue = aQueue;
    }

    @Override
    public void sayHelloStreaming(TapHelloRequest aRequest, StreamObserver<TapHelloReply> aResponse) {
        LOG.debug("sayHelloStreaming({})", aRequest, aResponse);
        queue.add(aRequest);

        aResponse.onNext(TapHelloReply.newBuilder().setMessage("hello 1").build());
        aResponse.onNext(TapHelloReply.newBuilder().setMessage("hello 2").build());
        aResponse.onCompleted();
    }
}
