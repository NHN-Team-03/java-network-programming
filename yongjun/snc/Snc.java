package com.nhnacademy.yongjun.snc;

import org.apache.commons.cli.*;

public class Snc {
    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("l", null, true, "servermode");

        System.out.println("옵션 설정 완료");

        try {
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("l")){
                System.out.println("서버 연결");
                SncServer.main(args);
            }else {
                Client.main(args);
                System.out.println("클라이언트 연결");
            }

        } catch (ParseException e) {
            System.out.println("잘못된 입력");
            throw new RuntimeException(e);
        }

//        if (args.length != 3) {
//            System.out.println("잘못된 입력");
//            System.exit(1);
//        }
//        if(args[1].equals("-l")){
//            SncServer.main(args);
//        }
//        else if(args[1].equals("127.0.0.1") || args[1].equals("localhost")){
//            Client.main(args);
//        }
//        else {
//            System.out.println("잘못된 입력");
//            System.exit(1);
//        }

    }
}
