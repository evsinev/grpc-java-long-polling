package com.payneteasy.grpc.longpolling.server.servlet;

import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.common.TransportId;

import java.io.IOException;
import java.io.InputStream;

public interface ITransportRegistry {

    TransportHolder getOrCreateTransportListener(TransportId transportId);

    void removeTransport(TransportId aTransportId);

    void enqueueMessage(StreamId aStreamId, InputStream aMessage);

    TransportHolder getReadyMessages(StreamId aStreamId, String method) throws InterruptedException, IOException;
}
