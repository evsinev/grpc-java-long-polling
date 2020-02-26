package com.payneteasy.grpc.longpolling.test.util;

import com.payneteasy.grpc.longpolling.client.util.Urls;
import com.payneteasy.tlv.HexUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleHttpClient.class);

    private final URL baseUrl;

    public SimpleHttpClient(URL aUrl) throws IOException {
        baseUrl = aUrl;
    }

    public String postHex(String aPath, String aHex) throws IOException {
        URL url = Urls.appendPaths(baseUrl, aPath);
        LOG.debug("Sending POST to {} ...", url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.getOutputStream().write(HexUtil.parseHex(aHex));
        byte[] out = IOUtils.toByteArray(connection.getInputStream());
        String hex = HexUtil.toFormattedHexString(out);
        LOG.debug("out: {}", hex);
        return hex;
    }

    public String doGet(String aPath) throws IOException {
        URL url = Urls.appendPaths(baseUrl, aPath);
        LOG.debug("Sending GET to {} ...", url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        byte[] out = IOUtils.toByteArray(connection.getInputStream());
        String hex = HexUtil.toFormattedHexString(out);
        LOG.debug("out: {}", hex);
        return hex;
    }
}
