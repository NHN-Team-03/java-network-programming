package quiz.quiz13;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Client extends Thread {

    static List<Client> clientList = new LinkedList<>();
    private Socket socket;
    private BufferedWriter writer;

    public Client(String id, Socket socket) throws IOException {
        super(id);
        this.socket = socket;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        clientList.add(this);
        for (Client client : clientList) {
            System.out.println(client.getName());
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equals("!exit")) {
                    break;
                }
                write(message);
            }
        } catch (IOException ignore) {
        }

        try {
            socket.close();
            Server.clientList.remove(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    public static void main(String[] args) {
        String id = "user" + clientList.size();
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
            } catch (NumberFormatException ignore) {
                System.out.println("Port: 1 ~ 65535 Integer");
                System.exit(1);
            }
        }

        try (Socket socket = new Socket(host, port)) {

            System.out.println("Conneted to server: " + host + ":" + port);
            Client client = new Client(id, socket);

            client.start();
        } catch (IOException ignore) {
        }
    }
}