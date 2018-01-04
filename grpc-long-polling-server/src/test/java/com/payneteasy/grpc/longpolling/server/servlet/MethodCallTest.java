package com.payneteasy.grpc.longpolling.server.servlet;

import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.common.TransportId;
import io.grpc.MethodDescriptor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MethodCallTest {

    @Test
    public void parse() throws Exception {
        MethodCall method = MethodCall.parse("helloworld.Greeter/SayHello/UNARY/203a2807-8bf4-4d81-bb2d-21a33f59e8f6/231b3739-8dfe-4416-b343-bf43cb360c85 ");
        assertEquals("helloworld.Greeter/SayHello", method.getMethod());
        assertEquals(MethodDescriptor.MethodType.UNARY, method.getType());
        assertEquals(StreamId.parse("203a2807-8bf4-4d81-bb2d-21a33f59e8f6", "231b3739-8dfe-4416-b343-bf43cb360c85"), method.getStreamId());
        assertEquals(TransportId.parse("203a2807-8bf4-4d81-bb2d-21a33f59e8f6"), method.getStreamId().getTransportId());
    }

}