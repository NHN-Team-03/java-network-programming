package com.nhnacademy.yongjun.shttpd;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileHandle implements HttpHandler {
    final String FAVI = "/favicon.ico";
    private final String DEFAULT_PATH = "/Users/jun/Documents/java-network-programming/";
    private final String FILEPATH = "/Users/jun/Documents/Text.txt/";
    private final String charset = "UTF-8";
    HttpURLConnection connection;
    URL url;

    public void handle(HttpExchange exchange) throws IOException {
        printstatus(exchange);
        InputStream inputStream = exchange.getRequestBody();
        String tempPath = exchange.getRequestURI().getPath();
        String httpPath = DEFAULT_PATH;
        if (!tempPath.equals(FAVI)) {
            httpPath += tempPath;

        }
        String filePath = FILEPATH;
        url = new URL(httpPath);
        connection = (HttpURLConnection) url.openConnection();



        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        Headers headers = exchange.getRequestHeaders();
        String contentType = headers.getFirst("Content-Type");
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            sendResponse(exchange, 400, "Bad Request");
            return;
        }

        String boundary = extractBoundary(contentType);
        if (boundary == null) {
            sendResponse(exchange, 400, "Bad Request");
            return;
        }

        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        try (OutputStream os = connection.getOutputStream();
             FileInputStream fis = new FileInputStream(filePath)) {
//            String boundary = "----WebKitBoundary7MA$YWxkasTzsoZu023gw";
            String lineEnd = "\r\n";

            String fieldName = "upload";
            String fileName = new File(filePath).getName();
//            String contentType = "application/x-www-form-urlencoded";

            os.write(("--" + boundary + lineEnd).getBytes());
            os.write(("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"" + lineEnd).getBytes());
            os.write(("Content-Type: " + contentType + lineEnd).getBytes());
            os.write(lineEnd.getBytes());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            os.write(lineEnd.getBytes());
            os.write(("--" + boundary + "--" + lineEnd).getBytes());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        Map<String, List<String>> parameters = exchange.getRequestHeaders();
//        if (parameters.containsKey("upload")) {
//            List<String> fileContents = parameters.get("upload");
//            if (fileContents.size() > 0) {
//                // 파일 내용을 저장할 디렉토리 설정
//                String uploadDir = "./uploads/";
//                File dir = new File(uploadDir);
//                if (!dir.exists()) {
//                    dir.mkdir();
//                }
//
//                // 업로드된 파일을 저장
//                String filename = UUID.randomUUID().toString();
//                String filePath = uploadDir + filename;
//                try (OutputStream os = new FileOutputStream(filePath)) {
//                    os.write(fileContents.get(0).getBytes());
//                } catch (IOException e) {
//                    sendResponse(exchange, 403, "Forbidden");
//                    return;
//                }
//
//                sendResponse(exchange, 200, "OK");
//            } else {
//                sendResponse(exchange, 400, "Bad Request");
//            }
//        } else {
//            sendResponse(exchange, 400, "Bad Request");
//        }
    }

    private void printstatus(HttpExchange exchange) throws IOException {
        RootHandler rootHandler = new RootHandler();
        rootHandler.handle(exchange);
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.sendResponseHeaders(code, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String extractBoundary(String contentType) {
        String[] parts = contentType.split(";");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("boundary=")) {
                return part.substring(9);
            }
        }
        return null;
    }


    private void printErr(HttpExchange exchange, OutputStream respBody) throws IOException {
        StringBuilder responseContent = new StringBuilder();
        responseContent.append("<!DOCTYPE html>");
        responseContent.append("<html>");
        responseContent.append("   <head>");
        responseContent.append("       <meta charset=\"UTF-8\">");
        responseContent.append("       <title>File List</title>");
        responseContent.append("   </head>");
        responseContent.append("   <body>");
        responseContent.append("       <h1>404 Not Found</h1>");
        ByteBuffer bb = Charset.forName("UTF-8").encode(responseContent.toString());
        int contentLength = bb.limit();
        byte[] content = new byte[contentLength];
        bb.get(content, 0, contentLength);

        // Set Response Headers
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "text/html;charset=UTF-8");
        headers.add("Content-Length", String.valueOf(contentLength));

        // Send Response Headers
        exchange.sendResponseHeaders(200, contentLength);

        respBody.write(content);

        // Close Stream
        // 반드시, Response Header를 보낸 후에 닫아야함
        respBody.close();
    }

}
