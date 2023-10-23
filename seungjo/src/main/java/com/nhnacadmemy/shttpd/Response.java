package com.nhnacadmemy.shttpd;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Response {


    public static void send(HttpExchange exchange, int statusCode, StringBuilder sb) throws IOException {
        OutputStream body = exchange.getResponseBody();

        // UTF-8로 인코딩
        ByteBuffer bb = StandardCharsets.UTF_8.encode(sb.toString());
        int contentLength = bb.limit();
        byte[] content = new byte[contentLength];
        bb.get(content, 0, contentLength);

        // Response Header 설정
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "text/html;charset=UTF-8");
        headers.add("Content-Length", String.valueOf(contentLength));

        // Response 전송
        if (statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            exchange.sendResponseHeaders(statusCode, -1);
        } else {
            exchange.sendResponseHeaders(statusCode, contentLength);
        }

        body.write(content);

        body.close();
    }


}
