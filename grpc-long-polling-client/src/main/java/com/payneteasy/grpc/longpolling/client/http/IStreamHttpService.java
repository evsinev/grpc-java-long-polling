package com.payneteasy.grpc.longpolling.client.http;

import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;

import java.io.InputStream;

public interface IStreamHttpService {

    void sendMessage(InputStream aInputStream);

    void cancelStream(Status aReason);

    void setClientStreamListener(ClientStreamListener aListener);
}
