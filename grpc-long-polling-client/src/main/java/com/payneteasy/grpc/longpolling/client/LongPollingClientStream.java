package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.client.http.IStreamHttpService;
import com.payneteasy.grpc.longpolling.client.http.StreamHttpServiceExecutor;
import com.payneteasy.grpc.longpolling.client.http.StreamHttpServiceUnary;
import io.grpc.*;
import io.grpc.internal.ClientStream;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class LongPollingClientStream implements ClientStream {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientStream.class);

    private final IStreamHttpService    streamHttpService;
    private volatile ClientStreamListener        clientStreamListener;

    public LongPollingClientStream(ExecutorService aExecutor, URL aBaseUrl, StreamId aStreamId, MethodDescriptor<?, ?> aMethod, Metadata aHeaders, CallOptions aCallOptions) {
        streamHttpService = new StreamHttpServiceExecutor(aExecutor, new StreamHttpServiceUnary(aBaseUrl, aStreamId, aMethod));
    }

    @Override
    public void cancel(Status reason) {
        LOG.trace("cancel({})", reason);
        streamHttpService.cancelStream(reason);
        clientStreamListener.closed(reason, new Metadata());
    }

    @Override
    public void start(ClientStreamListener listener) {
        LOG.trace("start({})", listener);
        clientStreamListener = listener;
        streamHttpService.setClientStreamListener(listener);
    }

    @Override
    public void writeMessage(InputStream message) {
        LOG.trace("writeMessage({})", message);
        streamHttpService.sendMessage(message);
    }

    @Override
    public void request(int numMessages) {
        LOG.trace("request({})", numMessages);
    }

    @Override
    public void halfClose() {
        LOG.trace("halfClose()");
    }

    @Override
    public void setAuthority(String authority) {
        LOG.trace("setAuthority({})", authority);
    }

    @Override
    public void setFullStreamDecompression(boolean fullStreamDecompression) {
        LOG.trace("setFullStreamDecompression({})", fullStreamDecompression);
    }

    @Override
    public void setDecompressorRegistry(DecompressorRegistry decompressorRegistry) {
        LOG.trace("setDecompressorRegistry({})", decompressorRegistry);
    }

    @Override
    public void setMaxInboundMessageSize(int maxSize) {
        LOG.trace("setMaxInboundMessageSize({})", maxSize);
    }

    @Override
    public void setMaxOutboundMessageSize(int maxSize) {
        LOG.trace("setMaxOutboundMessageSize({})", maxSize);
    }

    @Override
    public Attributes getAttributes() {
        LOG.trace("getAttributes()");
        return Attributes.EMPTY;
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
}
