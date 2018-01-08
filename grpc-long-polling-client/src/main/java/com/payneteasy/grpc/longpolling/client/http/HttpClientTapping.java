package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.client.util.ConnectionOptions;
import com.payneteasy.grpc.longpolling.client.util.ServerEndPoint;
import com.payneteasy.grpc.longpolling.common.MessagesContainer;
import com.payneteasy.grpc.longpolling.common.MethodDirection;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class HttpClientTapping implements IHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientTapping.class);

    private final ClientStreamListener              listener;
    private final URL                               url;
    private final IOnCompleteAction                 onCompleteAction;
    private final ConnectionOptions                 connectionOptions;

    public interface IOnCompleteAction {
        void onComplete(MessagesContainer messages) throws IOException;
    }

    public HttpClientTapping(ClientStreamListener aListener, ServerEndPoint aEndpoint, IOnCompleteAction aOnCompleteAction) {
        connectionOptions = aEndpoint.getConnectionOptions();
        listener          = aListener;
        url               = aEndpoint.createUrl(MethodDirection.TAP);
        onCompleteAction  = aOnCompleteAction;
    }

    @Override
    public void sendMessage(InputStream aInputStream) {
        ErrorsTranslator errors = new ErrorsTranslator(LOG, listener, url);
        errors.tryCatch(() -> {
            HttpConnection http   = new HttpConnection(LOG, url, connectionOptions);
            HttpStatus     status = http.doPost(aInputStream);

            if(status.wasNotOk()) {
                errors.abort(status);
                return;
            }

            onCompleteAction.onComplete(http.readMessagesContainer());
        });
    }

    @Override
    public void cancelStream(Status aReason) {
        // can't cancel http connection once it sent via HTTPUrlConnection
    }


}
