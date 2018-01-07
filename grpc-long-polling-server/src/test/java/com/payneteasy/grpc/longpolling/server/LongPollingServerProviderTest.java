package com.payneteasy.grpc.longpolling.server;

import org.junit.Test;

import static org.junit.Assert.*;

public class LongPollingServerProviderTest {

    private LongPollingServerProvider provider = new LongPollingServerProvider();

    @Test
    public void isAvailable() throws Exception {
        assertTrue(provider.isAvailable());
    }

    @Test
    public void priority() throws Exception {
        assertEquals(5, provider.priority());
    }

    @Test
    public void builderForPort() throws Exception {
        provider = new LongPollingServerProvider();
        assertNotNull(provider.builderForPort(-1));
    }

}