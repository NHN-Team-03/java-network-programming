package com.nhnacadmemy.scurl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Scurl {

    public static void addOptions(Options options) {

        Option header = Option.builder().option("H").argName("line").hasArg(true).desc("임의의 헤더를 서버로 전송합니다.").build();

        Option data =
                Option.builder().option("d").argName("data").hasArg(true).desc("POST, PUT 등에 데이터를 전송합니다.").build();

        Option method = Option.builder().option("X").argName("command").hasArg(true)
                .desc("사용할 method를 지정합니다. 지정되지 않은 경우 기본 값은 GET입니다.").build();

        Option file = Option.builder().option("F").argName("name=content").hasArg(true)
                .desc("multipart/form-data를 구성하여 전송합니다. content 부분에 @filename을 사용할 수 있습니다.").build();

        options.addOption("v", null, false, "verbose, 요청, 응답 헤더를 출력합니다.");
        options.addOption(header);
        options.addOption(data);
        options.addOption(method);
        options.addOption("L", null, false, "서버 응답이 30X 계열이면 다음 응답을 따라갑니다.");
        options.addOption(file);

    }

    public static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("scurl [option] url", options);
        System.exit(1);
    }

    public static void getMethod(HttpURLConnection connection) {
        bodyMessage(connection);
    }

    public static void verbose(HttpURLConnection connection, String... addHeader) {

        requestMessage(connection.getURL(), connection);

        for (String header : addHeader) {
            System.out.println("> " + header);
        }
        System.out.println("> ");

        responseMessage(connection);
        bodyMessage(connection);
    }

    private static void bodyMessage(HttpURLConnection connection) {

        try {
            // 응답 바디 출력
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            System.out.println(sb);
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void responseMessage(HttpURLConnection connection) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> stringListEntry : headerFields.entrySet()) {
            System.out.println("< " + stringListEntry.getKey() + ": " + stringListEntry.getValue());
        }
    }

    private static void requestMessage(URL url, HttpURLConnection connection) {

        System.out.println("> " + connection.getRequestMethod() + " " + url.getPath() + " HTTP/1.1");
        System.out.println("> " + "Host: " + url.getHost());
        System.out.println("> " + "User-Agent: " + connection.getRequestProperty("User-Agent"));
        System.out.println("> " + "Accept: " + connection.getHeaderField("Accept"));
    }

    public static void main(String[] args) {

        Options options = new Options();
        addOptions(options);

        try {
            URL url = new URL(args[args.length - 1]);

            // URL만 지정해줬을 경우 default method는 GET
            if (args.length == 1) {
                args = defaultMethod(args);
            }

            String header = "";
            String body = "";
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            //
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (cmd.hasOption("d")) {
                body = cmd.getOptionValue("d");

            }

            if (cmd.hasOption("H")) {
                connection.setRequestMethod("GET");

                // 요청 헤더에 추가로 전송
                header = cmd.getOptionValue("H");
                String[] split = header.split(":");
                connection.addRequestProperty(split[0].trim(), split[1].trim());
            }


            if (cmd.hasOption("X")) {

                String method = cmd.getOptionValue("X");
                switch (method) {
                    case "GET":
                        connection.setRequestMethod("GET");
                        getMethod(connection);
                        break;

                    case "POST":
                        connection.setRequestMethod("POST");
                        postMethod(connection, body);
                        break;

                    case "PUT":
                        connection.setRequestMethod("PUT");
                        putMethod(connection, body);
                        break;

                    case "DELETE":
                        connection.setRequestMethod("DELETE");
                        break;

                    default:
                        throw new IllegalArgumentException("지원하지 않는 method입니다.");
                }
            }


            if (cmd.hasOption("L")) {
                try {
                    redirectionMethod(url);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.exit(1);
                }
            }

            if (cmd.hasOption("v")) {
                if (!header.isEmpty()) {
                    verbose(connection, header);
                } else {

                    verbose(connection);
                }
            }


            // $ scurl -F "upload=@file_path" http://httpbin.org/post
            if (cmd.hasOption("F")) {

                String file = cmd.getOptionValue("F");

                connection.addRequestProperty("Content-Type", "multipart/form-data");
                fileMethod(connection, file);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            printHelp(options);
        }

    }


    private static String[] defaultMethod(String[] args) {
        args = Arrays.copyOf(args, args.length + 1);
        args[0] = "-X";
        args[1] = "GET";
        return args;
    }

    private static void fileMethod(HttpURLConnection connection, String file) {

        String[] split = file.split("=", 2);
        String fileName = split[0];
        String filePath = split[1].substring(1);
        File uploadFile = new File(filePath);

        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";

        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);
             FileInputStream fileInputStream = new FileInputStream(uploadFile)) {


            writer.append("--").append(boundary).append(CRLF);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + fileName + "\"; filename=\"" + uploadFile.getName() +
                            "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(uploadFile.getName()))
                    .append(CRLF); // Binary data

            writer.append(CRLF).flush();

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();

            writer.append(CRLF).flush();
            writer.append("--" + boundary + "--").append(CRLF).flush();

            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);

            getMethod(connection);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void redirectionMethod(URL initURL) {

        int redirectCount = 0;
        URL url = initURL;
        System.out.println(url);
        String host = "http://".concat(initURL.getHost());

        while (redirectCount <= 5) {

            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.connect();

                int responseCode = connection.getResponseCode();
                String location = connection.getHeaderField("Location");

                if (responseCode == 301 || responseCode == 302 || responseCode == 307 || responseCode == 308) {
                    String stringURL = host.concat(location);
                    url = new URL(stringURL);
                    verbose(connection);
                    connection.disconnect();
                    redirectCount++;
                } else {
                    break;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        if (redirectCount == 5) {
            throw new IllegalArgumentException("Redirection 횟수가 5회를 초과하였습니다.");
        }
    }

    private static void postMethod(HttpURLConnection connection, String body) {
        try {
            connection.setDoOutput(true);

            System.out.println("body = " + body);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body.getBytes());
            outputStream.flush();

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void putMethod(HttpURLConnection connection, String body) {
        try {
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body.getBytes());
            outputStream.flush();

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
