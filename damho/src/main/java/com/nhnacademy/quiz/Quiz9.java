package com.nhnacademy.quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Quiz9 {
    public static void main(String[] args) {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                System.err.println("port 번호는 정수를 사용해주세요.");
                System.exit(1);
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket socket = serverSocket.accept()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line.concat("\n"));
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("연결 실패");
            e.printStackTrace();
        }
    }
}
