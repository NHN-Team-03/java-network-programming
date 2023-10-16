package exam;

import java.io.IOException;
import java.net.ServerSocket;

public class Exam05 {
    public static void main(String[] args) {
        int port = 1234;

        try (ServerSocket server = new ServerSocket(port)) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
