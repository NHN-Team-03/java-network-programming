package quiz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Quiz11 {

    static class EchoServer extends Thread {
        Socket socket;

        EchoServer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedInputStream socketIn = new BufferedInputStream(socket.getInputStream());
                 BufferedOutputStream socketOut = new BufferedOutputStream(socket.getOutputStream())) {

                byte[] buffer = new byte[2048];
                int length;

                while ((length = socketIn.read(buffer)) > 0) {
                    socketOut.write(buffer, 0, length);
                    socketOut.flush();
                }

            } catch (IOException ignore){
            }

            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        int port = 1234;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();

                EchoServer server = new EchoServer(socket);
                server.start();
            }
        } catch (IOException ignore){
        }
    }
}
