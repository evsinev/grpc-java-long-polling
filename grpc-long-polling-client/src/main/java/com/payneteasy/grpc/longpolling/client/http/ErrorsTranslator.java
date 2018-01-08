package com.payneteasy.grpc.longpolling.client.http;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;

public class ErrorsTranslator {

    private final Logger log;
    private final ClientStreamListener listener;
    private final URL url;

    public interface IConnectionAction {
        void make() throws Exception;
    }

    public ErrorsTranslator(Logger aLog, ClientStreamListener aListener, URL aUrl) {
        listener = aListener;
        log = aLog;
        url = aUrl;
    }

    public void tryCatch(IConnectionAction aAction) {
        try {
            aAction.make();
        } catch (FileNotFoundException e) {
            notFound(e);
        } catch (ConnectException e) {
            unavailable(e);
        } catch (IOException e) {
            dataLoss(e);
        } catch (Exception e) {
            internal(e);
        }
    }

    public void abort(HttpStatus status) {
        log.error("Abort on " + status + " for {}", url);
        listener.closed(Status.ABORTED, new Metadata());

    }

    private void fireError(Status aStatus, Exception aException, String aReason) {
        log.error(aReason + " " + url, aException);
        if(listener != null) {
            listener.closed(aStatus, new Metadata());
        }
    }

    public void notFound(FileNotFoundException e) {
        fireError(Status.NOT_FOUND, e, "Not found");
    }

    public void unavailable(ConnectException e) {
        fireError(Status.UNAVAILABLE, e, "Cannot connect");
    }

    public void dataLoss(IOException e) {
        fireError(Status.DATA_LOSS, e, "IO error");
    }

    public void internal(Exception e) {
        fireError(Status.INTERNAL, e, "Internal error");
    }
}
