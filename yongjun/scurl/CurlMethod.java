package com.nhnacademy.yongjun.scurl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;

public class CurlMethod {
    static final int[] ERR = {301, 302, 307, 308};
    CommandLine cmd;
    URL url;
    HttpURLConnection connection;
    BufferedReader in;
    String address;
    List<Integer> errCode;

    public CurlMethod(CommandLine cmd) {
        this.cmd = cmd;
        address = cmd.getArgs()[0];
        errCode = new LinkedList<>();
        for (int i = 0; i < ERR.length; i++) {
            errCode.add(ERR[i]);
        }

        try {
            this.url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            checkOption();


            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        printHeader();
    }

    private void printHeader() {
        try {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
            }
            in.close();

            // 응답 내용 출력
            System.out.println(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void checkOption() {
        if (cmd.hasOption("X")) {
            try {
                connection.setRequestMethod(cmd.getOptionValue("X"));
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }
        }

        if (cmd.hasOption("H")) {
            String comment = cmd.getOptionValue("H");
            String[] coms = comment.split(":");

//
            connection.setRequestProperty(coms[0].trim(), coms[1].trim());
        }


        if (cmd.hasOption("d")) {
            String data = cmd.getOptionValue("d");
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

        if (cmd.hasOption("L")) {
            connection.setInstanceFollowRedirects(false);
            try {
                int code = connection.getResponseCode();
                int count = 0;
                while (count < 5) {

                    if (code == HttpURLConnection.HTTP_OK) {
                        break;
                    } else {
                        printVerbose();
                        if (errCode.contains(code)) {
                            address = connection.getHeaderField("Locatioin");
                            System.out.println(address);
                            break;
                        } else {
                            System.out.println("알 수 없는 오류");
                        }
                        if (address != null) {
                            connection = (HttpURLConnection) new URL(address).openConnection();
                            code = connection.getResponseCode();
                        }
                    }
                    count++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        if (cmd.hasOption("F")) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitBoundary7MA$YWxkasTzsoZu023gw");
            String filePath = cmd.getOptionValue("F");
            try (OutputStream os = connection.getOutputStream();
            FileInputStream fis = new FileInputStream(filePath)) {
                String boundary= "----WebKitBoundary7MA$YWxkasTzsoZu023gw";
                String lineEnd = "\r\n";

                String fieldName = "upload";
                String fileName = new File(filePath).getName();
                String contentType = "application/x-www-form-urlencoded";

                os.write(("--" + boundary + lineEnd).getBytes());
                os.write(("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"" + lineEnd).getBytes());
                os.write(("Content-Type: " + contentType + lineEnd).getBytes());
                os.write(lineEnd.getBytes());

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                os.write(lineEnd.getBytes());
                os.write(("--" + boundary + "--" + lineEnd).getBytes());


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if (cmd.hasOption("v")) {
            printVerbose();


        }
    }

    public void printVerbose() {
        if (cmd.hasOption("v")) {
            StringBuilder input = new StringBuilder();
            StringBuilder outpuut= new StringBuilder();
            outpuut.append("*   Trying ").append(url.getHost()).append(":").append(url.getPort()).append("...").append("\n")
                    .append("* Connected to ").append(url.getHost()).append(" (").append(url.getHost()).append(") port ").append(url.getPort()).append(" (#0)").append("\n")
                    .append("> ").append(connection.getRequestMethod()).append(" ").append(url.getFile()).append(" HTTP/1.1").append("\n")
                    .append("> Host: ").append(url.getHost() + "\n");
            

            for (String s : connection.getHeaderFields().keySet()) {
                input.append("<")
                        .append(s)
                        .append(" : ")
                        .append(connection.getHeaderField(s))
                        .append("\n");
            }
            System.out.println(outpuut);
            System.out.println(input);
        }
    }
}





