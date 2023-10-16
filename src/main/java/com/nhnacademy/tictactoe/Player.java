package com.nhnacademy.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Player {
    public static void main(String[] args) throws IOException {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                System.out.println("포트번호는 정수만 입력해주세요.");
            }
        }

        Socket socket;
        socket = new Socket("localhost", port);
        Threads threads = new Threads(socket);
        threads.start();
    }

    static class Threads {
        Socket socket;
        BufferedReader readerFromUser;
        BufferedReader readerFromServer;
        BufferedWriter writer;

        public void wakeUp() {
            synchronized (output) {
                output.notifyAll();
            }
        }

        Thread input = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    readFromServer();
                } catch (IOException ignore) {
                }
            }
        });

        Thread output = new Thread() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException ignore) {
                    }

                    initBufferFromUser();

                    try {
                        writer.write(readerFromUser.readLine().concat("\n"));
                        writer.flush();
                    } catch (IOException ignore) {
                    }
                }
            }
        };

        public Threads(Socket socket) throws IOException {
            this.socket = socket;
            this.readerFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.readerFromUser = new BufferedReader(new InputStreamReader(System.in));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }

        private void start() {
            this.input.start();
            this.output.start();
        }

        private void readFromServer() throws IOException {
            String line;

            while ((line = readerFromServer.readLine()) != null) {
                if (line.equals("exit")) {
                    close();
                    System.exit(0);
                }

                if (line.equals("notify")) {
                    wakeUp();
                    line = "";
                }
                System.out.println(line);
            }

        }

        private void close() {
            output.interrupt();
            input.interrupt();
        }

        private void initBufferFromUser() {
            try {
                while (readerFromUser.ready()) {
                    readerFromUser.readLine();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
