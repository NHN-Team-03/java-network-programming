package nhn.academy.shttpd;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import org.json.JSONObject;

public class Handler implements HttpHandler {

    private static String root = "./";

    long start = System.currentTimeMillis();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        byte[] responseBytes = null;
        String args = exchange.getRequestURI().toString().trim();
        String filePath;
        File getFile;
        StringBuilder response = new StringBuilder("<html><body>");
        int responseCode = 200;
        if ("GET".equals(exchange.getRequestMethod())) {
            filePath = args;
            getFile = new File(root + filePath);


            if (getFile.exists()) {
                if (getFile.isFile()) {
                    String content;
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(getFile));

                    while ((content = bufferedReader.readLine()) != null) {
                        response.append(content);
                    }
                    response.append("</li>");
                    response.append("</body></html>");
                } else if (getFile.isDirectory()) {

                    File[] files = getFile.listFiles();

                    for (File file : files) {
                        response.append("<li>").append(file.getName()).append("</li>");
                    }
                    response.append("</body></html>");


                }

                responseBytes = response.toString().getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.getResponseHeaders().set("Content-Length", String.valueOf(responseBytes.length));


            } else {
                response.append("<h1>404 Error<br>NOT FOUND</h1>")
                        .append("</body></html>");

                responseBytes = response.toString().getBytes();
                responseCode = 404;
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(responseBytes.length));
            exchange.sendResponseHeaders(responseCode, responseBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();


        }

        if ("POST".equals(exchange.getRequestMethod())) {

            filePath = args;
            getFile = new File(root + filePath);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

            String keymessage;
            String valuemessage;
            String boundaryStart;
            String boundaryEnd;


            /*
            POST에서 파일 생성이외는 405
               */
//            if () {
//                response.append("<h1>405 Error<br>Method Not Allowed</h1>")
//                        .append("</body></html>");
//                responseBytes = response.toString().getBytes();
//                responseCode = 405;
//
//            }
            if (getFile.createNewFile()) {
                response.append("<h1>SUCCESS CREATE FILE</h1>")
                        .append("</body></html>");
                responseBytes = response.toString().getBytes();

            } else {
                response.append("<h1>409 Error<br>EXIST FILE</h1>")
                        .append("</body></html>");
                responseBytes = response.toString().getBytes();
                responseCode = 409;

            }

            FileWriter writer = new FileWriter(getFile);

            while ((boundaryStart = bufferedReader.readLine()) != null) {
                keymessage = bufferedReader.readLine();
                bufferedReader.readLine();
                valuemessage = bufferedReader.readLine();
                boundaryEnd = bufferedReader.readLine();

                JSONObject jsonObject = new JSONObject(keymessage.split("=")[1], valuemessage);
                writer.write(jsonObject.toString());
                writer.write("{" + keymessage.split("=")[1] + ":\"" + valuemessage + "\"}");
                writer.flush();
            }


            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(responseBytes.length));
            exchange.sendResponseHeaders(responseCode, responseBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }

        if ("DELETE".equals(exchange.getRequestMethod())) {
            filePath = args;
            getFile = new File(root + filePath);

            getFile.delete();

            responseCode = 204;

            exchange.sendResponseHeaders(responseCode, -1);
        }

        long end = System.currentTimeMillis();


        // 시간, 요청 method, 경로, 응답 코드, 응답 크기, 응답에 걸린 시간
        System.out.println("현재 시간 : " + LocalDateTime.now());
        System.out.println("요청 method : " + exchange.getRequestMethod());
        System.out.println("경로 : " + exchange.getProtocol() + exchange.getLocalAddress());
        System.out.println("응답 코드 : " + responseCode);
        System.out.println("응답 크기 : " + responseBytes.length);
        System.out.println("응답에 걸린시간 : " + (end - start) +  "ms");
    }
}
