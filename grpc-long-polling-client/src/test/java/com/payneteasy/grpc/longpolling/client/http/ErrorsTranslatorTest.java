package com.payneteasy.grpc.longpolling.client.http;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ErrorsTranslatorTest {

    private final Logger               log      = mock(Logger.class);
    private final ClientStreamListener listener = mock(ClientStreamListener.class);
    private final ErrorsTranslator     errors   = new ErrorsTranslator(log, listener, createUrl());

    private URL createUrl()  {
        try {
            return new URL("http://localhost");
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void abort() throws Exception {
        errors.abort(new HttpStatus(1, "2"));
        verify(listener).closed(eq(Status.ABORTED), any(Metadata.class));
    }

    @Test
    public void notFound() throws Exception {
        errors.notFound(new FileNotFoundException());
        verify(listener).closed(eq(Status.NOT_FOUND), any(Metadata.class));
    }

    @Test
    public void unavailable() throws Exception {
        errors.unavailable(new ConnectException());
        verify(listener).closed(eq(Status.UNAVAILABLE), any(Metadata.class));
    }

    @Test
    public void dataLoss() throws Exception {
        errors.dataLoss(new IOException());
        verify(listener).closed(eq(Status.DATA_LOSS), any(Metadata.class));
    }

    @Test
    public void internal() throws Exception {
        errors.internal(new Exception());
        verify(listener).closed(eq(Status.INTERNAL), any(Metadata.class));
    }

    @Test
    public void tryCatchDataLoss() throws Exception {
        errors.tryCatch(() -> {
            throw new IOException();
        });
        verify(listener).closed(eq(Status.DATA_LOSS), any(Metadata.class));
    }

    @Test
    public void tryCatchInternal() throws Exception {
        errors.tryCatch(() -> {
            throw new Exception();
        });
        verify(listener).closed(eq(Status.INTERNAL), any(Metadata.class));
    }

    @Test
    public void tryCatchUnavailable() throws Exception {
        errors.tryCatch(() -> {
            throw new ConnectException();
        });
        verify(listener).closed(eq(Status.UNAVAILABLE), any(Metadata.class));
    }

    @Test
    public void tryCatchNotFound() throws Exception {
        errors.tryCatch(() -> {
            throw new FileNotFoundException();
        });
        verify(listener).closed(eq(Status.NOT_FOUND), any(Metadata.class));
    }


}