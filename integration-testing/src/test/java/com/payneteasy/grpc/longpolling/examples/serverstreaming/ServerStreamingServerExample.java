package com.payneteasy.grpc.longpolling.examples.serverstreaming;

import com.payneteasy.grpc.longpolling.test.util.SimpleJettyServer;
import io.grpc.examples.serverstreaming.ServerStreamingGreeterGrpc;
import io.grpc.examples.serverstreaming.TapHelloReply;
import io.grpc.examples.serverstreaming.TapHelloRequest;
import io.grpc.stub.StreamObserver;

public class ServerStreamingServerExample {

    public static void main(String[] args) {
        SimpleJettyServer server = new SimpleJettyServer(9095, new Service());
        try {
            server.startAndJoin();
        } finally {
            server.shutdown();
        }
    }

    private static class Service extends ServerStreamingGreeterGrpc.ServerStreamingGreeterImplBase {
        @Override
        public void sayHelloStreaming(TapHelloRequest request, StreamObserver<TapHelloReply> responseObserver) {
            responseObserver.onNext(TapHelloReply.newBuilder().setMessage("send 1 " + request.getName()).build());
            responseObserver.onNext(TapHelloReply.newBuilder().setMessage("send 2 " + request.getName()).build());
            responseObserver.onCompleted();
        }
    }

}
