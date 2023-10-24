package nhn.academy.shttpd;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class shttpd {
    public static void main(String[] args) {
        int port = 8080;
        HttpServer httpServer = null;

        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e){
            System.out.println(e.toString());
        }

        try {
            httpServer = HttpServer.create(new InetSocketAddress(port),0);
            httpServer.createContext("/", new Handler());

            httpServer.setExecutor(Executors.newCachedThreadPool());
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


