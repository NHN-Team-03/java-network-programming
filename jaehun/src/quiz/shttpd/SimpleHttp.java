package quiz.shttpd;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttp {
    private HttpServer server = null;
    private String host;
    private int port;

    public SimpleHttp(int port) throws IOException {
        this.host = "localhost";
        this.port = port;
        createServer(host, this.port);
    }

    public void createServer(String host, int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(host, port), 0);

        this.server.createContext("/", new Handler());
    }

    public void start() {
        server.start();
    }

    public void stop(int delay) {
        server.stop(delay);
    }
}
