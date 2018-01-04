package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.client.util.Urls;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

public class StreamHttpServiceUrlConnection implements IStreamHttpService {

    private static final Logger LOG = LoggerFactory.getLogger(StreamHttpServiceUrlConnection.class);

    private final URL sendUrl;

    public StreamHttpServiceUrlConnection(URL aBaseUrl, StreamId aStreamId, MethodDescriptor<?, ?> aMethod, ClientStreamListener aListener) {
        sendUrl = Urls.createStreamUrl(aBaseUrl, aStreamId, aMethod);
    }

    @Override
    public void sendMessage(InputStream aInputStream) {
        LOG.trace("Sending to {} ...", sendUrl);
        try {
            Thread.sleep(20_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        HttpURLConnection connection = (HttpURLConnection) sendUrl.openConnection();
//        try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//            String line;
//            while ( (line = in.readLine()) != null) {
//                LOG.debug("Read: {}", line);
//            }
//        }
//        aListener.transportInUse(false);
//        aListener.transportReady();

    }

    @Override
    public void cancelStream(Status aReason) {

    }

    @Override
    public void setClientStreamListener(ClientStreamListener aListener) {
        
    }
}
