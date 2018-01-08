package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.client.util.ConnectionOptions;
import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.tlv.HexUtil;
import io.grpc.Drainable;
import io.grpc.internal.IoUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection {

    private final Logger            log;
    private final HttpURLConnection connection;
    private final URL               url;

    public HttpConnection(Logger aLogger, URL aUrl, ConnectionOptions aOptions) throws IOException {
        log        = aLogger;
        url        = aUrl;
        connection = (HttpURLConnection) aUrl.openConnection();
        aOptions.configure(connection);
    }

//    public void fireMessageAvailable(ClientStreamListener aListener) throws IOException {
//        new Streams(log).messageAvailable(aListener, connection);
//    }

    public MessagesContainer readMessagesContainer() throws IOException {
        log.debug("Reading for output from server ...");
        return MessagesContainer.parse(connection.getInputStream());
    }

    public HttpStatus doPost(InputStream aInputStream) throws IOException {
        log.debug("Sending POST to {} ...", url);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.connect();
        return sendMessage(aInputStream);
    }

    private HttpStatus sendMessage(InputStream aInputStream) throws IOException {
        OutputStream out = connection.getOutputStream();
        if(aInputStream instanceof Drainable && !log.isDebugEnabled()) {
            ((Drainable) aInputStream).drainTo(out);
        } else {
            byte[] outputBytes = IoUtils.toByteArray(aInputStream);
            log.debug("OUTPUT: {}", HexUtil.toFormattedHexString(outputBytes));
            out.write(outputBytes);
        }
        return new HttpStatus(connection.getResponseCode(), connection.getResponseMessage());
    }

}
