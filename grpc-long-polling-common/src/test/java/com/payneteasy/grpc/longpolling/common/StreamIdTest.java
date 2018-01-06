package com.payneteasy.grpc.longpolling.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class StreamIdTest {

    private final static StreamId STREAM = StreamId.parse("1", "2");

    @Test
    public void constructor() {
        StreamId streamId = new StreamId(TransportId.parse("1"), "2");
        assertEquals("1", streamId.getTransportId().getTransportId());
        assertEquals("2", streamId.getStreamId());

        try {
            new StreamId(null, "1");
            fail("TransportId should not be created with null transport id");
        } catch (IllegalArgumentException e) {
            assertEquals("Transport is null", e.getMessage());
        }

        try {
            new StreamId(TransportId.parse("1"), null);
            fail("StreamId should not be created with null stream id");
        } catch (IllegalArgumentException e) {
            assertEquals("Stream id is null", e.getMessage());
        }
    }

    @Test
    public void equals() throws Exception {
        assertTrue(STREAM.equals(StreamId.parse("1", "2")));
        assertFalse(STREAM.equals(StreamId.parse("1", "1")));
        assertFalse(STREAM.equals(StreamId.parse("2", "2")));
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(STREAM.equals("hello"));
        //noinspection EqualsWithItself
        assertTrue(STREAM.equals(STREAM));
        //noinspection ObjectEqualsNull
        assertFalse(STREAM.equals(null));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(STREAM.hashCode(), StreamId.parse("1", "2").hashCode());
        assertNotEquals(STREAM.hashCode(), StreamId.parse("1", "1").hashCode());
        assertNotEquals(STREAM.hashCode(), StreamId.parse("2", "1").hashCode());
        assertNotEquals(STREAM.hashCode(), StreamId.parse("2", "2").hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("SID:1/2", STREAM.toString());
    }

    @Test
    public void getStreamId() throws Exception {
        assertEquals("2", STREAM.getStreamId());
    }

    @Test
    public void getTransportId() throws Exception {
        assertEquals("1", STREAM.getTransportId().getTransportId());
    }

    @Test
    public void parse() throws Exception {
        StreamId stream = StreamId.parse("1", "2");
        assertEquals("2", stream.getStreamId());
        assertEquals("1", stream.getTransportId().getTransportId());

        try {
            StreamId.parse(null, "1");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Transport id is null", e.getMessage());
        }

        try {
            StreamId.parse("1", null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Stream id is null", e.getMessage());
        }

    }

}