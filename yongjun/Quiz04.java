package com.nhnacademy.yongjun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Quiz04 {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        try {
            Socket socket = new Socket(host, port);
            System.out.println("서버 연결");
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String line = reader.readLine();
                if (line.equals("exit")){
                    break;
                }
                outputStream.write(line.getBytes());
                outputStream.write("\n".getBytes());
                outputStream.flush();
            }
            socket.close();

        } catch (IOException e) {
            System.err.println(e);
        }

    }
}
