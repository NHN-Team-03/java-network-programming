package com.nhnacademy.snc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ChildThread extends Thread {
    BufferedWriter writer;
    BufferedReader reader;

    public ChildThread(BufferedWriter writer, BufferedReader reader) {
        this.writer = writer;
        this.reader = reader;
    }

    @Override
    public void run() {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                writer.write(line.concat("\n"));
                writer.flush();
            }
        } catch (IOException ignore) {

        }
    }
}
