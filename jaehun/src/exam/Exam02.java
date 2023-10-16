package exam;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.stream.IntStream;


// tag::Exam02[]
public class Exam02 {
    public static void main(String[] args) {
        String[] info = {"localhost", "1234"};

        for (int i = 0; i < args.length; i++) {
            info[i] = args[i];
        }

        

        try {
            Socket socket = new Socket(info[0], Integer.parseInt(info[1]));

            System.out.println("서버에 연결되었습니다.");

            socket.getOutputStream().write("Hello World!".getBytes());

            socket.close();

        } catch (ConnectException e) {
            System.err.println(info[0] + ":" + info[1] + "에 연결할 수 없습니다.");
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
// end::Exam02[]

