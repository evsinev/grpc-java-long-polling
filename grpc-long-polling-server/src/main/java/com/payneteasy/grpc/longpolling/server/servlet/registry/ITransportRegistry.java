package com.payneteasy.grpc.longpolling.server.servlet.registry;

import com.payneteasy.grpc.longpolling.common.TransportId;

public interface ITransportRegistry {

    TransportHolder findTransportHolder(TransportId transportId);

    void cleanInactiveTransports();
}
