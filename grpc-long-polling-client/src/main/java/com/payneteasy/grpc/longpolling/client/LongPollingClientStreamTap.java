package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.client.http.HttpClientDelayedInit;
import com.payneteasy.grpc.longpolling.client.http.HttpClientDownloading;
import com.payneteasy.grpc.longpolling.client.http.HttpClientExecutor;
import com.payneteasy.grpc.longpolling.client.http.HttpClientTapping;
import com.payneteasy.grpc.longpolling.client.util.ServerEndPoint;
import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.common.SlotSender;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.payneteasy.grpc.longpolling.client.http.HttpClientDownloading.EMPTY_INPUT;

/**
 * Send one request then waiting for responses
 *
 * Sequences:
 * 1. setCompressor(identity)
 * 2. setFullStreamDecompression(false)
 * 3. setDecompressorRegistry([gzip], [gzip, identity])
 * 4. start(io.grpc.internal.ClientCallImpl$ClientStreamListenerImpl@41975bb9)
 * 5. writeMessage(1)
 * 6. request(io.grpc.protobuf.lite.ProtoInputStream@4a465447)
 * 7. halfClose()
 */
public class LongPollingClientStreamTap extends AbstractClientStream {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientStreamTap.class);

    private volatile ClientStreamListener listener;
    private volatile State                state;

    private enum State {
        SENDING_TAP
        , SENDING_DOWN
    }

    private final SlotSender<SingleMessageProducer> slotSender;
    private final HttpClientDelayedInit             tapping;
    private final HttpClientDelayedInit             downloading;

    public LongPollingClientStreamTap(ExecutorService aExecutor, ServerEndPoint aEndpoint, AtomicBoolean aTransportActive) {
        state = State.SENDING_TAP;
        slotSender = new SlotSender<>(LOG, aMessage -> listener.messagesAvailable(aMessage));

        tapping = new HttpClientDelayedInit(aListener ->
                new HttpClientExecutor(aExecutor, new HttpClientTapping(aListener, slotSender, aEndpoint))
        );

        downloading = new HttpClientDelayedInit(aListener ->
                new HttpClientExecutor(aExecutor, new HttpClientDownloading(aEndpoint, aTransportActive, slotSender, listener))
        );
    }

    @Override
    public void cancel(Status aReason) {
        LOG.trace("cancel({})", aReason);

    }

    @Override
    public void start(ClientStreamListener aListener) {
        LOG.trace("start({})", aListener);
        listener = aListener;
        tapping.initialiseDelegate(aListener);
        downloading.initialiseDelegate(aListener);
    }

    @Override
    public void request(int aCount) {
        LOG.trace("request({})", aCount);
        slotSender.onRequest(aCount);
        if(state == State.SENDING_DOWN) {
            downloading.sendMessage(EMPTY_INPUT);
        } else {
            state = State.SENDING_DOWN;
        }
    }

    @Override
    public void writeMessage(InputStream aMessage) {
        LOG.trace("writeMessage({})", aMessage);
        tapping.sendMessage(aMessage);
    }
}
