package nhn.academy.quiz;

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

        if(args.length > 0){
            host = args[0];
        }

        try {
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException ignore) {
            System.err.println("Port가 올바르지 않습니다.");
            System.exit(1);
        }

        try{
            Socket socket = new Socket(host,port);
            System.out.println("서버에 연결 있습니다.");

            OutputStream output = socket.getOutputStream();


            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while(true){
                String line = reader.readLine();
                if(line.equals("exit")){
                    break;
                }

                output.write(line.getBytes()); // 문자열 전송
                output.write("\n".getBytes()); // 문자열 끝을 위한 줄바꿈
                output.flush(); // 남아있는 데이터 완전히 전송

            }

            socket.close();
        } catch(ConnectException  e){
            System.out.println(host + " : " + port + "에 연결할 수 없습니다.");
        } catch(IOException e){

        }
    }
}
