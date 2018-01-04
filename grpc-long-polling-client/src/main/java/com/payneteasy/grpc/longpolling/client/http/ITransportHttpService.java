package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.client.TransportId;
import io.grpc.Status;
import io.grpc.internal.ManagedClientTransport;

public interface ITransportHttpService {

    void sendClose(TransportId aTransportId);

    void sendOpenTransport(ManagedClientTransport.Listener aListener);

    void cancelStream(Status aReason);

}
