package com.nhnacademy.exam;

import java.io.IOException;
import java.net.Socket;

public class Exam3 {
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
                System.err.println("port 번호는 숫자로 입력해주세요.");
            }
        }

        try {
            Socket socket = new Socket(host, port);
            int ch;
            while ((ch = socket.getInputStream().read()) >= 10) {
                System.out.println((char) ch);
            }
            socket.close();
        } catch (IOException e) {
            System.err.println("연결 실패");
        }
    }
}
