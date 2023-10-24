package com.nhnacadmemy.quiz.quiz12;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Server extends Thread {

    static List<ChatHandler> handlerList = new LinkedList<>();
    int port;

    public Server(int port) {
        this.port = port;
    }

    static class ChatHandler extends Thread {
        Socket socket;

        BufferedOutputStream socketOut;

        ChatHandler(Socket socket) throws IOException {
            super("user" + handlerList.size());
            handlerList.add(this);
            this.socket = socket;
            this.socketOut = new BufferedOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            System.out.println("Client connected: " + this.getName()
                    + " [" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() +"]");
            while (!Thread.currentThread().isInterrupted()) {
                try (BufferedInputStream socketIn = new BufferedInputStream(socket.getInputStream())) {
                    byte[] buffer = new byte[2048];
                    int length;

                    while ((length = socketIn.read(buffer)) > 0) {

                        for (ChatHandler chatHandler : handlerList) {
                            chatHandler.socketOut.write(buffer, 0, length);
                            chatHandler.socketOut.flush();
                        }
                    }
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                ChatHandler handler = new ChatHandler(socket);
                handler.start();
            }
        } catch (IOException ignore) {
        }
    }


    public static void main(String[] args) {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                System.err.println("Port: 1 ~ 65535 사이의 정수여야 합니다.");
                System.exit(1);
            }
        }

        Server server = new Server(port);
        server.start();
    }
}
