package quiz.shttpd;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 8000;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                System.err.println("잘못된 Port 번호 입니다.");
                System.exit(1);
            }
        }

        SimpleHttp http = new SimpleHttp(port);

        http.start();
    }
}
