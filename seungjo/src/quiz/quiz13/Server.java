package quiz.quiz13;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Server extends Thread {

    static List<Client> clientList = new LinkedList<>();
    static List<ChatHandler> handlerList = new LinkedList<>();


    // 각각의 ChatHandler는 하나의 Client와 연결되어 있다.
    static class ChatHandler extends Thread {
        private Socket socket;
        BufferedReader reader;

        public ChatHandler(Socket socket) throws IOException {
            handlerList.add(this);
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            System.out.println(
                    "Client connected: " + " [" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() +
                            "]");

            while (!Thread.currentThread().isInterrupted()) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (line.charAt(0) == '@') {
                            String[] str = line.split(" ", 2);
                            String id = str[0].substring(1);
                            String message = str[1];

                            System.out.println(id + " " + message);

                            for (Client client : clientList) {
                                if (client.getName().equals(id)) {
                                    client.write(client.getName() + " : " + message);
                                }
                            }
                        } else {
                            for (Client client : clientList) {
                                client.write(line);
                            }
                        }
                    }
                } catch (IOException ignore) {
                }
            }
        }
    }

    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                ChatHandler handler = new ChatHandler(socket);
                handler.start();
            }
        } catch (IOException ignore) {
        }
    }

    public static void main(String[] args) {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                System.exit(1);
            }
        }

        Server server = new Server(port);
        server.start();
        System.out.println("Server started on port " + port);

    }
}