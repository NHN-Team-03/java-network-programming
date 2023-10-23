package com.nhnacadmemy.shttpd;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class DeleteHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String fileName = exchange.getRequestURI().getPath();
        fileName = fileName.substring(1);

        List<String> fileList = FileList.getFileLists();

        // TODO : 파일을 지울 수 있으면 지우고 204 No Content를 보낸다.
        if (fileList.contains(fileName)) {

            boolean delete = FileList.findFile(fileName).delete();
            if (delete) {
                Response.send(exchange, HttpURLConnection.HTTP_NO_CONTENT, noContent());
            } else {
                Response.send(exchange, HttpURLConnection.HTTP_FORBIDDEN, forbidden());
            }
        }

        // TODO : 지정된 파일이 존재하지 않으면 204 No Content를 보낸다.
        if (!fileList.contains(fileName)) {
            Response.send(exchange, HttpURLConnection.HTTP_NO_CONTENT, noContent());
        }

        // TODO : 지정된 파일을 지울 수 없다면 403 Forbidden을 보낸다.


    }

    private StringBuilder forbidden() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"seungjo\">");
        sb.append("       <title>Seungjo's shttpd</title>");
        sb.append("   </head>");
        sb.append("   <body>");
        sb.append("       <h3> 해당 파일을 지울 수 없습니다!!! </h3>");
        sb.append("   </body>");
        sb.append("</html>");

        return sb;
    }

    private StringBuilder noContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("    <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"seungjo\">");
        sb.append("       <title>Seungjo's shttpd</title>");
        sb.append("   </head>");
        sb.append("    <body>");
        sb.append("        <h3>204 No Content!</h1>");
        sb.append("    </body>");
        sb.append("</html>");

        return sb;

    }
}
