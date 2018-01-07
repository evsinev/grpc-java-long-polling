package com.payneteasy.grpc.longpolling.test.serverstreaming;

import io.grpc.examples.serverstreaming.ServerStreamingGreeterGrpc;
import io.grpc.examples.serverstreaming.TapHelloReply;
import io.grpc.examples.serverstreaming.TapHelloRequest;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class ServerStreamingServiceImpl extends ServerStreamingGreeterGrpc.ServerStreamingGreeterImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStreamingServiceImpl.class);

    private final AtomicReference<TapHelloRequest> requestRef;

    public ServerStreamingServiceImpl(AtomicReference<TapHelloRequest> requestRef) {
        this.requestRef = requestRef;
    }

    @Override
    public void sayHelloStreaming(TapHelloRequest aRequest, StreamObserver<TapHelloReply> aResponse) {
        LOG.debug("sayHelloStreaming({})", aRequest, aResponse);
        requestRef.set(aRequest);

        aResponse.onNext(TapHelloReply.newBuilder().setMessage("hello 1").build());
        aResponse.onNext(TapHelloReply.newBuilder().setMessage("hello 2").build());
        aResponse.onCompleted();
    }
}
