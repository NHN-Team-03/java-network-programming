package quiz;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Quiz08 {
    public static void main(String[] args) {
        int port = 1234;

        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException ignore) {
            System.err.println("Port 정보가 잘못되어있습니다.");
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket = serverSocket.accept();

            System.out.println("Client[" + socket.getInetAddress().getHostAddress() + ":" +
                    socket.getPort() + "]가 연결되었습니다.");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
