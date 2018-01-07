package com.payneteasy.grpc.longpolling.client.util;

import io.grpc.MethodDescriptor;
import org.junit.Test;
import org.mockito.Mockito;

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

    @Test
    public void createStreamUrl() throws Exception {
        //noinspection unchecked
        MethodDescriptor.Marshaller<Object> marshaller = Mockito.mock(MethodDescriptor.Marshaller.class);

        MethodDescriptor<Object, Object> descriptor = MethodDescriptor.newBuilder()
                .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
                .setFullMethodName("class/Method")
                .setRequestMarshaller(marshaller)
                .setResponseMarshaller(marshaller)
                .build();
//        URL streamUrl = Urls.createStreamUrl(new URL("http://localhost"), StreamId.parse("1", "2"), descriptor, MethodDirection.UP);
//        assertEquals("http://localhost/class/Method/UP/1/2", streamUrl.toString());
    }

}