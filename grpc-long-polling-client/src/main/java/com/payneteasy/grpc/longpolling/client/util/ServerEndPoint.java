package com.payneteasy.grpc.longpolling.client.util;

import com.payneteasy.grpc.longpolling.common.MethodDirection;
import com.payneteasy.grpc.longpolling.common.StreamId;
import io.grpc.MethodDescriptor;

import java.net.URL;

public class ServerEndPoint {

    private final ConnectionOptions      connectionOptions;
    private final StreamId               streamId;
    private final MethodDescriptor<?, ?> method;

    public ServerEndPoint(ConnectionOptions aConnectionFactory, StreamId streamId, MethodDescriptor<?, ?> method) {
        connectionOptions = aConnectionFactory;
        this.streamId = streamId;
        this.method = method;
    }

    public URL createUrl(MethodDirection aDirection) {
        return Urls.appendPaths(connectionOptions.getBaseUrl()
                , method.getFullMethodName()
                , aDirection
                , streamId.getTransportId().getTransportId()
                , streamId.getStreamId()
        );
    }

    public ConnectionOptions getConnectionOptions() {
        return connectionOptions;
    }

    public StreamId getStreamId() {
        return streamId;
    }
}
