package com.payneteasy.grpc.longpolling.test.downstreaming;

import io.grpc.examples.downstreaming.DownHelloReply;
import io.grpc.examples.downstreaming.DownHelloRequest;
import io.grpc.examples.downstreaming.DownStreamingGreeterGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class DownStreamingServiceImpl extends DownStreamingGreeterGrpc.DownStreamingGreeterImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(DownStreamingServiceImpl.class);

    private final AtomicReference<DownHelloRequest> requestRef;

    public DownStreamingServiceImpl(AtomicReference<DownHelloRequest> requestRef) {
        this.requestRef = requestRef;
    }

    @Override
    public void sayHelloStreaming(DownHelloRequest aRequest, StreamObserver<DownHelloReply> aResponse) {
        LOG.debug("sayHelloStreaming({})", aRequest, aResponse);
        requestRef.set(aRequest);

        aResponse.onNext(DownHelloReply.newBuilder().setMessage("hello 1").build());
        aResponse.onNext(DownHelloReply.newBuilder().setMessage("hello 2").build());
        aResponse.onCompleted();
    }
}
