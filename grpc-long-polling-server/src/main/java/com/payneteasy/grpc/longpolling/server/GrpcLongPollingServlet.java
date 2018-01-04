package com.payneteasy.grpc.longpolling.server;

import com.payneteasy.tlv.HexUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GrpcLongPollingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
//        AsyncContext asyncContext = aRequest.startAsync();
        aResponse.getOutputStream().write(HexUtil.parseHex("0a06 7465 7374 2032"));
    }

}
