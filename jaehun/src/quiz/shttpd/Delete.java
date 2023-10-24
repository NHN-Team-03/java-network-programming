package quiz.shttpd;

import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.net.HttpURLConnection;
import java.nio.file.Path;

public class Delete {
    private HttpExchange exchange;

    public Delete(HttpExchange exchange) {
        this.exchange = exchange;


    }

    public String handler() {
        StringBuilder sb = new StringBuilder();
        String fileName = exchange.getRequestURI().getPath().substring(1);

        if (FileList.fileSet.contains(fileName)) {
            File file = new File(FileList.ROOT_PATH + "/" + fileName);

            if (file.delete()) {
                Handler.status = HttpURLConnection.HTTP_NO_CONTENT;
                FileList.fileSet.remove(fileName);
            } else {
                Handler.status = HttpURLConnection.HTTP_FORBIDDEN;

                sb.append("       <h2>HTTP FORBIDDEN </h2>");
                sb.append("       <p> " + fileName + "을 삭제할 수 없습니다. </p>");
            }

        } else {
            Handler.status = HttpURLConnection.HTTP_NO_CONTENT;
        }

        return sb.toString();
    }
}