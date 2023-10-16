package com.nhnacademy.quiz;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Quiz8 {
    public static void main(String[] args) {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                System.err.println("Port는 0~65535 사이의 정수만 가능");
                System.exit(1);
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket socket = serverSocket.accept()) {
            System.out.println(socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "가 연결되었습니다.");
            System.out.println("연결을 끊습니다.");
        } catch (IOException e) {
            System.err.println("서비스 열기에 실패 했습니다.");
        }
    }
}
