package com.payneteasy.grpc.longpolling.common;

import io.grpc.internal.StreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class SingleMessageProducer implements StreamListener.MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(SingleMessageProducer.class);

    private InputStream message;

    public SingleMessageProducer(byte[] aBuffer) {
        this.message = new ByteArrayInputStream(aBuffer);
    }

    public SingleMessageProducer(InputStream message) {
        this.message = message;
    }

    @Nullable
    @Override
    public InputStream next() {
        LOG.trace("next() [message = {}]", message);
        InputStream messageToReturn = message;
        message = null;
        return messageToReturn;
    }
}
