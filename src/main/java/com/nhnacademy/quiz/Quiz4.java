package com.nhnacademy.quiz;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Quiz4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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
            System.out.println("서버 연결 성공");

            OutputStream outputStream = socket.getOutputStream();

            while(true) {
                System.out.print("입력(종료 = exit) : ");
                String line = scanner.nextLine();
                if (line.equals("exit")) {
                    break;
                }
                outputStream.write(line.getBytes());
                outputStream.write("\n".getBytes());
                outputStream.flush();
            }

            socket.close();
        } catch (IOException e) {
            System.err.println(host + ":" + port + "에 연결할 수 없습니다.");
        }
    }
}
