package com.payneteasy.grpc.longpolling.test.sreaming;

import io.grpc.examples.manualflowcontrol.HelloReply;
import io.grpc.examples.manualflowcontrol.HelloRequest;
import io.grpc.examples.manualflowcontrol.StreamingGreeterGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class StreamingGreeterImpl extends StreamingGreeterGrpc.StreamingGreeterImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingGreeterImpl.class);

    private final CountDownLatch latch;

    public StreamingGreeterImpl(CountDownLatch aLatch) {
        latch = aLatch;
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloStreaming(StreamObserver<HelloReply> aResponse) {

        LOG.debug("Sending reply...");
        aResponse.onNext(HelloReply.newBuilder().setMessage("hello").build());
        aResponse.onNext(HelloReply.newBuilder().setMessage("hello").build());

        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest value) {
                LOG.debug("onNext({})", value);
                latch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                LOG.error("onError()", t);
            }

            @Override
            public void onCompleted() {
                LOG.error("onCompleted()");
            }
        };
    }
}
