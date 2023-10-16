package nhn.academy.quiz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Quiz13Server {
    /*
    아래의 요구 사항에 맞는 server를 구현해 보자.

실행시 서비스를 위한 port를 지정할 수 있다. 별도의 port 지정이 없는 경우, 1234를 기본으로 한다.

Server는 실행 후 대기 상태를 유지하고, client가 접속되면 client 정보를 출력한다.

Server는 하나 이상의 client가 접속되어도 동시에 지원 가능하도록 한다.

Server는 접속된 client로부터 ID를 받아 별도 관리한다.

Client 메시지 시작에 대상 client id가 추가되어 있는 경우, 해당 client에만 메시지를 전달한다.

대상 client id는 "@[ID] message"로 @ 다음에 붙여서 온다.

user1에 hello 메시지를 보내기 위해서는 "@user1 hello"로 보내면 된다.

제어 명령을 구현한다.

!list 명령은 접속되어 있는 client의 id list를 반환한다.

*/
    public static Map<String, ChatHandler> chatHandlerList = new HashMap<>();

    public static void main(String[] args) {
        int port = 1234;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                System.out.println("Port 0 ~ 65535 까지 정수만");
                System.exit(1);
            }
        }
        InputThread inputThread = new InputThread();
        inputThread.start();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                ChatHandler handler = new ChatHandler(socket);
                handler.start();
            }
        } catch (IOException ignore) {
            System.out.println("서비스 열기 실패");
        }
    }
    static class InputThread extends Thread {
        BufferedReader reader;

        public InputThread() {
            reader = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run() {
            String word;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    word = reader.readLine();
                    if (word.equals("!list") || word.equals("!List")) {
                        for (String id : chatHandlerList.keySet()) {
                            System.out.println(id);
                        }
                    }
                    word = "";
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}



class ChatHandler extends Thread {
    Socket socket;
    int bufferSize = 2048;
    private String ID;
    private BufferedOutputStream socketOut;

    public ChatHandler(Socket socket) {
        this.socket = socket;
        System.out.println(socket.getInetAddress().getHostAddress() + " : " + socket.getPort() + "가 연결");
    }

    public void send(String id, String data) {
        System.out.println(ID + "가 " + id + "에게 전송 : " + data);
        Quiz13Server.chatHandlerList.get(id).receive(data);
    }

    public void receive(String data) {
        try {
            socketOut.write(data.getBytes(), 0, data.length());
            socketOut.flush();
        } catch (IOException ignore) {
        }
    }

    public void sendAll(String data) {
        for (String id : Quiz13Server.chatHandlerList.keySet()) {
            if (id.equals(ID)) {
                continue;
            }
            Quiz13Server.chatHandlerList.get(id).receive(data);
        }
    }

    @Override
    public void run() {

        try (BufferedInputStream socketIn = new BufferedInputStream(socket.getInputStream());) {
            socketOut = new BufferedOutputStream(socket.getOutputStream());
            byte[] buffer = new byte[bufferSize];
            int length;

            length = socketIn.read(buffer);
            ID = new String(buffer, 0, length);
            Quiz13Server.chatHandlerList.put(ID, this);

            String word;
            while ((length = socketIn.read(buffer)) > 0) {

                word = new String(buffer, 0, length);

                if (word.equals("!exit")) {
                    System.out.println(socket.getInetAddress().getHostAddress() + " : " + socket.getPort() + "가 연결 해제");
                    String message = "연결이 해제되었습니다.";
                    socketOut.write(message.getBytes());
                    socketOut.write(word.getBytes());

                    Quiz13Server.chatHandlerList.remove(ID);
                } else if (word.charAt(0) == '@') {
                    int endindex = word.indexOf(']');
                    if (endindex != -1) {
                        this.send(word.substring(2, endindex), word.substring(endindex + 1, length));
                    }
                } else {
                    System.out.println(ID + "가 모두에게 전송 : " + word);
                    sendAll(word);
                }
                socketOut.flush();
            }

        } catch (IOException ignore) {
            System.out.println("서비스 열기 실패");
        }
    }
}