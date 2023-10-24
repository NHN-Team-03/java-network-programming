package com.nhnacademy.scurl;

import java.io.IOException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Scurl {
    public static Options createOptions() {
        Options options = new Options();

        options.addOption("v", false, "verbose, 요청, 응답 헤더를 출력한다.");
        options.addOption("H", true, "임의의 헤더를 서버로 전송한다.");
        options.addOption("d", true, "POST, PUT 등에 데이터를 전송한다.");
        options.addOption("X", true, "사용할 method르 지정한다. 지정되지 않은 경우, 기본값은 GET");
        options.addOption("L", false, "서버의 응답이 30x 계열이면 다음 응답을 따라 간다.");
        options.addOption("F", true, "multipart/form-dadt를 구성하여 전송한다. content 부분에 @filename을 사용할 수 있다.");

        return options;
    }

    public static void main(String[] args) throws ParseException, IOException {
        if (args.length < 1) {
            System.err.println("인자의 개수가 1보다 적습니다.");
            System.exit(1);
        }

        Options options = createOptions();

        HttpObject httpObject = new HttpObject(options, args);
        httpObject.run();
        //responseMethod 설정
    }
}
