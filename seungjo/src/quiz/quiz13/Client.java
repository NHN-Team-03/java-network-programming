package quiz.quiz13;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


/**
 * 아래의 요구 사항에 맞는 client를 구현해 보자.
 * 실행시 서비스를 위한 id, host, port를 지정할 수 있다.
 * 별도의 지정이 없는 경우,
 * id는 임의의 문자열로 생성한다.
 * host는 localhost
 * port는 1234로 한다.
 * Client가 server에 정상적으로 접속하면, 설정된 id를 전송한다.
 * 특정 client에만 메시지 전송을 원할 경우, 메시지 앞에 @[대상 id]을 추가한다.
 * 제어 명령을 구현한다.
 * !exit 명령은 clie은 연결을 끊는다.
 */
public class Client extends Thread {
    static List<Client> clientList = new LinkedList<>();

    private Socket socket;
    private String id;

    private BufferedWriter writer;


    public Client(String id, String host, int port) throws IOException {
        this.id = id;

        this.socket = new Socket(host, port);
        clientList.add(this);
    }

    public static String getClientList() {
        StringBuilder sb = new StringBuilder();
        for (Client client : clientList) {
            sb.append(client.id).append("\n");
        }
        return sb.toString();
    }

    public void write(String message) throws IOException {
        writer.write(message);
        writer.flush();
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            this.writer = writer;
            write("당신의 ID: " + this.getName() + "\n");

            while (!Thread.currentThread().isInterrupted()) {
                String line = reader.readLine() + "\n";

                if (line.charAt(0) == '@') {
                    String[] str = line.split(" ", 2);
                    String id = str[0].substring(1);
                    String message = str[1];

                    for (Client client : clientList) {
                        if (client.id.equals(id)) {
                            client.write("@" + this.getName() + " : " + message);
                        }
                    }
                } else {
                    for (Client client : clientList) {
                        client.write(this.getName() + " : " + line);
                    }
                }
            }
        } catch (IOException ignore) {
        }
    }

    public static void main(String[] args) throws IOException {
        String id = "user";
        String host = "localhost";
        int port = 1234;

        if (args.length > 0) {
            id = args[0];
        }

        if (args.length > 1) {
            host = args[1];
        }

        if (args.length > 2) {
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignore) {
                System.out.println("1 ~ 65535");
            }
        }

        Client client = new Client(id, host, port);
        client.start();

        Thread exitThread = new Thread(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String cmd;

            while (true) {
                try {
                    cmd = br.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (cmd.equals("!exit")) {
                    Server.removeSocket(client.socket);
                }
            }
        });
        exitThread.start();

        try {
            exitThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
