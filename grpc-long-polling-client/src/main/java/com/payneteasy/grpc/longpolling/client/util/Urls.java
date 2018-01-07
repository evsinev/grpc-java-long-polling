package com.payneteasy.grpc.longpolling.client.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Urls {

    public static URL appendPaths(URL aUrl, Object ... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(aUrl);
        for (Object arg : args) {
            String path = arg.toString();
            if(sb.charAt(sb.length() - 1) != '/' && !path.startsWith("/")) {
                sb.append('/');
            }
            sb.append(path);
        }
        try {
            return new URL(sb.toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format("Cannot create url from %s", sb), e);
        }
    }

}
