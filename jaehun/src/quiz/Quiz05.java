package quiz;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Quiz05 {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        if (args.length > 0) {
            host = args[0];
        }

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("잘못된 port 번호 입니다.");
                System.exit(1);
            }
        }

        try {
            Socket socket = new Socket(host, port);
            System.out.println("서버에 연결되었습니다.");

            BufferedInputStream bi = new BufferedInputStream(socket.getInputStream());

            StringBuilder builder = new StringBuilder();

            int ch;
            while ((ch = bi.read()) >= 0) {
                if (ch == '\n') {
                    if (builder.toString().equalsIgnoreCase("exit")) {
                        break;
                    } else {
                        System.out.println(builder);
                        builder.delete(0, builder.length());
                        continue;
                    }
                }

                builder.append(Character.valueOf((char) ch));
            }


            System.out.println("종료합니다.");

            socket.close();
        } catch (
                ConnectException e) {
            System.err.println(host + ":" + port + "에 연결할 수 없습니다.");
        } catch (
                IOException e) {
            System.err.println(e);
        }
    }
}
