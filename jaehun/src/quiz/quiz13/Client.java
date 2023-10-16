package quiz.quiz13;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
    private String id;

    private Socket socket;
    private Thread sendThread;
    private Thread receivethread;


    public Client(Socket socket, String id) {
        this.id = id;
        this.socket = socket;
        sendId();

        sendThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    writer.write(line.concat("\n"));
                    writer.flush();

                    if (line.equalsIgnoreCase("!exit")) {
                        receivethread.interrupt();
                        break;
                    }
                }

            } catch (IOException ignore) {
            }
        });

        receivethread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    writer.write(line.concat("\n"));
                    writer.flush();
                }

            } catch (IOException ignore) {
            }
        });
    }

    private void sendId() {
        try {
            socket.getOutputStream().write(id.concat("\n").getBytes());
            socket.getOutputStream().flush();
        } catch (IOException ignore) {
        }
    }

    public void start() {
        sendThread.start();
        receivethread.start();
    }

    public static void main(String[] args) {
        String id = "User";
        String host = "localhost";
        int port = 1234;

        if (args.length > 0) {
            id = args[0];
        }

        if (args.length > 1) {
            host = args[1];
        }

        if (args.length > 2) {
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Client: Port 번호가 잘못되었습니다.");
                System.exit(1);
            }
        }

        try {
            Socket socket = new Socket(host, port);

            Client client = new Client(socket, id);

            client.start();
        } catch (IOException e) {
            System.err.println(host + ":" + port + "에 접속할 수 없습니다.");
        }
    }
}
