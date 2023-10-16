package quiz.tictactoe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port = 1234;

        try {
            ServerSocket server = new ServerSocket(port);
            Socket socket1 = server.accept();
            socket1.getOutputStream().write("상대방의 접속을 기다리고 있습니다...\n".getBytes());
            socket1.getOutputStream().flush();
            Socket socket2 = server.accept();

            Tictactoe tictactoe = new Tictactoe(socket1, socket2);

            tictactoe.start();
        } catch (IOException ignore) {
        }
    }
}
