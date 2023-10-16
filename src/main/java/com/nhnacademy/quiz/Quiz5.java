package com.nhnacademy.quiz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Quiz5 {
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

            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream outputStream = new BufferedOutputStream(System.out);

            byte[] buffer = new byte[2048];

            int readLength;

            while((readLength = inputStream.read(buffer)) > 0) {
                if (new String(buffer, 0, readLength).trim().equals("exit")) {
                    break;
                }

                outputStream.write(buffer, 0, readLength);
                outputStream.flush();
            }

            socket.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
