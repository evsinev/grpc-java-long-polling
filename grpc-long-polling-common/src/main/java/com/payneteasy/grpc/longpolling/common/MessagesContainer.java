package com.payneteasy.grpc.longpolling.common;

import io.grpc.internal.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessagesContainer {

    public static final MessagesContainer EMPTY = new Builder().build();

    private final List<InputStream> inputs;

    private MessagesContainer(List<InputStream> inputs) {
        this.inputs = Collections.unmodifiableList(inputs);
    }

    public boolean isEmpty() {
        return inputs.isEmpty();
    }

    public void writeToOutput(OutputStream aOut) throws IOException {
        if(inputs.size() == 1) {
            aOut.write(0x01); // version
            IoUtils.copy(inputs.get(0), aOut);
        } else {
            aOut.write(0x02); // version
            for (InputStream input : inputs) {
                byte[] buf = IoUtils.toByteArray(input);
                aOut.write(getLengthBytes(buf.length));
                aOut.write(buf);
            }
        }
    }

    public static MessagesContainer parse(InputStream aInputStream) throws IOException {
        int version = aInputStream.read();
        switch (version) {
            case   -1: return MessagesContainer.EMPTY;
            case 0x01: return readOne(aInputStream);
            case 0x02: return readMany(aInputStream);
            default:
                throw new IllegalStateException("Version " + version + " not supported");
        }
    }

    private static MessagesContainer readOne(InputStream aInputStream) throws IOException {
        return new Builder().add(aInputStream).build();
    }

    private static MessagesContainer readMany(InputStream aInputStream) throws IOException {
        Builder builder = new Builder();
        for(int i=0; i<10_000; i++) {
            int length = readLength(aInputStream);
            if(length < 0) {
                break;
            }
            byte[] buf = readBytes(aInputStream, length);
            builder.add(buf);

        }
        return builder.build();
    }

    static int readLength(InputStream aInputStream) throws IOException {
        int b1 = aInputStream.read();
        if(b1 < 0) {
            return -1;
        }

        byte[] buf = new byte[4];
        buf[0] = (byte) b1;
        for(int i=1; i<buf.length; i++) {
            int b = aInputStream.read();
            if(b < 0) {
                throw new IllegalStateException("Cannot read length. End of the stream");
            }
            buf[i] = (byte) b;
        }

        return ByteBuffer.wrap(buf).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    static byte[] getLengthBytes(int aLength) {
        return ByteBuffer.allocate(4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(aLength)
                .array();
    }

    public List<InputStream> getInputs() {
        return inputs;
    }

    public static class Builder {
        List<InputStream> inputs = new ArrayList<>();

        public Builder add(InputStream aInputStream) throws IOException {
            byte[] buf = IoUtils.toByteArray(aInputStream);
            return add(buf);
        }

        public MessagesContainer build() {
            return new MessagesContainer(inputs);
        }

        public Builder add(byte[] aBuffer) {
            if(aBuffer.length != 0) {
                inputs.add(new ByteArrayInputStream(aBuffer));
            }
            return this;
        }
    }

    static byte[] readBytes(InputStream aInputStream, int aLength) throws IOException {
        byte[] buf = new byte[aLength];
        for(int i=0; i<buf.length; i++) {
            int b = aInputStream.read();
            if(b < 0) {
                throw new IllegalStateException("Cannot read data. End of the stream");
            }
            buf[i] = (byte) b;
        }

        return buf;
    }
}
