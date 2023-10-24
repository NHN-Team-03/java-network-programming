package com.nhnacadmemy.snc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Snc {

    public static void connect(Socket socket) {
        try {
            Transfer sendTransfer = new Transfer(
                    new BufferedReader(new InputStreamReader(System.in)),
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

            Transfer receiveTransfer = new Transfer(
                    new BufferedReader(new InputStreamReader(socket.getInputStream())),
                    new BufferedWriter(new OutputStreamWriter(System.out)));

            sendTransfer.start();
            receiveTransfer.start();

            sendTransfer.join();
            receiveTransfer.join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException ignore) {
        }
    }

    private static void serverMode(int port) {

        while (!Thread.currentThread().isInterrupted()) {
            try (ServerSocket serverSocket = new ServerSocket(port);
                 Socket socket = serverSocket.accept()) {
                System.out.println(socket.getInetAddress().getHostName() + ":" + socket.getPort());
                connect(socket);

            } catch (IOException ignore) {
            }
        }
    }

    private static void clientMode(String host, int port) {

        while (!Thread.currentThread().isInterrupted()) {
            try (Socket socket = new Socket(host, port)) {
                connect(socket);

            } catch (IOException ignore) {
            }
        }
    }

    public static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("snc [option] [host] [port]", options);
    }

    public static void main(String[] args) {

        Options options = new Options();
        options.addOption("l", null, true, "서버 모드로 동작, 입력 받은 포트로 listen");
        options.addOption("h", null, false, "도움말");

        String host = "localhost";
        int port = 1234;

        if (args.length < 2) {
            printHelp(options);
            System.exit(1);
        }

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("l")) {
                try {
                    port = Integer.parseInt(cmd.getOptionValue("l"));
                    serverMode(port);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("포트 번호를 잘못입력하셨습니다. 1 ~ 65535 사이의 값을 입력해주세요.");
                }
            }  else if (cmd.hasOption("h")) {
                printHelp(options);
            } else {
                try {
                    host = args[0];
                    port = Integer.parseInt(args[1]);
                    clientMode(host, port);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("포트 번호를 잘못입력하셨습니다. 1 ~ 65535 사이의 값을 입력해주세요.");
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

    }
}
