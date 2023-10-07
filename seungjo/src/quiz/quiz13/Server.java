package quiz.quiz13;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 아래의 요구 사항에 맞는 server를 구현해 보자.
 * 실행시 서비스를 위한 port를 지정할 수 있다. 별도의 port 지정이 없는 경우, 1234를 기본으로 한다.
 * Server는 실행 후 대기 상태를 유지하고, client가 접속되면 client 정보를 출력한다.
 * Server는 하나 이상의 client가 접속되어도 동시에 지원 가능하도록 한다.
 * Server는 접속된 client로부터 ID를 받아 별도 관리한다.
 * Client 메시지 시작에 대상 client id가 추가되어 있는 경우, 해당 client에만 메시지를 전달한다.
 * 대상 client id는 "@[ID] message"로 @ 다음에 붙여서 온다.
 * user1에 hello 메시지를 보내기 위해서는 "@user1 hello"로 보내면 된다.
 * 제어 명령을 구현한다.
 * !list 명령은 접속되어 있는 client의 id list를 반환한다.
 */
public class Server extends Thread {

    static ServerSocket serverSocket;
    static List<Socket> socketList = new ArrayList<>();

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public static void removeSocket(Socket socket) {
        socketList.remove(socket);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                socketList.add(socket);

                System.out.println("[" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "]가 접속했습니다.");

            }
        } catch (IOException ignore) {
        }
    }

    public static void main(String[] args) throws IOException {

        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
            }
        }

        Server server = new Server(port);
        server.start();

        Thread userListThread = new Thread(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String cmd;

            while (true) {
                try {
                    cmd = br.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (cmd.equals("!list")) {
                    System.out.println(Client.getClientList());
                }
            }
        });

        userListThread.start();

        try {
            userListThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}