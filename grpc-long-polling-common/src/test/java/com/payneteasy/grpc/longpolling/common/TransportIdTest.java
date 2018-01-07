package com.payneteasy.grpc.longpolling.common;

import org.junit.Test;

import java.util.Objects;

import static com.payneteasy.grpc.longpolling.common.TransportId.parse;
import static org.junit.Assert.*;

public class TransportIdTest {

    private static final TransportId t1 = TransportId.parse("1");

    @Test
    public void equals() throws Exception {
        TransportId transportId = TransportId.generateNew();
        assertEquals(transportId.getTransportId(), parse(transportId.getTransportId()).getTransportId());
        assertNotEquals(t1, parse("2"));
        assertEquals(t1, t1);
        String nullString = null;
        //noinspection ConstantConditions
        assertNotEquals(t1, nullString);
        String transportString = "test";
        assertNotEquals(t1, transportString);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(t1.hashCode(), TransportId.parse("1").hashCode());
        assertNotEquals(t1.hashCode(), TransportId.parse("2").hashCode());
        assertEquals(Objects.hash("1"), t1.hashCode());
    }

    @Test
    public void generateNew() throws Exception {
        TransportId transportId = TransportId.generateNew();
        assertNotNull(transportId.getTransportId());
        assertTrue(10 < transportId.getTransportId().length());
    }

    @Test
    public void getTransportId() throws Exception {
        assertEquals("1", t1.getTransportId());
    }

    @Test
    public void testParse() throws Exception {
        assertEquals("1", parse("1").getTransportId());
    }

    @Test
    public void generateNextStreamId() throws Exception {
        TransportId transportId = parse("1");
        assertEquals("SID:1/1", transportId.generateNextStreamId().toString());
        assertEquals("SID:1/2", transportId.generateNextStreamId().toString());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("TID:1", t1.toString());
    }

}