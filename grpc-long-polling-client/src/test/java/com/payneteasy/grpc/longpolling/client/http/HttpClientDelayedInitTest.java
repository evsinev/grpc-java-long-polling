package com.payneteasy.grpc.longpolling.client.http;

import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;

public class HttpClientDelayedInitTest {

    @Test
    public void initialiseDelegate() throws Exception {
        IHttpClient mock = Mockito.mock(IHttpClient.class);
        HttpClientDelayedInit client = new HttpClientDelayedInit(aListener -> mock);

        client.initialiseDelegate(Mockito.mock(ClientStreamListener.class));
        client.sendMessage(null);
        client.cancelStream(Status.ABORTED);

        Mockito.verify(mock).sendMessage(Mockito.any(InputStream.class));
        Mockito.verify(mock).cancelStream(Mockito.eq(Status.ABORTED));
    }

    @Test
    public void errorIvoke() throws Exception {
        IHttpClient mock = Mockito.mock(IHttpClient.class);
        HttpClientDelayedInit client = new HttpClientDelayedInit(aListener -> mock);


        try {
            client.sendMessage(null);
        } catch (IllegalStateException e) {
            Assert.assertEquals("delegate is not set", e.getMessage());
        }

        try {
            client.cancelStream(Status.ABORTED);
        } catch (IllegalStateException e) {
            Assert.assertEquals("delegate is not set", e.getMessage());
        }
    }

}