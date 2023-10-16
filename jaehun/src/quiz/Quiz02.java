package quiz;

import java.io.IOException;
import java.net.Socket;

public class Quiz02 {
    public static void main(String[] args) {
        for (int i = Integer.parseInt(args[0]); i <= Integer.parseInt(args[1]); i++) {
            try (Socket socket = new Socket("localhost", i)) {
                System.out.println("Port " + i + " 가 열려 있습니다.");
            } catch (IOException e) {}
        }

        // 장점 자원 관리를 효율적으로 할 수 있다.
        // 단점 통신의 범위가 try문 내부로 고정 된다.
    }
}
