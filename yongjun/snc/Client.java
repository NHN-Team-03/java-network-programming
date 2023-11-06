package com.nhnacademy.yongjun.snc;

import java.io.*;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends Thread{

    static Socket socket;
    static BufferedReader tuminalReader;
    BufferedReader serverReader;
    static BufferedWriter serverWriter;



    @Override
    public void run(){
        System.out.println("client run");
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
        String host = "localhost";

        try {
            port = Integer.parseInt(args[1]);
        }catch (NumberFormatException e){
            System.out.println("잘못된 입력");
            System.exit(1);
        }

        try {
            socket = new Socket(host, port);
            System.out.println("클라이언트 연결");
        } catch (IOException e) {
            System.out.println("존재하지 않는 포트");
            throw new RuntimeException(e);
        }



        try {
            tuminalReader = new BufferedReader(new InputStreamReader(System.in));
            serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Client thread = new Client();
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
