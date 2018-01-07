package com.payneteasy.grpc.longpolling.common;

public enum  MethodDirection {

      UNARY // == MethodType.UNARY  One request message followed by one response message
    , UP    // == BIDI_STREAMING
    , DOWN  // == BIDI_STREAMING
    , TAP   // == SERVER_STREAMING
}
