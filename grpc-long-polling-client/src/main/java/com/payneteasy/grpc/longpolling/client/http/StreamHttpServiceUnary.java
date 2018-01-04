package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.common.SingleMessageProducer;
import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.client.util.Urls;
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

public class StreamHttpServiceUnary implements IStreamHttpService {

    private static final Logger LOG = LoggerFactory.getLogger(StreamHttpServiceUnary.class);

    private final    URL                  sendUrl;
    private volatile ClientStreamListener listener;

    public StreamHttpServiceUnary(URL aBaseUrl, StreamId aStreamId, MethodDescriptor<?, ?> aMethod) {
        sendUrl = Urls.createStreamUrl(aBaseUrl, aStreamId, aMethod);
    }

    @Override
    public void sendMessage(InputStream aInputStream) {
        try {
            byte[] outputBytes = IoUtils.toByteArray(aInputStream);
            LOG.debug("Sending to {} ...", sendUrl);
            LOG.debug("OUTPUT: {}", HexUtil.toFormattedHexString(outputBytes));

            HttpURLConnection connection = (HttpURLConnection) sendUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.getOutputStream().write(outputBytes);

            int status = connection.getResponseCode();
            if(status != 200) {
                fireError(Status.ABORTED, new IOException(connection.getResponseMessage()), "Invalid status code " + status);
                return;
            }
            try(InputStream in = connection.getInputStream()) {
                byte[] bytes = IoUtils.toByteArray(in);
                LOG.debug("INPUT: {}", HexUtil.toFormattedHexString(bytes));
                listener.messagesAvailable(new SingleMessageProducer(bytes));
                listener.closed(Status.OK, new Metadata());
            }
        } catch (FileNotFoundException e) {
            fireError(Status.NOT_FOUND, e, "Not found");
        } catch (ConnectException e) {
            fireError(Status.UNAVAILABLE, e, "Cannot connect");
        } catch (IOException e) {
            fireError(Status.DATA_LOSS, e, "IO error");
        }

    }

    private void fireError(Status aStatus, Exception aException, String aReason) {
        LOG.error(aReason + " " + sendUrl, aException);
        if(listener != null) {
            listener.closed(aStatus, new Metadata());
        }
    }

    @Override
    public void cancelStream(Status aReason) {

    }

    @Override
    public void setClientStreamListener(ClientStreamListener aListener) {
        listener = aListener;
    }
}
