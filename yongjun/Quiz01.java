package com.nhnacademy.yongjun;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Quiz01 {

    public static void main(String[] args) {
        int startAd = 0;
        int endAd = 60000;

        for (int i = startAd; i < endAd; i++) {


            try (Socket socket = new Socket("localhost", i)) {
                System.out.println(i + "port 열림");
            }
             catch (IOException ignored) {

            }
        }
    }
}
