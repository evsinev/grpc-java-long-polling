package com.payneteasy.grpc.longpolling.common;

import com.payneteasy.tlv.HexUtil;
import io.grpc.Drainable;
import io.grpc.internal.ClientStreamListener;
import io.grpc.internal.IoUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class Streams {

    private final Logger log;

    public Streams(Logger aLogger) {
        log = aLogger;
    }

    /**
     *
     * @param aInputStream can be Drainable
     */
    public void sendMessage(InputStream aInputStream, HttpURLConnection aConnection) throws IOException {
        sendMessage(aInputStream, aConnection.getOutputStream());
    }

    public void sendMessage(InputStream aInputStream, OutputStream aOut) throws IOException {
        if(aInputStream instanceof Drainable && !log.isDebugEnabled()) {
            ((Drainable) aInputStream).drainTo(aOut);
        } else {
            byte[] outputBytes = IoUtils.toByteArray(aInputStream);
            log.debug("OUTPUT: {}", HexUtil.toFormattedHexString(outputBytes));
            aOut.write(outputBytes);
        }
    }

    public void messageAvailable(ClientStreamListener aListener, HttpURLConnection aConnection) throws IOException {
        messageAvailable(aListener, aConnection.getInputStream());
    }

    public void messageAvailable(ClientStreamListener aListener, InputStream aInputStream) throws IOException {
        try(InputStream in = aInputStream) {
            byte[] bytes = IoUtils.toByteArray(in);
            log.debug("INPUT: {}", HexUtil.toFormattedHexString(bytes));
            aListener.messagesAvailable(new SingleMessageProducer(getClass().getSimpleName(), bytes));
        }

    }
}
