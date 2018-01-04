package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.common.TransportId;
import io.grpc.Status;
import io.grpc.internal.ManagedClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransportHttpServiceUrlConnection implements ITransportHttpService {

    private static final Logger LOG = LoggerFactory.getLogger(TransportHttpServiceUrlConnection.class);

    private final URL transportUrl;

    public TransportHttpServiceUrlConnection(URL aTransportUrl, TransportId aTransportId) {
        transportUrl = aTransportUrl;
    }

    @Override
    public void sendClose(TransportId aTransportId) {
        LOG.trace("sendClose({})", aTransportId);
    }

    @Override
    public void sendOpenTransport(ManagedClientTransport.Listener aListener) {
        LOG.trace("sendOpenTransportClose({})", aListener);
        aListener.transportInUse(true);
        try {
            HttpURLConnection connection = (HttpURLConnection) transportUrl.openConnection();
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ( (line = in.readLine()) != null) {
                    LOG.debug("Read: {}", line);
                }
            }
            aListener.transportInUse(false);
            aListener.transportReady();
        } catch (FileNotFoundException e) {
            LOG.error("Url not found " + transportUrl, e);
            aListener.transportShutdown(Status.NOT_FOUND);
        } catch (ConnectException e) {
            aListener.transportShutdown(Status.UNAVAILABLE);
            LOG.error("Error while connecting to " + transportUrl, e);
        } catch (IOException e) {
            aListener.transportShutdown(Status.DATA_LOSS);
            LOG.error("Error while send transport", e);
        }
    }

    @Override
    public void cancelStream(Status aReason) {
        LOG.trace("cancelStream({})", aReason);
    }
}
