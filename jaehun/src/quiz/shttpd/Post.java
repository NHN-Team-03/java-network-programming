package quiz.shttpd;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

public class Post {
    private HttpExchange exchange;

    public Post(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public String handler(){
        StringBuilder sb = new StringBuilder();

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


        if (postType) {
            InputStream requestBody = exchange.getRequestBody();

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(requestBody));
                br.readLine();
                String filename = br.readLine().split("filename=")[1].replace("\"", "");

                if (FileList.fileSet.contains(filename)) {
                    Handler.status = HttpURLConnection.HTTP_CONFLICT;
                    sb.append("       <h2>HTTP CONFLICT </h2>");
                    sb.append("       <p> 디렉토리에 같은 이름의 파일이 존재합니다. </p>");
                } else {
                    String line;
                    boolean inpart = false;
                    FileWriter writer = new FileWriter(FileList.ROOT_PATH + "/" + filename);

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
            Handler.status = HttpURLConnection.HTTP_BAD_METHOD;
            sb.append("       <h2>HTTP BAD METHOD </h2>");
            sb.append("       <p> 허용되지 않은 Content Type 입니다. </p>");
        }

        return sb.toString();
    }
}


