package com.nhnacademy.exam;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Exam5 {
    public static void main(String[] args) {
        int port = 1234;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket = serverSocket.accept();

            socket.getOutputStream().write("Hello".getBytes());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("연결 실패");
        }
    }
}
