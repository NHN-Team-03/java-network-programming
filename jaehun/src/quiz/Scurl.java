package quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Scurl {

    public static void add(Options options) {
        options.addOption("v", "verbose, 요청, 응답 헤더를 출력합니다.");
        options.addOption("H", true, "임의의 헤더를 서버로 전송합니다.");
        options.addOption("d", true, "POST, PUT 등에 데이터를 전송합니다.");
        options.addOption("X", true, "사용할 method 를 지정합니다. 지정되지 않은경우 기본값은 GET 입니다.");
        options.addOption("L", "서버의 응답이 30X 계열이면 다음 응답을 따라 갑니다.");
        options.addOption("F", true, "multipart/form-data 를 구성하여 전송합니다. content 부분에 @ filename을 사용할 수 있습니다.");
    }

    private static void parse(Options options, String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);


            URL url = new URL(args[args.length - 1]);


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();


            connection.setInstanceFollowRedirects(false);

            if (cmd.hasOption("L")) {
                int count = 0;
                while (connection.getResponseCode() >= 300 && connection.getResponseCode() < 400) {
                    printRequest(connection);
                    printHeader(connection);
                    connection =
                            (HttpURLConnection) new URL(
                                    "http://" + url.getAuthority() +
                                            connection.getHeaderField("Location")).openConnection();
                    count++;


                    connection.setInstanceFollowRedirects(false);

                    if (count >= 5) {
                        throw new IllegalArgumentException("지정된 리다이렉션 횟수를 초과했습니다.");
                    }
                }
            }

            if (cmd.hasOption("H")) {
                String[] requestHeader = cmd.getOptionValue("H").split(":");
                connection.setRequestProperty(requestHeader[0].trim(), requestHeader[1].trim());
            }

            if (cmd.hasOption("X")) {
                connection.setRequestMethod(cmd.getOptionValue("X"));
            }

            if (cmd.hasOption("d")) {
                connection.setDoOutput(true);
                if (connection.getRequestMethod().equals("POST") || connection.getRequestMethod().equals("PUT")) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

                    writer.write(cmd.getOptionValue("d").concat("\n"));
                    writer.flush();
                } else {
                    throw new IllegalArgumentException("해당 RequestMethod로는 데이터 전송이 불가능 합니다.");
                }
            }

            if (cmd.hasOption("F")) {
                String[] fileInfo = cmd.getOptionValue("F").split("=");
                String name = fileInfo[0];
                String filepath = fileInfo[1].substring(1);

                String boundary = Integer.toHexString((int) System.currentTimeMillis());

                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                addFile(name, filepath, connection, boundary);
            }

            connection.connect();

            if (cmd.hasOption("v")) {
                printRequest(connection);
                printHeader(connection);
            }

            if (connection.getContentType().equals("text/*") ||
                    connection.getContentType().equals("application/json")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (ParseException ignore) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("scurl [options] url", options);
        } catch (MalformedURLException e) {
            System.err.println("URL 주소가 잘못 되었습니다.");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException ignore) {
        }
    }

    private static void addFile(String name, String filepath, HttpURLConnection connection, String boundary) {
        try {
            File file = new File(filepath);
            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);

            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filepath + "\"")
                    .append("\r\n");

            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(filepath)).append("\r\n");
            writer.append("\r\n");
            writer.flush();

            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            inputStream.close();

            writer.append("\r\n");
            writer.flush();

            writer.append("--" + boundary + "--").append("\r\n");
            writer.close();
        } catch (IOException ignore) {
        }
    }

    private static void printRequest(HttpURLConnection connection) {
        System.out.println(connection.getRequestMethod() + " " + connection.getURL().getPath() + " ");
        System.out.println("host : " + connection.getURL().getHost());
        System.out.println("User-Agent : " + connection.getURL().getUserInfo());
        System.out.println();
    }

    private static void printHeader(HttpURLConnection connection) {
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            System.out.print(entry.getKey() + " : ");
            for (String header : entry.getValue()) {
                System.out.println(header);
            }
        }

        System.out.println();
    }

    public static void main(String[] args) {
        Options options = new Options();
        add(options);
        parse(options, args);
    }


}