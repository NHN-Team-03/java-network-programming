package com.nhnacademy;

public class Main {
    public static void main(String[] args) {
        int count = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.println("i값 : " + i + " j값 : " + j + " = " + count++);
            }
            System.out.println();
        }
    }
}