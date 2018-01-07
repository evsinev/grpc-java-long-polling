package com.payneteasy.grpc.longpolling.client.util;

import com.payneteasy.grpc.longpolling.common.MethodDirection;
import com.payneteasy.grpc.longpolling.common.StreamId;
import io.grpc.MethodDescriptor;

import java.net.URL;

public class ServerEndPoint {

    private final URL                    baseUrl;
    private final StreamId               streamId;
    private final MethodDescriptor<?, ?> method;

    public ServerEndPoint(URL aBaseUrl, StreamId streamId, MethodDescriptor<?, ?> method) {
        this.baseUrl = aBaseUrl;
        this.streamId = streamId;
        this.method = method;
    }

    public URL createUrl(MethodDirection aDirection) {
        return Urls.appendPaths(baseUrl
                , method.getFullMethodName()
                , aDirection
                , streamId.getTransportId().getTransportId()
                , streamId.getStreamId()
        );
    }

    public StreamId getStreamId() {
        return streamId;
    }
}
