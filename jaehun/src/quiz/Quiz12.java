package quiz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Quiz12 {
    public static List<Socket> list = new ArrayList<>();

    static class Server extends Thread {
        Socket socket;

        public Server(Socket socket) {
            this.socket = socket;
            list.add(socket);
        }

        public Socket getSocket() {
            return socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

                String line;

                while (!Thread.currentThread().isInterrupted()) {
                    while((line = reader.readLine()) != null) {
                        for(Socket s : list) {
                            s.getOutputStream().write((line + "\n").getBytes());
                            s.getOutputStream().flush();
                        }
                    }

                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println();
            }
        }

        try (ServerSocket server = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = server.accept();

                Server ser = new Server(socket);

                ser.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
