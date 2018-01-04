package com.payneteasy.grpc.longpolling.client;

import java.util.Objects;
import java.util.UUID;

public class TransportId {

    private final String id;

    private TransportId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportId that = (TransportId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static TransportId generateNew() {
        return new TransportId(UUID.randomUUID().toString());
    }

    public String getTransportId() {
        return id;
    }
}
