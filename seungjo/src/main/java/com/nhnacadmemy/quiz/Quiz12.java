package com.nhnacadmemy.quiz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Quiz12 {

    static List<EchoServer> serverList = new LinkedList<>();

    static class EchoServer extends Thread {
        Socket socket;

        BufferedOutputStream socketOut;

        EchoServer(Socket socket) throws IOException {
            this.socket = socket;
            socketOut = new BufferedOutputStream(socket.getOutputStream());
            serverList.add(this);
        }

        public Socket getSocket() {
            return this.socket;
        }

        @Override
        public void run() {
            try (BufferedInputStream socketIn = new BufferedInputStream(socket.getInputStream())) {

                byte[] buffer = new byte[2048];
                int length;

                while ((length = socketIn.read(buffer)) > 0) {

                    for (EchoServer server : serverList) {
                        server.getSocket().getOutputStream().write(buffer, 0, length);
                        server.getSocket().getOutputStream().flush();
                    }
//                    socketOut.write(buffer, 0, length);
//                    socketOut.flush();
                }

            } catch (IOException ignore) {
            }

            try {
                socket.close();
                serverList.remove(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        int port = 1234;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();

                EchoServer server = new EchoServer(socket);
                server.start();
            }
        } catch (IOException ignore) {
        }
    }

}
