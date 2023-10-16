package exam;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Exam04 {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(1234)){
            Socket socket = server.accept();

            socket.getOutputStream().write("Hello!".getBytes());
            socket.getOutputStream().flush();

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
