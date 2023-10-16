package nhn.academy.quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

/*
아래의 요구 사항에 맞는 client를 구현해 보자.

실행시 서비스를 위한 id, host, port를 지정할 수 있다.

별도의 지정이 없는 경우,

id는 임의의 문자열로 생성한다.

host는 localhost

port는 1234로 한다.

Client가 server에 정상적으로 접속하면, 설정된 id를 전송한다.

특정 client에만 메시지 전송을 원할 경우, 메시지 앞에 @[대상 id]을 추가한다.

제어 명령을 구현한다.

!exit 명령은 client은 연결을 끊는다.
*/
public class Quiz13Client {
    static Socket socket;
    static class Receiver extends Thread{
        BufferedReader input;

        public Receiver(BufferedReader input) {
            this.input = input;
        }

        @Override
        public void run() {
            char[] buffer = new char[2048];
            String message;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    int length;
                    length = input.read(buffer);
                    message = new String(buffer, 0, length);
                    if(message.equals("!exit")){
                        socket.close();
                        break;
                    }
                    System.out.println(message);
                }
            } catch (IOException ignore) {

            }
        }
    }
    public static void main(String[] args) {
        String id = "Team03";
        String host = "localhost";
        int port = 1234;

        if(args.length > 0){
            id = args[0];
        }

        if(args.length > 1){
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

        try
        {
            socket = new Socket(host, port);
            BufferedReader terminalIn = new BufferedReader((new InputStreamReader(System.in)));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("서버에 연결");

            Receiver receiver = new Receiver(input);

            receiver.start();
            output.write(id);
            output.flush();

            String line;
            while ((line = terminalIn.readLine()) != null) {
                if (line.trim().equals("!exit")) {
                    output.write(line);
                    output.flush();
                    break;
                }
                output.write(line);
                output.flush();
            }
            socket.close();
        } catch(ConnectException e){
            System.out.println(host + " : " + port + "에 연결할 수 없습니다.");
        } catch(IOException e){

        }
    }
}
