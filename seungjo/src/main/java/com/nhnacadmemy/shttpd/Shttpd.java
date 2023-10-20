package com.nhnacadmemy.shttpd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.HelpFormatter;

public class Shttpd {

    static {
        filePath = "/Users/seungjo/NHN-Academy/java-network-programming/seungjo/src/main/java/com/nhnacadmemy/shttpd";
    }

    private static final String filePath;


    public static void usage() {
        System.out.println("Usage: shttpd port (1 ~ 65535)");
    }


    public static Set<String> getFileList(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public static void printFileList(BufferedWriter writer) {
        Set<String> fileList = getFileList(filePath);
        try {
            writer.write("======== FILE LIST ========\n");
            for (String file : fileList) {
                writer.write(file);
                writer.newLine();
            }
            writer.write("===========================");
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        int port = 80;

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                usage();
                System.exit(1);
            }
        }


        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket = serverSocket.accept();


            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.equals("GET")) {
                    printFileList(writer);
                }
            }

        } catch (IllegalArgumentException e) {
            usage();
            System.exit(1);
        } catch (IOException e) {
            usage();
            System.exit(1);
        }
    }
}
