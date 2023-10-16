package exam;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.function.Predicate;

public class Exam03 {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        if(args.length > 0) {
            host = args[0];
        }

        if(args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("잘못된 port 번호 입니다.");
                System.exit(1);
            }
        }

        try {
            Socket socket = new Socket(host, port);
            System.out.println("서버에 연결되었습니다.");

            int ch;

            BufferedInputStream bi = new BufferedInputStream(socket.getInputStream());

            while((ch = bi.read()) >= 0) {
                System.out.write(ch);
            }

//            while((ch = socket.getInputStream().read()) >= 0) {
//                System.out.write(ch);
//            }

            socket.close();

        } catch(ConnectException e) {
            System.err.println(host + ":" + port + "에 연결할 수 없습니다.");
        } catch(IOException e) {
            System.err.println(e);
        }



    }
}
