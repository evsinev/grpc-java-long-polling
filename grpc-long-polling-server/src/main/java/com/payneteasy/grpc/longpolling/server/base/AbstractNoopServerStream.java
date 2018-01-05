package com.payneteasy.grpc.longpolling.server.base;

import io.grpc.Attributes;
import io.grpc.Compressor;
import io.grpc.internal.ServerStream;
import io.grpc.internal.StatsTraceContext;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public abstract class AbstractNoopServerStream implements ServerStream {

    private final Logger LOG;

    public AbstractNoopServerStream(Logger LOG) {
        LOG.trace("Created");
        this.LOG = LOG;
    }

    @Nullable
    @Override
    public String getAuthority() {
        LOG.trace("getAuthority()");
        return null;
    }


    @Override
    public StatsTraceContext statsTraceContext() {
        LOG.trace("statsTraceContext()");
        return StatsTraceContext.NOOP;
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

    @Override
    public Attributes getAttributes() {
        LOG.trace("getAttributes()");
        return Attributes.EMPTY;
    }

}
