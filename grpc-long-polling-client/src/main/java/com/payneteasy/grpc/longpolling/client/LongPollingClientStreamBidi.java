package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.client.http.HttpClientDelayedInit;
import com.payneteasy.grpc.longpolling.client.http.HttpClientDownloading;
import com.payneteasy.grpc.longpolling.client.http.HttpClientExecutor;
import com.payneteasy.grpc.longpolling.client.http.HttpClientUploading;
import com.payneteasy.grpc.longpolling.client.util.ServerEndPoint;
import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.common.SlotSender;
import io.grpc.Deadline;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import io.grpc.internal.InsightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class LongPollingClientStreamBidi extends AbstractClientStream {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientStreamBidi.class);

    private volatile ClientStreamListener listener;

    private final    HttpClientDelayedInit             downloading;
    private final    HttpClientDelayedInit             uploading;
    private final    SlotSender<SingleMessageProducer> slotSender;

    public LongPollingClientStreamBidi(ExecutorService aExecutor, ServerEndPoint aEndpoint, AtomicBoolean aTransportActive) {
        slotSender = new SlotSender<>(LOG, aMessage -> listener.messagesAvailable(aMessage));

        uploading = new HttpClientDelayedInit(aListener ->
                new HttpClientExecutor(aExecutor, new HttpClientUploading(aEndpoint, listener))
        );

        downloading = new HttpClientDelayedInit(aListener ->
                new HttpClientExecutor(aExecutor, new HttpClientDownloading(aEndpoint, aTransportActive, slotSender, listener))
        );
    }

    @Override
    public void cancel(Status reason) {
        LOG.trace("cancel({})", reason);
        uploading.cancelStream(reason);
        downloading.cancelStream(reason);
        listener.closed(reason, new Metadata());
    }

    @Override
    public void start(ClientStreamListener listener) {
        LOG.trace("start({})", listener);
        this.listener = listener;
        uploading.initialiseDelegate(listener);
        downloading.initialiseDelegate(listener);
    }

    @Override
    public void writeMessage(InputStream message) {
        LOG.trace("writeMessage({})", message);
        uploading.sendMessage(message);
    }

    @Override
    public void request(int numMessages) {
        LOG.trace("request({})", numMessages);
        listener.onReady();
        slotSender.onRequest(numMessages);
        downloading.sendMessage(null);
    }

    @Override
    public void setDeadline(@Nonnull Deadline deadline) {
    }

    @Override
    public void appendTimeoutInsight(InsightBuilder insight) {
    }
}
