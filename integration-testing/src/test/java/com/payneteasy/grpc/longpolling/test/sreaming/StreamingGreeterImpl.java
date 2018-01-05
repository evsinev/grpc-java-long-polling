package com.payneteasy.grpc.longpolling.test.sreaming;

import io.grpc.examples.manualflowcontrol.HelloReply;
import io.grpc.examples.manualflowcontrol.HelloRequest;
import io.grpc.examples.manualflowcontrol.StreamingGreeterGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamingGreeterImpl extends StreamingGreeterGrpc.StreamingGreeterImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingGreeterImpl.class);

    @Override
    public StreamObserver<HelloRequest> sayHelloStreaming(StreamObserver<HelloReply> aResponse) {

        aResponse.onNext(HelloReply.newBuilder().setMessage("hello").build());

        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest value) {
                LOG.info("onNext({})", value);
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
