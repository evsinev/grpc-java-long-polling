package com.payneteasy.grpc.longpolling.common;

import java.util.Objects;
import java.util.UUID;

public class StreamId {

    private final TransportId transportId;
    private final String id;

    public static StreamId generateNew(TransportId aTransportId) {
        return new StreamId(aTransportId, UUID.randomUUID().toString());
    }

    private StreamId(TransportId transportId, String id) {
        this.transportId = transportId;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreamId streamId = (StreamId) o;
        return Objects.equals(transportId, streamId.transportId) &&
                Objects.equals(id, streamId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportId, id);
    }

    @Override
    public String toString() {
        return "StreamId{" +
                "transportId=" + transportId.getTransportId() +
                ", id='" + id + '\'' +
                '}';
    }

    public String getStreamId() {
        return id;
    }


    public TransportId getTransportId() {
        return transportId;
    }


    public static StreamId parse(String aTransportId, String aStreamId) {
        return new StreamId(TransportId.parse(aTransportId), aStreamId);
    }
}
