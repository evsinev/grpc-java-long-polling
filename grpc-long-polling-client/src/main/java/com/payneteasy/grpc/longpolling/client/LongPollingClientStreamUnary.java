package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.client.http.IStreamHttpService;
import com.payneteasy.grpc.longpolling.client.http.StreamHttpServiceExecutor;
import com.payneteasy.grpc.longpolling.client.http.StreamHttpServiceUnary;
import com.payneteasy.grpc.longpolling.common.StreamId;
import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class LongPollingClientStreamUnary extends AbstractClientStream {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientStreamUnary.class);

    private final    IStreamHttpService   streamHttpService;
    private volatile ClientStreamListener clientStreamListener;

    public LongPollingClientStreamUnary(ExecutorService aExecutor, URL aBaseUrl, StreamId aStreamId, MethodDescriptor<?, ?> aMethod, Metadata aHeaders, CallOptions aCallOptions) {
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
        clientStreamListener.onReady();
    }

}
