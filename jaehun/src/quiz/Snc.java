package quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Snc {
    private Socket socket;
    private Thread sendThread;
    private Thread receiveThread;

    public Snc(Socket socket) {
        this.socket = socket;

        sendThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                     BufferedWriter writer = new BufferedWriter(
                             new OutputStreamWriter(this.socket.getOutputStream()))) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        writer.write(line.concat("\n"));
                        writer.flush();
                    }

                } catch (IOException ignore) {
                }
            }
        });

        receiveThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        writer.write(line.concat("\n"));
                        writer.flush();
                    }

                } catch (IOException ignore) {
                }
            }
        });
    }

    public void start() {
        sendThread.start();
        receiveThread.start();
    }

    public void join() {
        try {
            sendThread.join();
            receiveThread.join();
        } catch (InterruptedException ignore) {
        }
    }

    public static void main(String[] args) {
        // host, port 기본값 설정
        String host = "localhost";
        int port = 1234;

        Options options = new Options();

        Option option = Option.builder().option("l").hasArg().desc("서버 모드로 동작, 입력받은 포트로 listen").build();

        options.addOption(option);

        boolean mode = false;

        if (args.length < 2) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("snc [option] [hostname] [port]", options);
            System.exit(1);
        }

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("l")) {
                mode = true;
            }
        } catch (ParseException ignore) {
        }

        host = args[0];
        InetAddress  addr = null;

        try {
            addr = InetAddress.getByAddress(host.getBytes());
        } catch (UnknownHostException e) {
        }

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignore) {
            System.err.println("Snc : Port 번호가 잘못되었습니다.");
            System.exit(1);
        }

        // 서버
        if (mode) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                Socket socket = serverSocket.accept();

                Snc server = new Snc(socket);

                server.start();
                server.join();
            } catch (IOException ignore) {
            }
        }
        // 클라이언트
        else {
            try (Socket socket = new Socket(addr.getHostAddress(), port)) {
                Snc client = new Snc(socket);

                client.start();
                client.join();
            } catch (IOException ignore) {
            }
        }


    }
}
