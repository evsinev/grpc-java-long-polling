package com.payneteasy.grpc.longpolling.server.servlet;

import com.payneteasy.grpc.longpolling.common.MethodDirection;
import com.payneteasy.grpc.longpolling.common.StreamId;

import java.util.StringTokenizer;

public class MethodCall {

    private final StreamId                    streamId;
    private final String                      methodFullname;
    private final MethodDirection             methodType;

    public MethodCall(StreamId aStreamId, String aMethod, MethodDirection aType) {
        streamId = aStreamId;
        methodFullname = aMethod;
        methodType = aType;
    }

    public static MethodCall parse(String aPath) {

        try {
            StringTokenizer st = new StringTokenizer(aPath, "/ ");
            String clazz     = st.nextToken();
            String method    = st.nextToken();
            String type      = st.nextToken();
            String transport = st.nextToken();
            String stream    = st.nextToken();

            return new MethodCall(StreamId.parse(transport, stream), clazz + "/" + method, MethodDirection.valueOf(type));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse '" + aPath + "'", e);
        }
    }

    public String getMethod() {
        return methodFullname;
    }

    public MethodDirection getType() {
        return methodType;
    }

    public StreamId getStreamId() {
        return streamId;
    }

    @Override
    public String toString() {
        return methodType +
                ", " + methodFullname +
                ", " + streamId;
    }
}
