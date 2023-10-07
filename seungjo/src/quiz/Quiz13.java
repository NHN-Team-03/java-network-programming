package quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Quiz13 {

    static List<Client> clientList = new LinkedList<>();

    static class Client extends Thread {

        private Socket socket;
        private BufferedWriter writer;

        public Client(String id, Socket socket) {
            super(id);

            this.socket = socket;
            clientList.add(this);
        }

        public void write(String message) throws IOException {
            writer.write(message);
            writer.flush();
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                this.writer = writer;
                write("당신의 ID: " + this.getName() + "\n");

                while (!Thread.currentThread().isInterrupted()) {
                    String line = reader.readLine() + "\n";

                    if (line.charAt(0) == '@') {
                        String[] str = line.split(" ", 2);
                        String id = str[0].substring(1);
                        String message = str[1];

                        for (Client client : clientList) {
                            if (client.getName().equals(id)) {
                                client.write("@" + this.getName() + " : " + message);
                            }
                        }
                    } else {
                        for (Client client : clientList) {
                            client.write(this.getName() + " : " + line);
                        }
                    }

                }
            } catch (IOException ignore) {
            }

            try {
                socket.close();
                clientList.remove(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                System.out.println("Port 1 ~ 65535 사이 값을 입력하세요");
                System.exit(1);
            }
        }

        int count = 1;
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (!Thread.currentThread().isInterrupted()) {

                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String input = br.readLine();

                if (input.equals("!list")) {
                    for (Client client : clientList) {
                        System.out.print(client.getName() + " ");
                    }
                }
                Socket socket = serverSocket.accept();

                Client client = new Client(("user" + count++), socket);
                System.out.println(client.getName() + "이 들어왔습니다.");
                client.start();
            }
        } catch (IOException ignore) {
        }
    }
}
