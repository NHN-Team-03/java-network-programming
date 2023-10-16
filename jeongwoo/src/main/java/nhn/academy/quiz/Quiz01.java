package nhn.academy.quiz;

import java.net.Socket;

public class Quiz01 {
    public static void main(String[] args) {
        int startPort = 0;
        int endPort = 65535;

        if (args.length < 2) {
            System.out.println("검색할 포트 범위 지정이 필오");
        }

        try {
            startPort = Integer.parseInt(args[0]);
            endPort = Integer.parseInt(args[1]);

            if (endPort < startPort) {
                int temp = startPort;
                startPort = endPort;
                endPort = temp;
            }
        } catch (NumberFormatException e) {
            System.err.println("포트 올바르지 않아요~");
        }

        for(int port = startPort; port < endPort;port++){
            try{
                Socket socket = new Socket("localhost",port);
                System.out.println("Port " + port + " 열려 있습니다.");
                socket.close();
            }catch (Exception ignore){

            }
        }
    }
}
