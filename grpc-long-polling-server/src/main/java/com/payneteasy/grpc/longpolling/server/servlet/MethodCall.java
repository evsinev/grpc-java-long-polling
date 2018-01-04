package com.payneteasy.grpc.longpolling.server.servlet;

import com.payneteasy.grpc.longpolling.common.StreamId;
import io.grpc.MethodDescriptor;

import java.util.StringTokenizer;

public class MethodCall {

    private final StreamId                    streamId;
    private final String                      methodFullname;
    private final MethodDescriptor.MethodType methodType;

    public MethodCall(StreamId aStreamId, String aMethod, MethodDescriptor.MethodType aType) {
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

            return new MethodCall(StreamId.parse(transport, stream), clazz + "/" + method, MethodDescriptor.MethodType.valueOf(type));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse '" + aPath + "'", e);
        }
    }

    public String getMethod() {
        return methodFullname;
    }

    public MethodDescriptor.MethodType getType() {
        return methodType;
    }

    public StreamId getStreamId() {
        return streamId;
    }

    @Override
    public String toString() {
        return "MethodCall{" +
                "streamId=" + streamId +
                ", methodFullname='" + methodFullname + '\'' +
                ", methodType=" + methodType +
                '}';
    }
}
