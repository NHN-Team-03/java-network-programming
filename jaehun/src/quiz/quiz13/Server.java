package quiz.quiz13;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    static Map<String, Socket> clientList = new HashMap<>();

    static class Handler extends Thread {
        private Socket socket;
        private String id;
        private BufferedReader reader;
        private BufferedWriter writer;

        public Handler(Socket socket) {
            this.socket = socket;

            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                id = reader.readLine();

            } catch (IOException ignore) {
            }

            System.out.println("Client[" + id + "]가 입장하였습니다.");

            clientList.put(id, this.socket);
        }

        @Override
        public void run() {
            String line;
            while (!this.isInterrupted()) {
                try {
                    while ((line = reader.readLine()) != null) {
                        if (line.charAt(0) == '@') {
                            String[] info = line.substring(1).split(" ", 2);
                            send(info[0], info[1]);
                        } else if (line.charAt(0) == '!') {
                            line = line.substring(1);
                            if (line.equalsIgnoreCase("exit")) {
                                close();
                            } else if (line.equalsIgnoreCase("list")) {
                                printClient();
                            }
                        } else {
                            sendAll(line);
                        }
                    }
                } catch (IOException ignore) {
                }
            }
        }

        private void printClient() {
            try {
                writer.write("=====================\n");
                writer.write("Client 목록\n");
                writer.write("=====================\n");
                for (String client : clientList.keySet()) {
                    writer.write(client.concat("\n"));
                }
                writer.write("=====================\n");

                writer.flush();
            } catch (IOException ignore) {
            }
        }

        private void close() {
            try {
                System.out.println("Client[" + id + "] 접속 종료");
                clientList.remove(id);
                reader.close();
                writer.close();
                socket.close();
            } catch (IOException ignore) {
            }
        }

        private void sendAll(String message) {
            for (String client : clientList.keySet()) {
                send(client, message);
            }
        }

        private void send(String id, String message) {
            Socket receiver = clientList.get(id);

            try {
                receiver.getOutputStream().write((this.id + " : " + message + "\n").getBytes());
            } catch (IOException ignore) {
            }
        }
    }

    public static void main(String[] args) {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Server: Port번호가 잘못되었습니다.");
                System.exit(1);
            }
        }

        try (ServerSocket server = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = server.accept();

                Handler handler = new Handler(socket);

                handler.start();
            }
        } catch (IOException e) {
            System.err.println(port + "에 연결할 수 없습니다.");
        }
    }
}
