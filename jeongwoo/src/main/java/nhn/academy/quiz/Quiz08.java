package nhn.academy.quiz;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Quiz08 {
    public static void main(String[] args) {
        int port = 1234;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                System.out.println("Port 0 ~ 65535 까지 정수만");
                System.exit(1);
            }
        }
        System.out.println("중단 점");
        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket socket = serverSocket.accept()) {

            System.out.println(socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "가 연결");
            System.out.println("연결 종료");
        } catch (IOException ignore) {
            System.out.println("서비스 열기 실패");
        }
    }
}
