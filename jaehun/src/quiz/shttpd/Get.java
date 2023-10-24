package quiz.shttpd;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class Get {
    private final HttpExchange exchange;
    private final StringBuilder stringBuilder;
    private int status;

    public Get(HttpExchange exchange, StringBuilder stringBuilder) {
        this.exchange = exchange;
        this.stringBuilder = stringBuilder;
        status = HttpURLConnection.HTTP_OK;

        this.handler();
    }

    public int getStatus() {
        return status;
    }

    public void handler() {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            stringBuilder.append("       <h2>File List</h2>");
            for (String file : FileList.fileSet) {
                stringBuilder.append("      <p>").append(file).append("</p>");
            }
        } else {
            String fileName = path.substring(1);

            if (FileList.fileSet.contains(fileName)) {
                if (!Files.isReadable(Path.of(fileName))) {
                    status = HttpURLConnection.HTTP_FORBIDDEN;

                    stringBuilder.append("       <h2> HTTP FORBIDDEN </h2>");
                    stringBuilder.append("       <p> 파일을 읽을 수 없습니다. </p>");

                } else {
                    status = HttpURLConnection.HTTP_OK;
                    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                        stringBuilder.append("       <h2>").append(fileName).append("</h2>");
                        String line;
                        while ((line = br.readLine()) != null) {
                            stringBuilder.append("      <p>").append(line).append("</p>");
                        }
                    } catch (IOException ignore) {
                    }

                }
            } else {
                status = HttpURLConnection.HTTP_NOT_FOUND;
                stringBuilder.append("       <h2> HTTP NOT FOUND </h2>");
                stringBuilder.append("      <p> 파일을 찾지 못했습니다. </p>");
            }
        }

    }
}


