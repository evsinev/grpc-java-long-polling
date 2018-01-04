package com.payneteasy.grpc.longpolling.test.helloworld;

import com.payneteasy.grpc.longpolling.server.GrpcLongPollingServlet;
import org.junit.Test;

public class HelloWorldTest {

    @Test
    public void test() {
        HelloWorldServer server = new HelloWorldServer(9096, new GrpcLongPollingServlet());
        server.start();
        
        server.shutdown();
    }
}
