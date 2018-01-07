package com.payneteasy.grpc.longpolling.client.http;

import com.payneteasy.grpc.longpolling.common.TransportId;
import io.grpc.Status;
import io.grpc.internal.ManagedClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransportHttpServiceNoop implements ITransportHttpService {

    private static final Logger LOG = LoggerFactory.getLogger(TransportHttpServiceNoop.class);

    private final TransportId transportId;

    public TransportHttpServiceNoop(TransportId transportId) {
        this.transportId = transportId;
    }

    @Override
    public void sendClose(TransportId aTransportId) {
        LOG.trace("sendClose({})", aTransportId);
    }

    @Override
    public void sendOpenTransport(ManagedClientTransport.Listener aListener) {
        LOG.trace("sendOpenTransportClose({})", aListener);
        LOG.debug("Transport {} ready", transportId);
        aListener.transportReady();
    }

    @Override
    public void cancelStream(Status aReason) {
        LOG.trace("cancelStream({})", aReason);
    }
}
