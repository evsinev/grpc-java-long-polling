package com.payneteasy.grpc.longpolling.server.base;

import io.grpc.Attributes;
import io.grpc.Compressor;
import io.grpc.Decompressor;
import io.grpc.internal.ServerStream;
import io.grpc.internal.ServerStreamListener;
import io.grpc.internal.StatsTraceContext;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public abstract class AbstractNoopServerStream implements ServerStream {

    private  final Logger              LOG;

    // writeMessage is executed from [grpc-default-executor-0] thread
    // request      is executed from [grpc-default-executor-0] thread
    // setListener  is executed from [qtp681855685-29]         thread (Servlet.doPost)
    protected volatile ServerStreamListener listener;

    public AbstractNoopServerStream(Logger aLog) {
        aLog.trace("Created");
        this.LOG = aLog;
    }

    @Override
    public void setListener(ServerStreamListener aListener) {
        LOG.trace("setListener({})", aListener);
        listener = aListener;
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

    @Override
    public void setDecompressor(Decompressor decompressor) {
        LOG.trace("setDecompressor({})", decompressor);
    }

    @Override
    public void flush() {
        LOG.trace("flush()");
    }


}
