package com.payneteasy.grpc.longpolling.test.helloworld;

import com.payneteasy.grpc.longpolling.server.LongPollingServer;
import com.payneteasy.grpc.longpolling.server.servlet.LongPollingDispatcherServlet;
import com.payneteasy.grpc.longpolling.test.util.ServerUtils;
import com.payneteasy.grpc.longpolling.test.util.SimpleJettyServer;
import com.payneteasy.tlv.HexUtil;
import io.grpc.internal.ServerListener;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloWorldServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldServerTest.class);

    @Test(timeout = 10_000)
    public void test() throws IOException, InterruptedException {

        LongPollingServer pollingServer = ServerUtils.createLongPollingServer(new GreeterImpl());
        ServerListener   serverListener = pollingServer.waitForServerListener();

        SimpleJettyServer server = new SimpleJettyServer(9096, new LongPollingDispatcherServlet(serverListener));
        server.start();

        byte[] buf = send();
        LOG.debug("returned = {}", new String(buf, 2, buf.length - 2));
        Assert.assertEquals("[13] :  0A 0B 48 65  6C 6C 6F 20  68 65 6C 6C  6F", HexUtil.toFormattedHexString(buf));

        server.shutdown();
    }

    private byte[] send() throws IOException {
        URL url = new URL("http://localhost:9096/test/helloworld.Greeter/SayHello/UNARY/1/2");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.getOutputStream().write(HexUtil.parseHex("0A 05 68 65  6C 6C 6F"));
        return IOUtils.toByteArray(connection.getInputStream());
    }
}
