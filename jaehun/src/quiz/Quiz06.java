package quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

public class Quiz06 {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        if(args.length > 0) {
            host = args[0];
        }

        if(args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Port가 올바르지 않습니다.");
            }
        }

        try (Socket socket = new Socket(host, port)) {
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader terminalIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter terminalOut = new BufferedWriter(new OutputStreamWriter(System.out));

            System.out.println("서버에 연결되었습니다.");

            String line;

            while ((line = terminalIn.readLine()) != null) {
                socketOut.write(line + "\n");
                socketOut.flush();

                line = socketIn.readLine();
                terminalOut.write(line + "\n");
                terminalOut.flush();
            }
        } catch (ConnectException e) {
            System.err.println(host + ":" + port + "에 연결할 수 없습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
