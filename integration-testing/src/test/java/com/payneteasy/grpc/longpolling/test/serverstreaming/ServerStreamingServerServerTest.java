package com.payneteasy.grpc.longpolling.test.serverstreaming;

import com.payneteasy.grpc.longpolling.test.util.SimpleHttpClient;
import com.payneteasy.grpc.longpolling.test.util.SimpleJettyServer;
import io.grpc.examples.serverstreaming.TapHelloRequest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ServerStreamingServerServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStreamingServerServerTest.class);


    @Test(timeout = 10_000)
    public void test() throws InterruptedException, IOException {
        ArrayBlockingQueue<TapHelloRequest> queue = new ArrayBlockingQueue<>(1, true);
        SimpleJettyServer jettyServer = new SimpleJettyServer(9096, new ServerStreamingServiceImpl(queue));
        jettyServer.start();
        try {

            SimpleHttpClient http = jettyServer.createHttpClient();

            // TAP
            String responseHex = http.postHex(
                    "/tap.ServerStreamingGreeter/SayHelloStreaming/TAP/2/1"
                    , "0A 05 48 65  6C 6C 6F");
            Assert.assertEquals("[0] :", responseHex);

            LOG.debug("Waiting while the remote service get request ...");
            TapHelloRequest serverGotRequest = queue.poll(5, TimeUnit.SECONDS);
            Assert.assertNotNull("Value must be set inside server", serverGotRequest);
            Assert.assertEquals("Hello", serverGotRequest.getName());


            // DOWN
            String responseHexDown = http.doGet("/tap.ServerStreamingGreeter/SayHelloStreaming/DOWN/2/1");
            Assert.assertEquals("[27] :  02 00 00 00  09 0A 07 68  65 6C 6C 6F  20 31 00 00  00 09 0A 07  68 65 6C 6C  6F 20 32", responseHexDown);


        } finally {
            jettyServer.shutdown();
        }

    }
}
