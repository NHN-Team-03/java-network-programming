package quiz.shttpd;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Handler implements HttpHandler {

    public static int status = HttpURLConnection.HTTP_OK;

    public Handler() {
        FileList.addFile();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HtmlMaker maker = new HtmlMaker();
        FileList.addFile();

        OutputStream responseBody = exchange.getResponseBody();

        String method = exchange.getRequestMethod();
        String bodyMessage = "";
        switch (method) {
            case "GET":
                bodyMessage = new Get(exchange).handler();
                break;
            case "POST":
                bodyMessage = new Post(exchange).handler();
                break;
            case "DELETE":
                bodyMessage = new Delete(exchange).handler();
                break;
            default:
                status = HttpURLConnection.HTTP_BAD_METHOD;
        }

        maker.writeBody(bodyMessage);

        StringBuilder sb = maker.getHtml();


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
}
