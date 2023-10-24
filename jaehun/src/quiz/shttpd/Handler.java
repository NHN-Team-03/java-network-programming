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

    public int status;
    private HtmlMaker maker;
    private StringBuilder stringBuilder;


    public Handler() {
         status = HttpURLConnection.HTTP_OK;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        maker = new HtmlMaker();
        FileList.addFile();
        stringBuilder = new StringBuilder();

        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                Get getHandler = new Get(exchange, stringBuilder);
                status = getHandler.getStatus();
                break;
            case "POST":
                Post postHandler = new Post(exchange, stringBuilder);
                status = postHandler.getStatus();
                break;
            case "DELETE":
                Delete deleteHandler = new Delete(exchange, stringBuilder);
                status = deleteHandler.getStatus();
                break;
            default:
                status = HttpURLConnection.HTTP_BAD_METHOD;
        }

        response(exchange);
    }

    private void response(HttpExchange exchange) throws IOException{
        OutputStream responseBody = exchange.getResponseBody();

        maker.writeBody(stringBuilder.toString());

        ByteBuffer bb = StandardCharsets.UTF_8.encode(stringBuilder.toString());
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
