package com.nhnacademy.snc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Snc {
    public static void run(Socket socket) {
        try (BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader terminalIn = new BufferedReader(new InputStreamReader(System.in));
             BufferedWriter terminalOut = new BufferedWriter(new OutputStreamWriter(System.out))) {
            ChildThread ch1 = new ChildThread(socketOut, terminalIn);
            ChildThread ch2 = new ChildThread(terminalOut, socketIn);

            ch1.start();
            ch2.start();

            ch1.join();
            ch2.join();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        boolean isServer = false;

        if (args.length != 2) {
            throw new IllegalArgumentException("인자는 2개여야 합니다.\n");
        }

        if (args[0].equals("-l")) {
            isServer = true;
            host = args[0];
        }

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("포트 번호는 정수여야 합니다.");
            return;
        }

        if (isServer) {
            try (ServerSocket server = new ServerSocket(port)) {
                Socket socket = server.accept();
                run(socket);
            } catch (IOException ignore) {
                System.out.println("서버 소켓 여는 중 에러 발생");
            }
        } else {
            try (Socket socket = new Socket(host, port)) {
                run(socket);
            } catch (IOException ignore) {
                System.out.println("클라이언트 소켓 여는 중 에러 발생");
            }
        }
    }
}
