package com.payneteasy.grpc.longpolling.common;

import io.grpc.internal.StreamListener;
import org.apache.commons.io.IOUtils;
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
        if(LOG.isTraceEnabled()) {
            String messageInfo = message != null ? ("size=" + size + "," + message) : "no message";
            LOG.trace("{}: next() [message = {}]", messageSource, messageInfo);
        }
        InputStream messageToReturn = message;
        message = null;
        return messageToReturn;
    }

    @Override
    public String toString() {
        return "SingleMessageProducer{"
                + "source=" + messageSource + ", size=" + size
                + '}';
    }

    public static SingleMessageProducer readFully(Class<?> aClass, InputStream aInputStream) throws IOException {
        byte[] buffer = IOUtils.toByteArray(aInputStream);
        return new SingleMessageProducer(aClass.getSimpleName(), buffer);
    }
}
