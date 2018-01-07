package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.client.util.ServerEndPoint;
import com.payneteasy.grpc.longpolling.common.*;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpClientDownloading implements IHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientDownloading.class);

    public static final InputStream EMPTY_INPUT = new ByteArrayInputStream(new byte[0]);

    private volatile boolean                           active;

    private final    ClientStreamListener              listener;
    private final    AtomicBoolean                     transportActive;
    private final    URL                               sendUrl;
    private final    SlotSender<SingleMessageProducer> slotSender;
    private final    StreamId                          streamId;

    public HttpClientDownloading(ServerEndPoint aEndpoint
            , AtomicBoolean aTransportActive, SlotSender<SingleMessageProducer> aSlotSender
            , ClientStreamListener aListener
    ) {
        slotSender      = aSlotSender;
        transportActive = aTransportActive;
        streamId        = aEndpoint.getStreamId();
        listener        = aListener;
        active          = true;
        sendUrl         = aEndpoint.createUrl(MethodDirection.DOWN);
    }

    @Override
    public void sendMessage(InputStream aInputStream) {
        if (active && transportActive.get()) {
            try {
                LOG.debug("Sending to {} ...", sendUrl);

                HttpURLConnection connection = (HttpURLConnection) sendUrl.openConnection();
                connection.connect();
                
                int status = connection.getResponseCode();
                if(status == 410) { // transport is inactive
                    LOG.warn("Transport is inactive on server side for stream {}", streamId);
                    listener.closed(Status.OK, new Metadata());
                    return;
                }
                
                if(status != 200) {
                    fireError(Status.ABORTED, new IOException(connection.getResponseMessage()), "Invalid status code " + status);
                    return;
                }

                MessagesContainer messages = MessagesContainer.parse(connection.getInputStream());
                for (InputStream inputStream : messages.getInputs()) {
                    slotSender.onSendMessage(SingleMessageProducer.readFully(getClass(), inputStream));
                }

            } catch (FileNotFoundException e) {
                fireError(Status.NOT_FOUND, e, "Not found");
            } catch (ConnectException e) {
                if(transportActive.get()) {
                    fireError(Status.UNAVAILABLE, e, "Cannot connect");
                } else {
                    LOG.warn("Skipping throwing an error because transport is inactive. Error was '{}'", e.getMessage());
                }
            } catch (IOException e) {
                fireError(Status.DATA_LOSS, e, "IO error");
            }
        } else {
            LOG.warn("Transport is inactive on client side for stream {} [active={}, transportActive{}]", streamId, active, transportActive);
        }

    }

    private void fireError(Status aStatus, Exception aException, String aReason) {
        LOG.error(aReason + " " + sendUrl, aException);
        if(listener != null) {
            listener.closed(aStatus, new Metadata());
        }
        active = false;
    }

    @Override
    public void cancelStream(Status aReason) {
        active = false;
    }

}
