package quiz.shttpd;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Handler implements HttpHandler {
    private static final String ROOT_PATH = "/Users/jangjaehun/Team-03/java-network-programming";
    private int status;
    private String path;
    private Set<String> fileSet;
    private HttpExchange exchange;
    private StringBuilder sb;

    public Handler() {
        this.status = HttpURLConnection.HTTP_OK;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.exchange = exchange;
        path = exchange.getRequestURI().getPath();

        OutputStream responseBody = exchange.getResponseBody();
        fileSet = new HashSet<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(ROOT_PATH))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileSet.add(path.getFileName().toString());
                }
            }
        }

        sb = new StringBuilder();

        sb.append("<!DOCTYPE html>");
        sb.append("<html>");

        createHtmlHead(sb);

        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getHandler();
                break;
            case "POST":
                postHandler();
                break;
            case "DELETE":
                deleteHandler();
                break;
            default:
                setStatus(HttpURLConnection.HTTP_BAD_METHOD);
        }

        sb.append("</html>");

        ByteBuffer bb = StandardCharsets.UTF_8.encode(sb.toString());
        int contentLength = bb.limit();
        byte[] content = new byte[contentLength];
        bb.get(content, 0, contentLength);

        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "text/html;charset=UTF-8");
        headers.add("Content-Length", String.valueOf(contentLength));

        if (status == HttpURLConnection.HTTP_NO_CONTENT) {
            contentLength = -1;
        }

        exchange.sendResponseHeaders(status, contentLength);

        responseBody.write(content);

        responseBody.close();

        exchange.close();
    }

    private void deleteHandler() {
        String fileName = path.substring(1);

        sb.append("   <body>");

        if (fileSet.contains(fileName)) {
            File file = new File(ROOT_PATH + "/" + fileName);

            if (file.delete()) {
                setStatus(HttpURLConnection.HTTP_NO_CONTENT);
            } else {
                setStatus(HttpURLConnection.HTTP_FORBIDDEN);
                sb.append("       <h2>HTTP FORBIDDEN </h2>");
                sb.append("       <p> " + fileName + "을 삭제할 수 없습니다. </p>");
            }

        } else {
            setStatus(HttpURLConnection.HTTP_NO_CONTENT);
        }

        sb.append("   </body>");
    }

    private void postHandler() {
        List<String> types = exchange.getRequestHeaders().get("Content-Type");

        boolean postType = false;

        String boundary = "";

        for (String type : types) {
            if (type.contains("form-data")) {
                postType = true;
            }

            if (type.contains("boundary")) {
                boundary = type.split("boundary=")[1];
            }
        }

        sb.append("   <body>");

        if (postType) {
            InputStream requestBody = exchange.getRequestBody();

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(requestBody));
                br.readLine();
                String filename = br.readLine().split("filename=")[1].replace("\"", "");

                if (fileSet.contains(filename)) {
                    setStatus(HttpURLConnection.HTTP_CONFLICT);
                    sb.append("       <h2>HTTP CONFLICT </h2>");
                    sb.append("       <p> 디렉토리에 같은 이름의 파일이 존재합니다. </p>");
                } else {
                    String line;
                    boolean inpart = false;
                    FileWriter writer = new FileWriter(ROOT_PATH + "/" + filename);

                    while ((line = br.readLine()) != null) {
                        if (line.isEmpty()) {
                            if (!inpart) {
                                inpart = !inpart;
                                continue;
                            }
                        } else if (line.contains(boundary)) {
                            break;
                        }

                        if (inpart) {
                            writer.append(line);
                            writer.append("\n");
                        }

                    }
                    writer.flush();

                    writer.close();
                    sb.append("       <h2> " + filename + " 저장 </h2>");
                }


            } catch (IOException ignore) {
            }

        } else {
            setStatus(HttpURLConnection.HTTP_BAD_METHOD);
            sb.append("       <h2>HTTP BAD METHOD </h2>");
            sb.append("       <p> 허용되지 않은 Content Type 입니다. </p>");
        }

        sb.append("   </body>");

    }

    private void getHandler() {
        sb.append("   <body>");

        if (path.equals("/")) {

            sb.append("       <h2>File List</h2>");
            for (String file : fileSet) {
                sb.append("      <p>").append(file).append("</p>");
            }


        } else {
            String fileName = path.substring(1);

            if (fileSet.contains(fileName)) {
                if (!Files.isReadable(Path.of(fileName))) {
                    setStatus(HttpURLConnection.HTTP_FORBIDDEN);

                    sb.append("       <h2>HTTP FORBIDDEN</h2>");
                    sb.append("       <p> 파일을 읽을 수 없습니다. </p>");

                } else {
                    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                        sb.append("       <h2>" + fileName + "</h2>");
                        String line;

                        while ((line = br.readLine()) != null) {
                            sb.append("      <p>").append(line).append("</p>");
                        }
                    } catch (IOException ignore) {
                    }

                }


            } else {
                setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                sb.append("       <h2> HTTP NOT FOUND </h2>");
                sb.append("      <p> 파일을 찾지 못했습니다. </p>");
            }
        }

        sb.append("   </body>");
    }

    private void createHtmlHead(StringBuilder sb) {
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"Jaehun\">");
        sb.append("       <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        sb.append("       <title>Simple HTTP Directory</title>");
        sb.append("   </head>");
    }
}


