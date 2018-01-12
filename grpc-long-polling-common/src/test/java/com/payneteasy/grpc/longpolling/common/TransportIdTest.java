package com.payneteasy.grpc.longpolling.common;

import org.junit.Test;

import java.util.Objects;

import static com.payneteasy.grpc.longpolling.common.TransportId.parse;
import static org.junit.Assert.*;

public class TransportIdTest {

    private static final TransportId T1 = parse("1");

    @Test
    public void equals() throws Exception {
        TransportId transportId = TransportId.generateNew();
        assertEquals(transportId.getTransportId(), parse(transportId.getTransportId()).getTransportId());
        assertNotEquals(T1, parse("2"));
        //noinspection EqualsWithItself
        assertTrue(T1.equals(T1));
        String nullString = null;
        //noinspection ConstantConditions
        assertNotEquals(T1, nullString);
        String transportString = "test";
        assertNotEquals(T1, transportString);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(T1.hashCode(), parse("1").hashCode());
        assertNotEquals(T1.hashCode(), parse("2").hashCode());
        assertEquals(Objects.hash("1"), T1.hashCode());
    }

    @Test
    public void generateNew() throws Exception {
        TransportId transportId = TransportId.generateNew();
        assertNotNull(transportId.getTransportId());
        assertTrue(10 < transportId.getTransportId().length());
    }

    @Test
    public void getTransportId() throws Exception {
        assertEquals("1", T1.getTransportId());
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
        assertEquals("TID:1", T1.toString());
    }

}