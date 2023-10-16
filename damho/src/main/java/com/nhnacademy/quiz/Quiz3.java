package com.nhnacademy.quiz;

import java.io.IOException;
import java.net.Socket;

public class Quiz3 {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        if (args.length > 0) {
            host = args[0];
        }

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException exception) {
                System.err.println("포트번호에는 숫자를 입력해주세요.");
            }
        }

        Socket socket;

        try {
            socket = new Socket(host, port);
            System.out.println("Local address : " + socket.getLocalAddress().getHostAddress());
            System.out.println("Local port : " + socket.getLocalPort());
            System.out.println("Remote address : " + socket.getInetAddress().getHostAddress());
            System.out.println("Remote port : " + socket.getPort());
            socket.close();
        } catch (IOException e) {
            System.err.println(host + ":" + port + "에 연결할 수 없습니다.");
        }
    }
}
