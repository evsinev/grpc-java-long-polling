package com.payneteasy.grpc.longpolling.client.http;

import io.grpc.Status;

import java.io.InputStream;

public interface IHttpClient {

    void sendMessage(InputStream aInputStream);

    void cancelStream(Status aReason);

}
