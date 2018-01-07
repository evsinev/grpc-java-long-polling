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

    private  final Logger log;

    // writeMessage is executed from [grpc-default-executor-0] thread
    // request      is executed from [grpc-default-executor-0] thread
    // setListener  is executed from [qtp681855685-29]         thread (Servlet.doPost)
    protected volatile ServerStreamListener listener;

    public AbstractNoopServerStream(Logger aLog) {
        aLog.trace("Created");
        this.log = aLog;
    }

    @Override
    public void setListener(ServerStreamListener aListener) {
        log.trace("setListener({})", aListener);
        listener = aListener;
    }

    @Nullable
    @Override
    public String getAuthority() {
        log.trace("getAuthority()");
        return null;
    }


    @Override
    public StatsTraceContext statsTraceContext() {
        log.trace("statsTraceContext()");
        return StatsTraceContext.NOOP;
    }

    @Override
    public boolean isReady() {
        log.trace("isReady()");
        return true;
    }

    @Override
    public void setCompressor(Compressor compressor) {
        log.trace("setCompressor({})", compressor);

    }

    @Override
    public void setMessageCompression(boolean enable) {
        log.trace("setMessageCompression({})", enable);

    }

    @Override
    public Attributes getAttributes() {
        log.trace("getAttributes()");
        return Attributes.EMPTY;
    }

    @Override
    public void setDecompressor(Decompressor decompressor) {
        log.trace("setDecompressor({})", decompressor);
    }

    @Override
    public void flush() {
        log.trace("flush()");
    }


}
