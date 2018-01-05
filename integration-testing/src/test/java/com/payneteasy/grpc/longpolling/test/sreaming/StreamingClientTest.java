package com.payneteasy.grpc.longpolling.test.sreaming;

import com.payneteasy.grpc.longpolling.client.LongPollingChannelBuilder;
import com.payneteasy.grpc.longpolling.test.helloworld.HelloWorldServer;
import com.payneteasy.tlv.HexUtil;
import io.grpc.ManagedChannel;
import io.grpc.examples.manualflowcontrol.HelloReply;
import io.grpc.examples.manualflowcontrol.HelloRequest;
import io.grpc.examples.manualflowcontrol.StreamingGreeterGrpc;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StreamingClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingClientTest.class);

    @Test(timeout = 10_000)
    public void test() throws InterruptedException {

        HttpServlet servlet = new HttpServlet() {

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                LOG.info("GET: {}", req.getRequestURI());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resp.getOutputStream().write(HexUtil.parseHex("0a06 7465 7374 2032"));
            }

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                LOG.info("POST: {}", req.getRequestURI());
                resp.getOutputStream().write(HexUtil.parseHex("0a06 7465 7374 2032"));
            }
        };

        HelloWorldServer server = new HelloWorldServer(9096, servlet);
        server.start();

        try {

            ManagedChannel channel = LongPollingChannelBuilder
                    .forTarget("http://localhost:9096/test")
                    .usePlaintext(true)
                    .build();

            StreamingGreeterGrpc.StreamingGreeterStub stub = StreamingGreeterGrpc.newStub(channel);

            CountDownLatch latch = new CountDownLatch(3);

            ClientResponseObserver<HelloRequest, HelloReply> observer = new ClientResponseObserver<HelloRequest, HelloReply>() {
                @Override
                public void beforeStart(ClientCallStreamObserver<HelloRequest> aRequestStream) {
                    aRequestStream.disableAutoInboundFlowControl();
                    aRequestStream.setOnReadyHandler(() -> {
                        aRequestStream.onNext(HelloRequest.newBuilder().setName("test 1").build());
                        aRequestStream.onNext(HelloRequest.newBuilder().setName("test 2").build());
                    });
                }

                @Override
                public void onNext(HelloReply value) {
                    LOG.debug("onNext({})", value);
                    latch.countDown();
                }

                @Override
                public void onError(Throwable t) {
                    LOG.debug("onError()", t);
                }

                @Override
                public void onCompleted() {
                    LOG.debug("onCompleted()");
                }
            };

            stub.sayHelloStreaming(observer);

            LOG.info("Waiting 5 seconds ...");
            Assert.assertTrue("We should receive 3 messages", latch.await(5, TimeUnit.SECONDS));

            channel.shutdown();

        } finally {
            server.shutdown();

        }
    }
}
