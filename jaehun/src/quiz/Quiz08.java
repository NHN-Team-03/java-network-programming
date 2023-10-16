package quiz;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Quiz08 {
    public static void main(String[] args) {
        int port = 1234;

        if(args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Port 번호가 잘못되었습니다.");
                System.exit(1);
            }
        }

        try (ServerSocket server = new ServerSocket(port)) {
            Socket socket = server.accept();

            System.out.println("Client[" + socket.getRemoteSocketAddress() + "]가 연결되었습니다.");
            System.out.println("Client[" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "]가 연결되었습니다.");

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
