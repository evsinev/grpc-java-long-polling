package com.payneteasy.grpc.longpolling.client.http;

import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;

import java.io.InputStream;

public class HttpClientDelayedInit implements IHttpClient {

    private volatile IHttpClient   delegate;

    private final IDelegateCreator delegateCreator;

    public interface IDelegateCreator {
        IHttpClient create(ClientStreamListener aListener);
    }

    public HttpClientDelayedInit(IDelegateCreator aCreator) {
        delegateCreator = aCreator;
    }

    public void initialiseDelegate(ClientStreamListener aListener) {
        delegate = delegateCreator.create(aListener);
    }

    @Override
    public void sendMessage(InputStream aInputStream) {
        if(delegate == null) {
            throw new IllegalStateException("delegate is not set");
        }
        delegate.sendMessage(aInputStream);
    }

    @Override
    public void cancelStream(Status aReason) {
        if(delegate == null) {
            throw new IllegalStateException("delegate is not set");
        }
        delegate.cancelStream(aReason);
    }
}
