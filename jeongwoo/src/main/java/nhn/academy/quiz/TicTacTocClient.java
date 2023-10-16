package nhn.academy.quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

public class TicTacTocClient {
    // Client는 차례만 전송받고 항상 입력
    // inner class
    static class Receiver extends Thread {
        BufferedReader input;

        public Receiver(BufferedReader input) {
            this.input = input;
        }

        @Override
        public void run() {
            char[] buffer = new char[2048];
            String message;
            try {
                int length;
                while (!Thread.currentThread().isInterrupted()) {
                    length = input.read(buffer);
                    message = new String(buffer, 0, length);
                    if (message.contains("end")) {
                        System.out.println("종료");
                        break;
                    }
                    System.out.println(message);
                }
                socket.close();
                System.exit(1);
            } catch (IOException ignore) {

            }
        }
    }

    static Socket socket;

    public static void main(String[] args) {
        String id = "Player1";
        String host = "localhost";
        int port = 1234;

        if (args.length > 0) {
            id = args[0];
        }

        if (args.length > 1) {
            host = args[1];
        }

        try {
            if (args.length > 2) {
                port = Integer.parseInt(args[2]);
            }
        } catch (NumberFormatException ignore) {
            System.err.println("Port가 올바르지 않습니다.");
            System.exit(1);
        }

        try {
            socket = new Socket(host, port);
            BufferedReader terminalIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("서버에 연결되었습니다.");

            Receiver receiver = new Receiver(input); // 서버에서 전송하는 값 받아주기
            receiver.start();

            output.write(id);
            output.flush();
            String line;
            while (((line = terminalIn.readLine()) != null)) {
                output.write(line);
                output.flush();
            }
            socket.close();
        } catch (ConnectException e) {
            System.err.println(host + " : " + port + "에 연결할 수 없습니다.");
        } catch (IOException ignore) {
        }
    }
}
