package com.nhnacadmemy.quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Quiz06 {
    public static void main(String[] args) {
        String hostIp = "localhost";
        int port = 1234;

        if (args.length > 0) {
            hostIp = args[0];
        }

        try {
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException ignore) {
            System.err.println("Port 번호가 올바르지 않습니다.");
            System.exit(1);
        }

        try (
            Socket socket = new Socket(hostIp, port);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            BufferedReader terminalIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter terminalOut = new BufferedWriter(new OutputStreamWriter(System.out))) {

            System.out.println("서버에 연결되었습니다.");

            String line;

            while ((line = terminalIn.readLine()) != null) {
                socketOut.write(line + "\n");
                socketOut.flush();

                line = socketIn.readLine();
                terminalOut.write(line + "\n");
                terminalOut.flush();
            }
        } catch (IOException ignore) {
            System.err.println("연결에 실패하였습니다.");
        }
    }
}
