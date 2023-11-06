package com.nhnacademy.yongjun.shttpd;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootHandler implements HttpHandler {

    final String FAVI = "/favicon.ico";
    private final String DEFAULT_PATH = "/Users/jun/Documents/java-network-programming";
    File[] files;

    File file;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String Methode = exchange.getRequestMethod();
        switch (Methode) {
            case "GET":
                httpGet(exchange);
            case "POST":
                httpPost(exchange);
            case "DELETE":
                httpDelete(exchange);
        }


    }

    private void httpDelete(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        String tempPath = exchange.getRequestURI().getPath();
        String filePath = DEFAULT_PATH;
        String fileName = "test";
        if (!tempPath.equals(FAVI)) {
            filePath += tempPath;
            fileName = tempPath;
        }
//        String fileInfo = readFile(input);
//        String fileName = findFileName(fileInfo);

        if (fileName.equals("0")) {
            System.out.println("qqqqq");
            printNoconten(exchange);
        } else {
            file = new File(filePath);
            if (file.delete()) {
                System.out.println("file = " + file.getName());
                printNoconten(exchange);
            }
//            files = file.listFiles();
//
//            for (File file : files) {
//                if (file.getName().equals(fileName)) {
//                    file.delete();
//                    printNoconten(exchange);
//                }
//            }


        }
    }


    private void httpPost(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        String tempPath = exchange.getRequestURI().getPath();
        String filePath = DEFAULT_PATH;
        if (!tempPath.equals(FAVI)) {
            filePath += tempPath;
        }
//        String fileName = "Text.txt";
        String fileInfo = readFile(input);
        String fileName = findFileName(fileInfo);

        if (fileName.equals("0")) {
            printFileNotFound(exchange);
        } else {
            File targetFile = new File(filePath + fileName);
            file = new File(filePath);
            files = file.listFiles();
            for (File file : files) {
                if (file.getName().equals(fileName)) {
                    printConflict(exchange, fileName);
                    return;
                }
            }

            saveFile(fileInfo, targetFile);
            printOk(exchange);

        }
    }

    private void printNoconten(HttpExchange exchange) throws IOException {
        String response = "204 No Content";
        exchange.sendResponseHeaders(204, -1);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void printConflict(HttpExchange exchange, String fileName) throws IOException {
        String response = "409 Conflict";
        exchange.sendResponseHeaders(409, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void printFileNotFound(HttpExchange exchange) throws IOException {
        String response = "File Not Found";
        exchange.sendResponseHeaders(405, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void printOk(HttpExchange exchange) throws IOException {
        String response = "File uploaded successfully!";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String findFileName(String fileInfo) {
        Pattern pattern = Pattern.compile("filename=\"(.*?)\"");
        Matcher matcher = pattern.matcher(fileInfo);

        // 매칭된 결과 확인
        if (matcher.find()) {
            // 첫 번째 매칭된 그룹을 출력
            String filename = matcher.group(1);
            System.out.println(filename);
            return filename;
        } else {
            System.out.println("파일 이름을 찾을 수 없습니다.");
            return "0";
        }
    }


    private void saveFile(String input, File targetFile) {
        try (FileWriter writer = new FileWriter(targetFile)) {
            String pattern = "Content-Type: text/plain\\s*(.*?)\\s*\\-\\-";
            Pattern regex = Pattern.compile(pattern, Pattern.DOTALL);
            Matcher matcher = regex.matcher(input);

            if (matcher.find()) {
                writer.write(matcher.group(1).trim());
            }

//
//            StringTokenizer tokenizer = new StringTokenizer(input);
//            StringBuilder sb = new StringBuilder();
//            while (tokenizer.hasMoreTokens()) {
//                String line = tokenizer.nextToken();
//                if (line.isEmpty()) {
//                    while ((line = tokenizer.nextToken()) != null) {
//                        sb.append(line);
//                    }
//                }
//            }
//            System.out.println(sb);
//            sb.delete(sb.length() - 1, sb.length() - 1);
//            writer.write(sb.toString());
//            writer.flush();
            //            while ((reader = input.read(bytes)) != -1) {
//                os.write(bytes, 0, reader);
//            }


        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String readFile(InputStream input) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    private void httpGet(HttpExchange exchange) throws IOException {
        OutputStream respBody = exchange.getResponseBody();
        String tempPath = exchange.getRequestURI().getPath();
        String filePath = DEFAULT_PATH;
        if (!tempPath.equals(FAVI)) {
            filePath += tempPath;
        }

        try {
            // Write Response Body
            file = new File(filePath);

            files = file.listFiles();
            StringBuilder fileLine = new StringBuilder();
            if (files == null) {
                FileReader fileReader = new FileReader(file);
                BufferedReader reader = new BufferedReader(fileReader);
                String line;

                while ((line = reader.readLine()) != null) {
                    fileLine.append(line + "</br>");
                }

            } else {
                for (int i = 0; i < files.length; i++) {
                    fileLine.append(files[i].getName()).append("</br>");
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>");
            sb.append("<html>");
            sb.append("   <head>");
            sb.append("       <meta charset=\"UTF-8\">");
            //            sb.append("       <meta name=\"author\" content=\"Dochi\">");
            sb.append("       <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            sb.append("       <title>SimpleHttpd</title>");
            sb.append("   </head>");
            sb.append("   <body>");
            //            sb.append("       <h5>Hello, HttpServer!!!</h5>");
            sb.append("       <h1>200 OK </h1>");
            sb.append("       <span>Method: " + (exchange.getRequestMethod()) + "</span></br>");
            sb.append("       <span>filedirectory " + fileLine + "</span></br>");
            sb.append("   </body>");
            sb.append("</html>");

            // Encoding to UTF-8
            ByteBuffer bb = Charset.forName("UTF-8").encode(sb.toString());
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

        } catch (FileNotFoundException e) {

            printErr(exchange, respBody);
        } catch (IOException e) {
            e.printStackTrace();

            if (respBody != null) {
                respBody.close();
            }
        } finally {
            exchange.close();
        }
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

