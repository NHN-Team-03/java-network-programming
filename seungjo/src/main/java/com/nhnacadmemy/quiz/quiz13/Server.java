package com.nhnacadmemy.quiz.quiz13;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    static Map<String, Socket> chatter = new HashMap<>();

    static class ChatHandler extends Thread {

        String id;
        Socket socket;
        BufferedReader reader;
        BufferedWriter writer;

        public ChatHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }

        public void send(String line) {
            String[] info = line.substring(1).split(" ", 2);
            String name = info[0];
            String message = info[1];

            try {
                Socket findSocket = chatter.get(name);
                OutputStream outputStream = findSocket.getOutputStream();
                outputStream.write(("-> " + this.id + " : " + message).getBytes());
                outputStream.flush();

            } catch (IOException ignore) {
            }
        }

        public void sendAll(String line) {
            try {
                for (String key : chatter.keySet()) {
                    OutputStream outputStream = chatter.get(key).getOutputStream();
                    outputStream.write((this.id + " : " + line).getBytes());
                    outputStream.flush();
                }
            } catch (IOException ignore) {
            }
        }

        private void printClients() throws IOException {
            for (String id : chatter.keySet()) {
                writer.write(id);
                writer.newLine();
            }
            writer.flush();
        }

        @Override
        public void run() {

            try {
                this.id = reader.readLine();
                chatter.put(id, this.socket);
                System.out.println(id + " connected");
            } catch (IOException ignore) {
            }


            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        line = line.concat("\n");
                        if (line.startsWith("!")) {
                            printClients();
                        } else if (line.startsWith("@")) {
                            send(line);
                        } else {
                            sendAll(line);
                        }
                    }


                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void close(String id) {
        try {
            Socket remove = chatter.remove(id);
            remove.close();
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void main(String[] args) {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                System.out.println("Port: 1 ~ 65535");
                System.exit(1);
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                ChatHandler handler = new ChatHandler(socket);

                handler.start();
            }
        } catch (IOException ignore) {
        }
    }
}

