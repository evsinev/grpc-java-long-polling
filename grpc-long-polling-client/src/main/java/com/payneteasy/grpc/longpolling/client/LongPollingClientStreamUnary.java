package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.client.http.HttpClientDelayedInit;
import com.payneteasy.grpc.longpolling.client.http.HttpClientExecutor;
import com.payneteasy.grpc.longpolling.client.http.HttpClientUnary;
import com.payneteasy.grpc.longpolling.client.util.ServerEndPoint;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

public class LongPollingClientStreamUnary extends AbstractClientStream {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientStreamUnary.class);

    private volatile ClientStreamListener  listener;

    private final    HttpClientDelayedInit httpClient;

    public LongPollingClientStreamUnary(ExecutorService aExecutor, ServerEndPoint aEndPoint) {
        httpClient = new HttpClientDelayedInit(aListener ->
                new HttpClientExecutor(aExecutor, new HttpClientUnary(aEndPoint, aListener))
        );
    }

    @Override
    public void cancel(Status reason) {
        LOG.trace("cancel({})", reason);
        httpClient.cancelStream(reason);
        listener.closed(reason, new Metadata());
    }

    @Override
    public void start(ClientStreamListener aListener) {
        LOG.trace("start({})", aListener);
        listener = aListener;
        httpClient.initialiseDelegate(aListener);
    }

    @Override
    public void writeMessage(InputStream message) {
        LOG.trace("writeMessage({})", message);
        httpClient.sendMessage(message);
    }

    @Override
    public void request(int numMessages) {
        LOG.trace("request({})", numMessages);
        listener.onReady();
    }

}
