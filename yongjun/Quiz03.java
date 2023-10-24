package com.nhnacademy.yongjun;

import java.net.Socket;

public class Quiz03 {
    public static void main(String[] args) {

        Socket socket = new Socket();

        System.out.println(socket.getPort());
        System.out.println(socket);

    }
}
