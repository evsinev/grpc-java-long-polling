package com.payneteasy.grpc.longpolling.test.sreaming;

import com.payneteasy.grpc.longpolling.server.LongPollingServer;
import com.payneteasy.grpc.longpolling.server.servlet.LongPollingDispatcherServlet;
import com.payneteasy.grpc.longpolling.test.helloworld.HelloWorldServer;
import com.payneteasy.grpc.longpolling.test.util.ServerUtils;
import com.payneteasy.tlv.HexUtil;
import io.grpc.internal.IoUtils;
import io.grpc.internal.ServerListener;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StreamingServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingServerTest.class);


    @Test(timeout = 10_000)
    public void up() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        LongPollingServer longPollingServer = ServerUtils.createLongPollingServer(new StreamingGreeterImpl(latch));
        ServerListener    serverListener    = longPollingServer.waitForServerListener();
        HelloWorldServer  jettyServer       = new HelloWorldServer(9096, new LongPollingDispatcherServlet(serverListener));

        jettyServer.start();

        send("UP", "0A 06 74 65  73 74 20 31");
        send("UP", "0A 06 74 65  73 74 20 31");
        Thread.sleep(200);
        send("UP", "0A 06 74 65  73 74 20 31");

        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));

        jettyServer.shutdown();
        longPollingServer.shutdown();
    }

    @Test(timeout = 10_000)
    public void upDown() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        LongPollingServer longPollingServer = ServerUtils.createLongPollingServer(new StreamingGreeterImpl(latch));
        ServerListener    serverListener    = longPollingServer.waitForServerListener();
        HelloWorldServer  jettyServer       = new HelloWorldServer(9096, new LongPollingDispatcherServlet(serverListener));

        jettyServer.start();

        send("UP", "0A 06 74 65  73 74 20 31");
        assertEquals("[23] :  02 " + // version
                "00 00 00  07 " +             // length 1
                "0A 05 68  65 6C 6C 6F  " +   // data   1
                "00 00 00 07  " +             // length 2
                "0A 05 68 65  6C 6C 6F"       // data   2
                , send("DOWN", "0A 06 74 65  73 74 20 32"));
        

        jettyServer.shutdown();
        longPollingServer.shutdown();
    }

    @Test(timeout = 10_000)
    public void down() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        LongPollingServer longPollingServer = ServerUtils.createLongPollingServer(new StreamingGreeterImpl(latch));
        ServerListener    serverListener    = longPollingServer.waitForServerListener();
        HelloWorldServer  jettyServer       = new HelloWorldServer(9096, new LongPollingDispatcherServlet(serverListener));

        jettyServer.start();

        String response = send("DOWN", "0A 06 74 65  73 74 20 32");
        
        if(!response.equals("[23] :  02 00 00 00  07 0A 05 68  65 6C 6C 6F  00 00 00 07  0A 05 68 65  6C 6C 6F")
                && !response.equals("[8] :  01 0A 05 68  65 6C 6C 6F")) {
            fail("Response is wrong: " + response);
        }


        jettyServer.shutdown();
        longPollingServer.shutdown();
    }

    private String send(String aMethodDirection, String aHex) throws IOException {
        String urlText = String.format("http://localhost:9096/test/manualflowcontrol.StreamingGreeter/SayHelloStreaming/%s/801VJ7k2ThCAjUUF7lKlDw/1", aMethodDirection);
        URL url = new URL(urlText);
        LOG.info("Sending to {} ...", url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.getOutputStream().write(HexUtil.parseHex(aHex));
        byte[] out =  IoUtils.toByteArray(connection.getInputStream());
        String hex = HexUtil.toFormattedHexString(out);
        LOG.debug("out: {}", hex);
        return hex;
    }



}
