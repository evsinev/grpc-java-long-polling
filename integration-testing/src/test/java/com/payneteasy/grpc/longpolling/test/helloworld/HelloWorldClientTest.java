package com.payneteasy.grpc.longpolling.test.helloworld;

import com.payneteasy.grpc.longpolling.client.LongPollingChannelBuilder;
import com.payneteasy.tlv.HexUtil;
import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HelloWorldClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldClientTest.class);

    @Test(timeout = 10_000)
    public void test() {
        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.getOutputStream().write(HexUtil.parseHex("0a06 7465 7374 2032"));

            }
        };

        HelloWorldServer server = new HelloWorldServer(9096, servlet);
        server.start();

        ManagedChannel channel = LongPollingChannelBuilder.forTarget("http://localhost:9096/test").build();
        GreeterGrpc.GreeterBlockingStub service = GreeterGrpc
                .newBlockingStub(channel)
                .withDeadlineAfter(5, TimeUnit.SECONDS);

        HelloRequest request = HelloRequest.newBuilder().setName("hello").build();
        HelloReply reply = service.sayHello(request);
        LOG.debug("reply: {}", reply);
        Assert.assertEquals("test 2", reply.getMessage());

        server.shutdown();

    }
}
