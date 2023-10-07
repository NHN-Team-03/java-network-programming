package quiz;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Quiz04 {

    public static void main(String[] args) {
        String hostIp = "localhost";
        int port = 1234;

        if (args.length > 0) {
            hostIp = args[0];
        }

        try {
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Port 번호가 올바르지 않습니다.");
            System.exit(1);
        }

        Scanner sc = new Scanner(System.in);
        try {
            Socket socket = new Socket(hostIp, port);
            String input;
            while (!(input = sc.nextLine()).equals("exit")) {
                socket.getOutputStream().write((input + "\n").getBytes());
            }
            socket.close();
        } catch (ConnectException e) {
            System.err.println(hostIp + ":" + port + "에 연결할 수 없습니다.");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        sc.close();
    }
}
