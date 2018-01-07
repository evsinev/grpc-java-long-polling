package com.payneteasy.grpc.longpolling.client.util;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class UrlsTest {

    @Test
    public void appendPaths() throws Exception {
        assertEquals("http://localhost", Urls.appendPaths(new URL("http://localhost")).toString());
        assertEquals("http://localhost/test", Urls.appendPaths(new URL("http://localhost/"), "test").toString());
        assertEquals("http://localhost//test", Urls.appendPaths(new URL("http://localhost/"), "/test").toString());
        assertEquals("http://localhost/test", Urls.appendPaths(new URL("http://localhost"), "test").toString());
        assertEquals("http://localhost/test", Urls.appendPaths(new URL("http://localhost"), "/test").toString());
        assertEquals("http://localhost/test/hello", Urls.appendPaths(new URL("http://localhost"), "/test", "hello").toString());
        assertEquals("http://localhost/test/hello", Urls.appendPaths(new URL("http://localhost"), "/test", "/hello").toString());
    }

}