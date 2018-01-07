package com.payneteasy.grpc.longpolling.common;

import com.payneteasy.tlv.HexUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static com.payneteasy.grpc.longpolling.common.MessagesContainer.parse;
import static com.payneteasy.tlv.HexUtil.parseHex;
import static org.junit.Assert.*;

public class MessagesContainerTest {

    @Test
    public void oneMessage() throws Exception {
        MessagesContainer messages = parse(new ByteArrayInputStream(parseHex(
                "01 " +  // version
                        "02"       // content
        )));
        assertFalse(messages.isEmpty());
        ByteArrayOutputStream out = new ByteArrayOutputStream(10);
        messages.writeToOutput(out);
        assertEquals("[2] :  01 02", HexUtil.toFormattedHexString(out.toByteArray()));
    }

    @Test
    public void twoMessages() throws Exception {
        MessagesContainer messages = parse(new ByteArrayInputStream(parseHex(
                "02 " + // version
                        "00 00 00 01   02" +  // message 1
                        "00 00 00 02   02 03" // message 2
                )));
        assertFalse(messages.isEmpty());
        ByteArrayOutputStream out = new ByteArrayOutputStream(10);
        messages.writeToOutput(out);
        assertEquals("[12] :  02 00 00 00  01 02 00 00  00 02 02 03", HexUtil.toFormattedHexString(out.toByteArray()));
    }

    @Test
    public void empty() throws Exception {
        assertTrue(parse(new ByteArrayInputStream(new byte[0])).isEmpty());
        assertTrue("Must be empty", parse(new ByteArrayInputStream(new byte[]{0x01})).isEmpty());
        assertTrue(parse(new ByteArrayInputStream(new byte[]{0x02})).isEmpty());
    }

    @Test
    public void badVersion() throws Exception {
        try {
            parse(new ByteArrayInputStream(new byte[]{0x03})).isEmpty();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Version 3 not supported", e.getMessage());
        }
    }

    @Test
    public void wrongLength() throws Exception {
        try {
            parse(new ByteArrayInputStream(parseHex(
                    "02 " + // version
                            "00 "
            )));
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Cannot read length. End of the stream", e.getMessage());
        }
    }

    @Test
    public void wrongData() throws Exception {
        try {
            parse(new ByteArrayInputStream(parseHex(
                    "02 " + // version
                            "00 00 00 02 " // length
                    + "01" // wrong data
            )));
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Cannot read data. End of the stream", e.getMessage());
        }
    }

}