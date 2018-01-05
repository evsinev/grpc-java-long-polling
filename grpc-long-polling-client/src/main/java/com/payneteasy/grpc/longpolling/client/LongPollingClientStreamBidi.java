package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.client.http.*;
import com.payneteasy.grpc.longpolling.common.StreamId;
import io.grpc.*;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class LongPollingClientStreamBidi extends AbstractClientStream {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientStreamBidi.class);

    private final    IStreamHttpService   downloadingHttpService;
    private final    IStreamHttpService   uploadingHttpService;
    private volatile ClientStreamListener clientStreamListener;

    public LongPollingClientStreamBidi(ExecutorService aExecutor, URL aBaseUrl, StreamId aStreamId, MethodDescriptor<?, ?> aMethod, Metadata aHeaders, CallOptions aCallOptions, AtomicBoolean aTransportActive) {
        switch (aMethod.getType()) {
            case BIDI_STREAMING:
                uploadingHttpService   = new StreamHttpServiceExecutor(aExecutor, new StreamHttpServiceUploading(aBaseUrl, aStreamId, aMethod));
                downloadingHttpService = new StreamHttpServiceExecutor(aExecutor, new StreamHttpServiceDownloading(aBaseUrl, aStreamId, aMethod, aTransportActive));
                break;

            default:
                throw new IllegalArgumentException("Not supported yet " + aMethod.getType());
        }
    }

    @Override
    public void cancel(Status reason) {
        LOG.trace("cancel({})", reason);
        uploadingHttpService.cancelStream(reason);
        downloadingHttpService.cancelStream(reason);
        clientStreamListener.closed(reason, new Metadata());
    }

    @Override
    public void start(ClientStreamListener listener) {
        LOG.trace("start({})", listener);
        clientStreamListener = listener;
        uploadingHttpService.setClientStreamListener(listener);
        downloadingHttpService.setClientStreamListener(listener);
    }

    @Override
    public void writeMessage(InputStream message) {
        LOG.trace("writeMessage({})", message);
        uploadingHttpService.sendMessage(message);
    }

    @Override
    public void request(int numMessages) {
        LOG.trace("request({})", numMessages);
        clientStreamListener.onReady();
        downloadingHttpService.sendMessage(null);
    }

}
