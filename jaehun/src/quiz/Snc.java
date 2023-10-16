package quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Snc {

    public static void start(Socket socket) {
        Thread sendThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line.concat("\n"));
                        writer.flush();
                    }

                } catch (IOException ignore) {
                }
            }
        });

        Thread receiveThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line.concat("\n"));
                        writer.flush();
                    }
                } catch (IOException ignore) {
                }
            }
        });

        sendThread.start();
        receiveThread.start();

        try {
            sendThread.join();
            receiveThread.join();
        } catch (InterruptedException ignore) {
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;
        boolean mode = true;

        if (args.length < 2) {
            System.err.println("설정이 잘못 되었습니다.");
            System.exit(1);
        }

        if (!args[0].equals("-l")) {
            mode = false;
        }

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Port 번호가 잘못되었습니다.");
        }

        if (mode) {
            try (ServerSocket server = new ServerSocket(port)) {
                Socket socket = server.accept();
                Snc.start(socket);
            } catch (IOException ignore) {
            }
        } else {
            try (Socket socket = new Socket(host, port)) {
                Snc.start(socket);
            } catch (IOException ignore) {
            }
        }

    }
}
