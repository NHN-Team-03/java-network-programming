package com.nhnacademy.yongjun;

import java.io.IOException;
import java.net.Socket;

public class Exam01 {
    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 1234)) {
            System.out.println("서버연결");
            socket.close();
            System.out.println("서버 종료");
        } catch (IOException e) {
            System.err.println("연결 실패");
            throw new RuntimeException(e);
        }


    }
}
