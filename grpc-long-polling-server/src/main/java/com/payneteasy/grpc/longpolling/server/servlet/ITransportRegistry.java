package com.payneteasy.grpc.longpolling.server.servlet;

import com.payneteasy.grpc.longpolling.common.StreamId;
import com.payneteasy.grpc.longpolling.common.TransportId;
import com.payneteasy.grpc.longpolling.server.LongPollingServerTransport;

import java.io.InputStream;

public interface ITransportRegistry {

    TransportHolder getOrCreateTransportListener(TransportId transportId, LongPollingServerTransport aServerTransport);

    void removeTransport(TransportId aTransportId);

    void enqueueMessage(StreamId aStreamId, InputStream aMessage);

}
