package com.payneteasy.grpc.longpolling.server.servlet.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanupRegistryThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(CleanupRegistryThread.class);

    private final ITransportRegistry registry;

    public CleanupRegistryThread(ITransportRegistry aRegistry) {
        setName("cleanup-transport-registry");
        registry = aRegistry;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {

                Thread.sleep(60_000);
                registry.cleanInactiveTransports();

            } catch (InterruptedException e) {
                LOG.debug("Interrupted");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }


}
