package quiz.quiz13;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedOutputStream socketOut;
    private String id;

    private Thread sendThread;
    private Thread receiveThread;

    public Client(Socket socket, String id) throws IOException {
        this.socket = socket;
        this.id = id;
        socketOut = new BufferedOutputStream(socket.getOutputStream());

        sendId();

        this.sendThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("!exit")) {
                        //TODO: 서버에서 이 클라이언트 지워야 됨
                        Server.close(id);
                        System.exit(0);
                    }
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }
            } catch (IOException ignore) {
            }
        });

        this.receiveThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException ignore) {
            }
        });


    }

    private void sendId() {
        try {
            socketOut.write((this.id).concat("\n").getBytes());
            socketOut.flush();
        } catch (IOException e) {
        }
    }

    public void start() {
        sendThread.start();
        receiveThread.start();
    }


    public static void main(String[] args) {
        String id = "Team";
        String host = "localhost";
        int port = 1234;

        if (args.length > 0) {
            id = args[0];
        }

        if (args.length > 1) {
            host = args[1];
        }

        try {
            if (args.length > 2) {
                port = Integer.parseInt(args[2]);
            }
        } catch (NumberFormatException e) {
            System.err.println("src.Client: Port 번호 잘못 됨");
        }

        try {
            Socket socket = new Socket(host, port);
            Client client = new Client(socket, id);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}