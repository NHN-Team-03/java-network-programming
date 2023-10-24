package com.nhnacadmemy.shttpd;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class RootHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // TODO: RootHandler에서 어떤 메서드를 호출할지 결정

        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                GetHandler getHandler = new GetHandler();
                getHandler.handle(exchange);
                break;

            case "POST":
                PostHandler postHandler = new PostHandler();
                postHandler.handle(exchange);
                break;

            case "DELETE":
                DeleteHandler deleteHandler = new DeleteHandler();
                deleteHandler.handle(exchange);
                break;

            default:
                throw new IllegalArgumentException("Invalid method: " + method);
        }
    }
}
