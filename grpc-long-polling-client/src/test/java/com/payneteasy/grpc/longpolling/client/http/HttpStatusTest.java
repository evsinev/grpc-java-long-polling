package com.payneteasy.grpc.longpolling.client.http;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpStatusTest {

    @Test
    public void ok() throws Exception {
        HttpStatus status = new HttpStatus(HttpStatus.OK, "2");
        assertEquals(HttpStatus.OK, status.getCode());
        assertEquals("2", status.getMessage());
        assertEquals("HttpStatus{code=200, message='2'}", status.toString());
        assertFalse(status.wasNotOk());
    }

    @Test
    public void wasNotOk() throws Exception {
        HttpStatus status = new HttpStatus(HttpStatus.GONE, "2");
        assertTrue(status.wasNotOk());
    }

}