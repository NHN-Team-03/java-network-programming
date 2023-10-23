package com.nhnacadmemy.shttpd;

import static com.nhnacadmemy.shttpd.FileList.getFileLists;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

public class PostHandler implements HttpHandler {

    /**
     * POST 요청을 처리한다.
     */

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // exchnage에서 받아온 body의 값을 읽어온다.

        // TODO: Content-Type에 multipart-form/data가 아닌 경우 405 Method Not Allowed 응답을 보낸다.
        List<String> strings = exchange.getRequestHeaders().get("Content-Type");
        String boundary = "";
        boolean isMultipart = false;
        for (String string : strings) {
            if (string.contains("multipart/form-data")) {
                isMultipart = true;
            }
            if (string.contains("boundary")) {
                boundary = string.split("boundary=")[1];
            }
        }

        if (isMultipart) {
            InputStream requestBody = exchange.getRequestBody();
            BufferedReader br = new BufferedReader(new InputStreamReader(requestBody));

            // TODO: bufferedReader로 읽어온 값에서 첫번째 줄에 있는 filename을 읽어온다.
            br.readLine();
            String fileName = br.readLine().split("filename=")[1].replaceAll("\"", "");


            List<String> fileList = getFileLists();
            if (fileList.contains(fileName)) {

                // TODO: 이미 존재하는 파일일 경우 409 Conflict 응답을 보낸다.
                Response.send(exchange, HttpURLConnection.HTTP_CONFLICT, conflict(fileName));
            } else {

                FileWriter fw = new FileWriter(FileList.filePath + "/" + fileName);

                // TODO: Body의 본문만 파일에 저장한다.
                boolean inPart = false;
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) {
                        inPart = true;
                    } else if (line.equals("--" + boundary + "--")) {
                        break;
                    }

                    if (inPart && (!line.isEmpty() || sb.length() > 0)) {
                        sb.append(line).append("\n");
                    }
                }

                fw.write(sb.toString());

                fw.flush();
                fw.close();

                Response.send(exchange, HttpURLConnection.HTTP_CREATED, create(fileName));
            }


        } else {
            // TODO: Content-Type이 multipart/form-data가 아닌 경우 405 Method Not Allowed 응답을 보낸다.
            Response.send(exchange, HttpURLConnection.HTTP_BAD_METHOD, methodNotAllowed());
        }

    }

    private StringBuilder create(String fileName) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"seungjo\">");
        sb.append("       <title>Seungjo's shttpd</title>");
        sb.append("   </head>");

        sb.append("   <body>");
        sb.append("       <h3>201 Created</h3>");
        sb.append("       <h3>File Name: ").append(fileName).append("</h3>");
        sb.append("   </body>");
        sb.append("</html>");

        return sb;
    }

    private StringBuilder conflict(String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"seungjo\">");
        sb.append("       <title>Seungjo's shttpd</title>");
        sb.append("   </head>");

        sb.append("   <body>");
        sb.append("       <h3>409 Conflict!</h3>");
        sb.append("       <h3>File Name: ").append(fileName).append("</h3>");
        sb.append("   </body>");
        sb.append("</html>");

        return sb;
    }

    private StringBuilder methodNotAllowed() {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"seungjo\">");
        sb.append("       <title>Seungjo's shttpd</title>");
        sb.append("   </head>");

        sb.append("   <body>");
        sb.append("       <h3>405 Method Not Allowed</h3>");
        sb.append("   </body>");
        sb.append("</html>");

        return sb;
    }

}
