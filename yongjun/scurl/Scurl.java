package com.nhnacademy.yongjun.scurl;
import org.apache.commons.cli.*;

public class Scurl {
    static CurlMethod curlMethod;

    public static void main(String[] args) {
        Options options = makeOption();
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options,args);
            curlMethod = new CurlMethod(cmd);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }



    public static Options makeOption(){
        Options options = new Options();
        options.addOption("v", "verbose", false, "요청, 응답 헤더를 출력")
                .addOption("H", null, true, "line")
                .addOption("d", null, true, "data")
                .addOption("X", null, true, "command")
                .addOption("L",null, false, "서버의 응답이 30X 계열이면 다음 응답을 따라 간다.")
                .addOption("F", null, true,"name=content");
        return options;

    }

}
