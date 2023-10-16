package com.nhnacademy.quiz;

import java.io.IOException;
import java.net.Socket;

public class Quiz1 {
    public static void main(String[] args) {
        int startPort = 0;
        int endPort = 65535;

        if (args.length < 2) {
            System.out.println("인자 2개 입력해주세요");
            return;
        }

        try {
            startPort = Integer.parseInt(args[0]);
            endPort = Integer.parseInt(args[1]);
        } catch(NumberFormatException exception) {
            System.out.println("숫자를 입력해주세요.");
            return;
        }

        if (startPort > endPort) {
            int temp = startPort;
            startPort = endPort;
            endPort = temp;
        }

        for (int i = startPort; i <= endPort; i++) {
            try {
                Socket socket = new java.net.Socket("localhost", i);
                System.out.println(i + "번 포트 열려있음");
                socket.close();
            } catch(IOException exception) {
                ;
            }
        }
    }
}
