package com.payneteasy.grpc.longpolling.client;

import io.grpc.Attributes;
import io.grpc.Compressor;
import io.grpc.DecompressorRegistry;
import io.grpc.internal.ClientStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractClientStream implements ClientStream {

    private final Logger log = LoggerFactory.getLogger(getClass());

    //region Just tracing

    @Override
    public void halfClose() {
        log.trace("halfClose()");
    }

    @Override
    public void setAuthority(String authority) {
        log.trace("setAuthority({})", authority);
    }

    @Override
    public void setFullStreamDecompression(boolean fullStreamDecompression) {
        log.trace("setFullStreamDecompression({})", fullStreamDecompression);
    }

    @Override
    public void setDecompressorRegistry(DecompressorRegistry decompressorRegistry) {
        log.trace("setDecompressorRegistry({}, {})", decompressorRegistry.getAdvertisedMessageEncodings(), decompressorRegistry.getKnownMessageEncodings());
    }

    @Override
    public void setMaxInboundMessageSize(int maxSize) {
        log.trace("setMaxInboundMessageSize({})", maxSize);
    }

    @Override
    public void setMaxOutboundMessageSize(int maxSize) {
        log.trace("setMaxOutboundMessageSize({})", maxSize);
    }

    @Override
    public Attributes getAttributes() {
        log.trace("getAttributes()");
        return Attributes.EMPTY;
    }

    @Override
    public void flush() {
        log.trace("flush()");
    }

    @Override
    public boolean isReady() {
        log.trace("isReady()");
        return true;
    }

    @Override
    public void setCompressor(Compressor compressor) {
        log.trace("setCompressor({})", compressor.getMessageEncoding());
    }

    @Override
    public void setMessageCompression(boolean enable) {
        log.trace("setMessageCompression({})", enable);
    }
    //endregion

}
