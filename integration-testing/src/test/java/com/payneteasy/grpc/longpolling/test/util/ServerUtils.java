package com.payneteasy.grpc.longpolling.test.util;

import com.payneteasy.grpc.longpolling.server.LongPollingServer;
import com.payneteasy.grpc.longpolling.server.LongPollingServerBuilder;
import io.grpc.BindableService;
import io.grpc.Server;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ServerUtils {
    public static LongPollingServer createLongPollingServer(BindableService aService) {
        try {
            LongPollingServer pollingServer = new LongPollingServer();

            Server grpcServer = LongPollingServerBuilder.forPort(-1)
                    .longPollingServer(pollingServer)
                    .addService(aService)
                    .build();
            grpcServer.start();
            return pollingServer;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
