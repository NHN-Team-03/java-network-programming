package com.nhnacadmemy.shttpd;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Shttpd {

    private HttpServer server = null;

    public static void usage() {
        System.out.println("Usage: shttpd port (1 ~ 65535)");
    }

    public Shttpd(String host, int port) {
        createServer(host, port);
    }

    /**
     * 서버를 생성한다.
     */
    private void createServer(String host, int port) {
        try {
            // HTTP Server 생성
            this.server = HttpServer.create(new InetSocketAddress(host, port), 0);

            // HTTP Server Context 설정 -> Handler 지정
            server.createContext("/", new RootHandler());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 서버를 실행한다.
     */
    public void start() {
        server.start();
    }

    /**
     * 서버를 중지한다.
     *
     * @param delay 지연시간
     */
    public void stop(int delay) {
        server.stop(delay);
    }

    public static void main(String[] args) {

        // 80포트는 권한이 없어서 사용하지 못함.
        int port = 3000;

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                usage();
                System.exit(1);
            }
        }

        Shttpd shttpd = null;

        try {
            // 시작한다고 서버에게 알려줌.
            System.out.println(String.format("[%s][HTTP SERVER][START]",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));

            // 서버 생성
            shttpd = new Shttpd("localhost", port);
            shttpd.start();

            // Shutdown hook 설정
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    // 종료한다고 서버에게 알려줌.
                    System.out.println(String.format("[%s][HTTP SERVER][STOP]",
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
                }
            }));

            // Enter를 입력하면 서버 종료

            System.out.println("Please press 'Enter' to stop the server.");
            System.in.read();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            shttpd.stop(0);
        }
    }
}
