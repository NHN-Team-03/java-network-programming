package quiz.shttpd;

import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.net.HttpURLConnection;

public class Delete {
    private final HttpExchange exchange;
    private int status;
    private final StringBuilder stringBuilder;

    public Delete(HttpExchange exchange, StringBuilder stringBuilder) {
        this.exchange = exchange;
        status = HttpURLConnection.HTTP_OK;
        this.stringBuilder = stringBuilder;

        this.handler();
    }

    public int getStatus() {
        return status;
    }


    public void handler() {

        String fileName = exchange.getRequestURI().getPath().substring(1);

        if (FileList.fileSet.contains(fileName)) {
            File file = new File(FileList.ROOT_PATH + "/" + fileName);

            if (file.delete()) {
                status = HttpURLConnection.HTTP_NO_CONTENT;
                FileList.fileSet.remove(fileName);
            } else {
                status = HttpURLConnection.HTTP_FORBIDDEN;

                stringBuilder.append("       <h2>HTTP FORBIDDEN </h2>");
                stringBuilder.append("       <p> " + fileName + "을 삭제할 수 없습니다. </p>");
            }

        } else {
            status = HttpURLConnection.HTTP_NO_CONTENT;
        }

    }
}