package com.payneteasy.grpc.longpolling.examples.helloworld;

import com.payneteasy.grpc.longpolling.test.helloworld.GreeterImpl;
import com.payneteasy.grpc.longpolling.test.util.SimpleJettyServer;

public class HelloWorldServerExample {

    public static void main(String[] args) {
        SimpleJettyServer server = new SimpleJettyServer(9095, new GreeterImpl());
        try {
            server.startAndJoin();
        } finally {
            server.shutdown();
        }
    }
}
