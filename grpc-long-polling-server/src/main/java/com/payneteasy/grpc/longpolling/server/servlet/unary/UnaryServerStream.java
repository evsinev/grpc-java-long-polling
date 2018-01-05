package com.payneteasy.grpc.longpolling.server.servlet.unary;

import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.server.base.AbstractNoopServerStream;
import io.grpc.Decompressor;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.IoUtils;
import io.grpc.internal.ServerStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UnaryServerStream extends AbstractNoopServerStream {

    private static final Logger LOG = LoggerFactory.getLogger(UnaryServerStream.class);

    private final     byte[]                outputBuffer;
    private final     HttpServletResponse   response;
    private final     CountDownLatch        latch;
    private final     StreamId              streamId;

    public UnaryServerStream(byte[] aOutputBuffer, HttpServletResponse aResponse, StreamId aStreamId) {
        super(LOG);
        outputBuffer = aOutputBuffer;
        response     = aResponse;
        latch        = new CountDownLatch(1);
        streamId     = aStreamId;
    }

    @Override
    public void writeHeaders(Metadata headers) {
        LOG.trace("writeHeaders({})", headers);
        for (String key : headers.keys()) {
            response.addHeader(key, headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)));
        }
    }

    @Override
    public void writeMessage(InputStream message) {
        LOG.trace("writeMessage({})", message);
        try {
            IoUtils.copy(message, response.getOutputStream());
        } catch (IOException e) {
            LOG.error("IO error", e);
            if(listener != null) {
                listener.closed(Status.DATA_LOSS);
            }
        } finally {
            latch.countDown();
        }
    }

    @Override
    public void close(Status status, Metadata trailers) {
        LOG.trace("close({}, {})", status, trailers);
    }

    @Override
    public void cancel(Status status) {
        LOG.trace("cancel({})", status);
    }

    @Override
    public void request(int numMessages) {
        LOG.trace("request({})", numMessages);
        LOG.debug("Sending messagesAvailable ...");
        listener.messagesAvailable(new SingleMessageProducer(getClass().getSimpleName(), outputBuffer));
        LOG.debug("Stream {} half closed", streamId);
        listener.halfClosed();
    }

    public void waitDone(int aPeriod, TimeUnit aTimeUnit) throws InterruptedException {
        latch.await(aPeriod, aTimeUnit);
    }
}
