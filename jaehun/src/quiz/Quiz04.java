package quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

public class Quiz04 {
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
                System.err.println("잘못 된 port 번호 입니다.");
                System.exit(1);
            }
        }



        try {
            Socket socket = new Socket(host, port);
            System.out.println("서버 연결에 성공했습니다.");

            OutputStream output = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String line;

            while (true) {
                line = reader.readLine();

                if(line.toLowerCase().equals("exit")) {
                    System.out.println("종료합니다.");
                    break;
                }

                output.write(line.getBytes());
                output.write("\n".getBytes());
                output.flush();

            }

            socket.close();

        } catch (ConnectException e) {
            System.err.println(host + ":" + port + "에 연결할 수 없습니다.");
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
