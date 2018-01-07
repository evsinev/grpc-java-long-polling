package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.client.util.ServerEndPoint;
import com.payneteasy.grpc.longpolling.common.MethodDirection;
import com.payneteasy.grpc.longpolling.common.Streams;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientUnary implements IHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUnary.class);

    private final    ClientStreamListener listener;
    private final    URL                  sendUrl;
    private final    Streams              streams = new Streams(LOG);

    public HttpClientUnary(ServerEndPoint aEndPoint, ClientStreamListener aListener) {
        listener = aListener;
        sendUrl  = aEndPoint.createUrl(MethodDirection.UNARY);
    }

    @Override
    public void sendMessage(InputStream aInputStream) {
        try {
            LOG.debug("Sending to {} ...", sendUrl);

            HttpURLConnection connection = (HttpURLConnection) sendUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);

            streams.sendMessage(aInputStream, connection);

            int status = connection.getResponseCode();
            if(status != 200) {
                fireError(Status.ABORTED, new IOException(connection.getResponseMessage()), "Invalid status code " + status);
                return;
            }

            streams.messageAvailable(listener, connection);
            listener.closed(Status.OK, new Metadata());

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
       // we can't cancel HttpURLConnection execution
    }

}
