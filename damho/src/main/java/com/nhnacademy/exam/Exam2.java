package com.nhnacademy.exam;

import java.io.IOException;
import java.net.Socket;

public class Exam2 {
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
                System.err.println("port 번호에는 숫자를 입력해주세요.");
            }
        }

        try {
            Socket socket = new Socket(host, port);

            socket.getOutputStream().write("Hello World!!".getBytes());

            socket.close();
        } catch (IOException e) {
            System.err.println(host + ":" + port + "에 연결할 수 없습니다.");
        }
    }
}
