package com.nhnacademy.quiz.quiz13;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    ServerSocket serverSocket;
    int port;
    Socket child;
    List<Socket> socketList;

    public Server() {
        this(1234);
    }

    public Server(int port) {
        this.port = port;
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

    static class ChildThread extends Thread {
        Socket socket;

        public ChildThread(Socket socket) {
            this.socket = socket;
        }
    }
}
