package com.payneteasy.grpc.longpolling.common;

import io.grpc.internal.IoUtils;
import io.grpc.internal.StreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SingleMessageProducer implements StreamListener.MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(SingleMessageProducer.class);

    private       InputStream message;
    private final String      messageSource;
    private final int         size;

    public SingleMessageProducer(String aSource, byte[] aBuffer) {
        this.message = new ByteArrayInputStream(aBuffer);
        messageSource = aSource;
        size = aBuffer.length;
    }

    @Nullable
    @Override
    public InputStream next() {
        LOG.trace("{}: next() [message = {}]", messageSource, message != null ? message : "no message");
        InputStream messageToReturn = message;
        message = null;
        return messageToReturn;
    }

    @Override
    public String toString() {
        return "SingleMessageProducer{"
                + "source=" + messageSource + ", siez=" + size
                + '}';
    }

    public static SingleMessageProducer readFully(Class<?> aClass, InputStream aInputStream) throws IOException {
        byte[] buffer = IoUtils.toByteArray(aInputStream);
        return new SingleMessageProducer(aClass.getSimpleName(), buffer);
    }
}
