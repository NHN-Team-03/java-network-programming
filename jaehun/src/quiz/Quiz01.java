package quiz;

import java.io.IOException;
import java.net.Socket;

public class Quiz01 {
    public static void main(String[] args) {
        for (int i = Integer.parseInt(args[0]); i <= Integer.parseInt(args[1]); i++) {
            try {
                Socket socket = new Socket("localhost", i);

                System.out.println("Port " + i + " 열려 있습니다.");

                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
