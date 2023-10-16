package nhn.academy.quiz;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Quiz11 {
    /*
    아래의 요구 사항에 맞는 server를 구현해 보자.

실행시 서비스를 위한 port를 지정할 수 있다. 별도의 port 지정이 없는 경우, 1234를 기본으로 한다.

Server는 실행 후 대기 상태를 유지하고, client가 접속되면 client 정보를 출력한다.

Server는 접속된 client가 보내온 데이터를 돌려 보낸다.

Server는 하나 이상의 client가 접속되어도 동시에 지원 가능하도록 한다.
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        try{
            if(args.length > 0){
                port = Integer.parseInt(args[1]);
            }
        }catch (NumberFormatException ignore){
            System.out.println("Port번호가 올바르지 않습니다.");
            System.exit(1);
        }

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket = serverSocket.accept();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
