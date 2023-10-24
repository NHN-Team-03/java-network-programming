package com.nhnacademy.yongjun.snc;

import com.nhnacademy.example.Transfer;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SncServer extends Thread {
    static Socket socket;
    static BufferedReader tuminalReader;
    BufferedReader serverReader;
    static BufferedWriter serverWriter;

    @Override
    public void run(){
        System.out.println("server run");
        while (!Thread.currentThread().isInterrupted()) {

            try {
                serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String line;
                while ((line = serverReader.readLine()) != null) {
                    System.out.println(line);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void main(String[] args) {
        int port = 12345;

        try {
            port = Integer.parseInt(args[1]);
        }catch (NumberFormatException e){
            System.out.println("잘못된 입력");
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)){
            socket = serverSocket.accept();
            System.out.println("socket 연결");
            tuminalReader = new BufferedReader(new InputStreamReader(System.in));

            serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            SncServer thread = new SncServer();
            thread.start();
            String line;
            while ((line = tuminalReader.readLine()) != null){
                serverWriter.write(line+"\n");
                serverWriter.flush();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
