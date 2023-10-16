package quiz.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Player {
    private Socket socket;
    private Thread sendThread;
    private Thread receiveThread;

    public Player(Socket socket) {
        this.socket = socket;

        this.sendThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line.concat("\n"));
                    writer.flush();

                }

            } catch (IOException ignore) {
            }
        });

        this.receiveThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.equals("end")) {
                        sendThread.interrupt();
                        writer.write("게임을 종료합니다.\n");
                        writer.flush();
                        socket.close();
                        receiveThread.interrupt();
                        System.exit(0);
                    }

                    if(line.equals("notify")) {


                        continue;
                    }
                    writer.write(line.concat("\n"));
                    writer.flush();

                }

            } catch (IOException ignore) {
            }
        });
    }

    public void start() {
        sendThread.start();
        receiveThread.start();
    }

    public void join() {
        try {
            sendThread.join();
            receiveThread.join();
        } catch (InterruptedException ignore) {
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        try (Socket socket = new Socket(host, port)){
            Player player = new Player(socket);

            player.start();

            player.join();
        } catch (IOException ignore) {
        }
    }
}
