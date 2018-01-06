package com.payneteasy.grpc.longpolling.common;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SlotSenderTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlotSenderTest.class);


    @Test
    public void requestThenSend() throws Exception {
        AtomicReference<String> ref = new AtomicReference<>();
        SlotSender<String> sender = new SlotSender<>(LOG, ref::set);
        sender.onRequest(1);
        assertNull(ref.get());
        sender.onSendMessage("hello");
        assertEquals("hello", ref.get());
    }

    @Test
    public void sendThenRequest() throws Exception {
        AtomicReference<String> ref = new AtomicReference<>();
        SlotSender<String> sender = new SlotSender<>(LOG, ref::set);
        sender.onSendMessage("hello");
        assertNull(ref.get());
        sender.onRequest(1);
        assertEquals("hello", ref.get());
    }

    @Test
    public void noSlotsForSending() throws Exception {
        AtomicReference<String> ref = new AtomicReference<>();
        SlotSender<String> sender = new SlotSender<>(LOG, ref::set);
        sender.onSendMessage("hello");
        sender.onSendMessage("hello");
        sender.onSendMessage("hello");
        assertNull(ref.get());
    }

    @Test
    public void sendOnly2Not3() throws Exception {
        List<String> list = new ArrayList<>();
        SlotSender<String> sender = new SlotSender<>(LOG, list::add);
        sender.onSendMessage("hello 1");
        sender.onSendMessage("hello 2");
        sender.onSendMessage("hello 3");
        sender.onRequest(2);
        assertEquals(2, list.size());
        assertEquals("hello 1", list.get(0));
        assertEquals("hello 2", list.get(1));
    }

}