package com.nhnacadmemy.quiz;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Quiz03 {
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
            System.err.println("Port가 올바르지 않습니다.");
            System.exit(1);
        }

        StringBuilder sb = new StringBuilder();
        try {
            Socket socket = new Socket(hostIp, port);
            sb.append("서버에 연결되었습니다.").append("\n");
            sb.append("Local address : ").append(socket.getLocalAddress().getHostAddress()).append("\n");
            sb.append("Local port : ").append(socket.getLocalPort()).append("\n");
            sb.append("Remote address : ").append(socket.getInetAddress().getHostAddress()).append("\n");
            sb.append("Remote port : ").append(socket.getPort());


            socket.close();
            System.out.println(sb);

        } catch (ConnectException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.out.println(hostIp + ":" + port + "에 연결할 수 없습니다.");
        }
    }
}
