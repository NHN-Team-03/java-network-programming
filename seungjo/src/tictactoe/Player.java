package tictactoe;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Player {

    String id;

    Socket socket;
    Thread sendThread;
    Thread receiveThread;

    BufferedOutputStream socketOut;

    public Player(String id, Socket socket) throws IOException {
        this.id = id;
        this.socket = socket;
        this.socketOut = new BufferedOutputStream(socket.getOutputStream());

        sendId();

        this.sendThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.matches("[1-9]")) {
                        writer.write(line);
                        writer.newLine();
                        writer.flush();
                    } else {
                        System.out.println("1 ~ 9 사이의 숫자를 입력해주세요.");
                    }
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
            throw new RuntimeException(e);
        }
    }


    public void start() {
        this.sendThread.start();
        this.receiveThread.start();
    }

    public static void main(String[] args) {
        String id = "user";
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
                System.out.println("Port : 1 ~ 65535 사이의 숫자를 입력해주세요.");
                System.exit(1);
            }
        }

        try {
            Socket socket = new Socket(host, port);
            Player player = new Player(id, socket);
            player.start();
        }catch (IOException e) {
            System.out.println("서버에 연결할 수 없습니다.");
        }
    }
}
