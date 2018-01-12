package com.payneteasy.grpc.longpolling.common;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public final class Uuids {

    private Uuids() {
    }

    public static String generateUuid() {
        return uuidToBase64(UUID.randomUUID());
    }

    private static String uuidToBase64(UUID uuid) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return encoder.encodeToString(buffer.array());
    }

}
