package com.payneteasy.grpc.longpolling.client.util;

import com.payneteasy.grpc.longpolling.common.MethodDirection;
import com.payneteasy.grpc.longpolling.common.StreamId;
import io.grpc.MethodDescriptor;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ServerEndPointTest {

    @Test
    public void createUrl() throws Exception {
        //noinspection unchecked
        MethodDescriptor.Marshaller<Object> marshaller = Mockito.mock(MethodDescriptor.Marshaller.class);
        MethodDescriptor<Object, Object> descriptor = MethodDescriptor.newBuilder()
                .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
                .setFullMethodName("class/Method")
                .setRequestMarshaller(marshaller)
                .setResponseMarshaller(marshaller)
                .build();

        ServerEndPoint endPoint = new ServerEndPoint(new URL("http://localhost"), StreamId.parse("1", "2"), descriptor);
        assertEquals("http://localhost/class/Method/UP/1/2", endPoint.createUrl(MethodDirection.UP).toString());
        assertEquals(StreamId.parse("1", "2"), endPoint.getStreamId());
    }


}