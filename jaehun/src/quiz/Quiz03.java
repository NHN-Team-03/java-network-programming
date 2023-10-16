package quiz;

import java.io.IOException;
import java.net.Socket;

public class Quiz03 {
    public static void main(String[] args) {
        String[] info = {"localhost", "1234"};

        for(int i = 0; i < args.length; i++) {
            info[i] = args[i];
        }

        try (Socket socket = new Socket(info[0], Integer.parseInt(info[1]))){
            System.out.println("서버에 연결되었습니다.");
            String localInfo = socket.getLocalSocketAddress().toString();
            String remoteInfo = socket.getRemoteSocketAddress().toString();


            System.out.println("Local address : " + localInfo.substring(localInfo.indexOf("/") + 1, localInfo.indexOf(":")));
            System.out.println("Local port : " + localInfo.substring(localInfo.indexOf(":") + 1));
            System.out.println("Remote address : " + remoteInfo.substring(remoteInfo.indexOf("/") + 1, remoteInfo.indexOf(":")));
            System.out.println("Remote port : " + remoteInfo.substring(remoteInfo.indexOf(":") + 1));
        } catch (IOException e) {
            System.out.println(info[0] + ":" + info[1] + "에 연결할 수 없습니다.");
        }


    }
}
