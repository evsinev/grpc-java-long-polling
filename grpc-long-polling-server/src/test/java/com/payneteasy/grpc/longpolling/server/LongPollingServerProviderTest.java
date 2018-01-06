package com.payneteasy.grpc.longpolling.server;

import org.junit.Test;

import static org.junit.Assert.*;

public class LongPollingServerProviderTest {

    private LongPollingServerProvider provider;

    @Test
    public void isAvailable() throws Exception {
        assertTrue(new LongPollingServerProvider().isAvailable());
    }

    @Test
    public void priority() throws Exception {
        assertEquals(5, new LongPollingServerProvider().priority());
    }

    @Test
    public void builderForPort() throws Exception {
        provider = new LongPollingServerProvider();
        assertNotNull(provider.builderForPort(-1));
    }

}