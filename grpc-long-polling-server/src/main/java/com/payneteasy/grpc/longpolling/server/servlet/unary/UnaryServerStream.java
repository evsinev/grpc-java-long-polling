package com.payneteasy.grpc.longpolling.server.servlet.unary;

import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import io.grpc.*;
import io.grpc.internal.IoUtils;
import io.grpc.internal.ServerStream;
import io.grpc.internal.ServerStreamListener;
import io.grpc.internal.StatsTraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UnaryServerStream implements ServerStream {

    private static final Logger LOG = LoggerFactory.getLogger(UnaryServerStream.class);

    private final byte[] outputBuffer;

    private final HttpServletResponse response;
    private volatile  ServerStreamListener listener;
    private final CountDownLatch latch  = new CountDownLatch(1);

    public UnaryServerStream(byte[] aOutputBuffer, HttpServletResponse aResponse) {
        outputBuffer = aOutputBuffer;
        response = aResponse;
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
    public void writeHeaders(Metadata headers) {
        LOG.trace("writeHeaders({})", headers);
        for (String key : headers.keys()) {
            response.addHeader(key, headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)));
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
    public void setListener(ServerStreamListener aListener) {
        LOG.trace("setListener({})", aListener);
        listener = aListener;
    }

    @Override
    public void setDecompressor(Decompressor decompressor) {
        LOG.trace("setDecompressor({})", decompressor);
    }

    @Override
    public Attributes getAttributes() {
        LOG.trace("getAttributes()");
        return Attributes.EMPTY;
    }

    @Nullable
    @Override
    public String getAuthority() {
        LOG.trace("getAttributes()");
        return null;
    }


    @Override
    public StatsTraceContext statsTraceContext() {
        LOG.trace("statsTraceContext()");
        return StatsTraceContext.NOOP;
    }

    @Override
    public void request(int numMessages) {
        LOG.trace("request({})", numMessages);
        LOG.debug("Sending messagesAvailable ...");
        listener.messagesAvailable(new SingleMessageProducer(getClass().getSimpleName(), outputBuffer));
        listener.halfClosed();
    }

    @Override
    public void flush() {
        LOG.trace("flush()");
    }

    @Override
    public boolean isReady() {
        LOG.trace("isReady()");
        return true;
    }

    @Override
    public void setCompressor(Compressor compressor) {
        LOG.trace("setCompressor({})", compressor);

    }

    @Override
    public void setMessageCompression(boolean enable) {
        LOG.trace("setMessageCompression({})", enable);

    }

    public void waitDone(int aPeriod, TimeUnit aTimeUnit) throws InterruptedException {
        latch.await(aPeriod, aTimeUnit);
    }
}
