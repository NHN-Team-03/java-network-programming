package quiz;

import java.io.IOException;
import java.net.Socket;

public class Quiz01 {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("검색할 포트 범위 지정이 필요합니다.");
        }

        int startPort = Integer.parseInt(args[0]);
        int endPort = Integer.parseInt(args[1]);

        while (startPort <= endPort) {
            try {
                Socket socket = new Socket("localhost", startPort);
                System.out.println("Port " + startPort + " 열려 있습니다.");
                socket.close();
            } catch (IOException e) {
                //
            }
            startPort++;
        }
    }
}
