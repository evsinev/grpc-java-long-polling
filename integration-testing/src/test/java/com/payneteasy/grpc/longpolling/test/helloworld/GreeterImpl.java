package com.payneteasy.grpc.longpolling.test.helloworld;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GreeterImpl.class);

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        LOG.debug("sayHello: {}, {}", req, responseObserver);
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

