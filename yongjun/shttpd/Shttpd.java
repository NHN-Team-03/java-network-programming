package com.nhnacademy.yongjun.shttpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Shttpd {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8000;
    private final int BACKLOG = 0;
    private HttpServer server = null;

    public Shttpd(String host, int port) throws IOException {
        creatServer(host, port);
    }

    private void creatServer(String host, int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), BACKLOG);
        server.createContext("/", new RootHandler());
    }

    private void start() {
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 종료 로그
            System.out.println(
                    String.format(
                            "[%s][HTTP SERVER][STOP]",
                            new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date())
                    )
            );
        }));

        // Enter를 입력하면 종료
        System.out.print("Please press 'Enter' to stop the server.");
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            server.stop(0);
        }
    }

    public static void main(String[] args) {
        try {
            Shttpd shttpd = new Shttpd(HOSTNAME, PORT);
            shttpd.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
