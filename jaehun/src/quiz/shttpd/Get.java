package quiz.shttpd;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class Get {
    private HttpExchange exchange;

    public Get(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public String handler() {
        StringBuilder sb = new StringBuilder();

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            Handler.status = HttpURLConnection.HTTP_OK;

            sb.append("       <h2>File List</h2>");
            for (String file : FileList.fileSet) {
                sb.append("      <p>").append(file).append("</p>");
            }
        } else {
            String fileName = path.substring(1);

            if (FileList.fileSet.contains(fileName)) {
                if (!Files.isReadable(Path.of(fileName))) {
                    Handler.status = HttpURLConnection.HTTP_FORBIDDEN;

                    sb.append("       <h2> HTTP FORBIDDEN </h2>");
                    sb.append("       <p> 파일을 읽을 수 없습니다. </p>");

                } else {
                    Handler.status = HttpURLConnection.HTTP_OK;
                    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                        sb.append("       <h2>").append(fileName).append("</h2>");
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append("      <p>").append(line).append("</p>");
                        }
                    } catch (IOException ignore) {
                    }

                }


            } else {
                Handler.status = HttpURLConnection.HTTP_NOT_FOUND;
                sb.append("       <h2> HTTP NOT FOUND </h2>");
                sb.append("      <p> 파일을 찾지 못했습니다. </p>");
            }
        }

        return sb.toString();
    }
}


