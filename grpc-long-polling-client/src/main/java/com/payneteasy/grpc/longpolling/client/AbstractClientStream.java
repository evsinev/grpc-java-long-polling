package com.payneteasy.grpc.longpolling.client;

import io.grpc.Attributes;
import io.grpc.Compressor;
import io.grpc.DecompressorRegistry;
import io.grpc.internal.ClientStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractClientStream implements ClientStream {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    //region Just tracing

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
        LOG.trace("setDecompressorRegistry({}, {})", decompressorRegistry.getAdvertisedMessageEncodings(), decompressorRegistry.getKnownMessageEncodings());
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
        LOG.trace("setCompressor({})", compressor.getMessageEncoding());
    }

    @Override
    public void setMessageCompression(boolean enable) {
        LOG.trace("setMessageCompression({})", enable);
    }
    //endregion

}
