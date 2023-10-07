package quiz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Quiz10 {
    public static void main(String[] args) {

        int port = 1234;
        int bufferSize = 2048;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                System.err.println("Port는 0 ~ 65535 사이의 정수만 가능합니다.");
                System.exit(1);
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket socket = serverSocket.accept();
             BufferedInputStream socketIn = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream socketOut = new BufferedOutputStream(socket.getOutputStream())) {

            System.out.println("Connected client! ");
            System.out.println("Client [" + socket.getInetAddress().getHostAddress()
                    + ":" + socket.getPort() + "]");

            byte[] buffer = new byte[bufferSize];
            int length;

            while((length = socketIn.read(buffer)) > 0) {
                socketOut.write(buffer, 0, length);
                socketOut.flush();
            }

        } catch (IOException ignore) {
            System.out.println("서비스 열기에 실패하였습니다.");
        }
    }
}
