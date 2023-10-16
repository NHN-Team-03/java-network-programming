package com.nhnacademy.quiz.quiz12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    ServerSocket serverSocket;
    static final int port = 1234;
    Socket child;
    List<Socket> socketList;
    List<String> idList;

    public Server() {
        socketList = new ArrayList<>();

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("서버 소켓 여는 중 에러 발생");
            System.exit(1);
        }

        System.out.println("다중 사용자 접속 echo 서버");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                child = serverSocket.accept();
                ChildThread childThread = new ChildThread(child);
                socketList.add(child);
                childThread.start();
                System.out.println(child.getInetAddress().getHostAddress() + ":" + child.getPort() + "가 연결되었습니다.");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    class ChildThread extends Thread {
        Socket socket;
        BufferedReader reader;
//        BufferedWriter writer;

        public ChildThread(Socket socket) {
            this.socket = socket;
            try {
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                System.err.println("reader, writer 생성 중 에러 발생");
                System.exit(1);
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String line = reader.readLine();

                    if (line == null) {
                        socket.close();
                        break;
                    }

                    if (line.equals("exit")) {
                        socket.close();
                        break;
                    }

                    for (Socket target : socketList) {
                        if (target.equals(this.socket)) {
                            continue;
                        }
                        target.getOutputStream().write((line + "\n").getBytes());
                        target.getOutputStream().flush();
                    }


                } catch (IOException e) {
                    System.out.println(e);
                    System.err.println("클라이언트에서 보낸 데이터 읽는 중 IOExceptioin 발생");
                    System.exit(1);
                }
            }
        }
    }
}