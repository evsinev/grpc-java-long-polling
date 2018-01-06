package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.client.util.Urls;
import com.payneteasy.grpc.longpolling.common.MethodDirection;
import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.common.Streams;
import com.payneteasy.tlv.HexUtil;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import io.grpc.internal.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class StreamHttpServiceDownloading implements IStreamHttpService {

    private static final Logger LOG = LoggerFactory.getLogger(StreamHttpServiceDownloading.class);

    private volatile ClientStreamListener listener;
    private volatile boolean              active = true;
    private final    AtomicBoolean        transportActive;
    private final    URL                  sendUrl;
    private final    Streams              streams = new Streams(LOG);

    public StreamHttpServiceDownloading(URL aBaseUrl, StreamId aStreamId, MethodDescriptor<?, ?> aMethod, AtomicBoolean aTransportActive) {
        sendUrl = Urls.createStreamUrl(aBaseUrl, aStreamId, aMethod, MethodDirection.DOWN);
        transportActive = aTransportActive;
    }

    @Override
    public void sendMessage(InputStream aInputStream) {
        while (active && transportActive.get()) {
            try {
                LOG.debug("Sending to {} ...", sendUrl);

                HttpURLConnection connection = (HttpURLConnection) sendUrl.openConnection();
                connection.connect();
                
                int status = connection.getResponseCode();
                if(status != 200) {
                    fireError(Status.ABORTED, new IOException(connection.getResponseMessage()), "Invalid status code " + status);
                    return;
                }

                streams.messageAvailable(listener, connection.getInputStream());

            } catch (FileNotFoundException e) {
                fireError(Status.NOT_FOUND, e, "Not found");
            } catch (ConnectException e) {
                if(transportActive.get()) {
                    fireError(Status.UNAVAILABLE, e, "Cannot connect");
                } else {
                    LOG.warn("Skipping throwing an error because transport is inactive. Error is {}", e.getMessage());
                }
            } catch (IOException e) {
                fireError(Status.DATA_LOSS, e, "IO error");
            }
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

    @Override
    public void setClientStreamListener(ClientStreamListener aListener) {
        listener = aListener;
    }
}
