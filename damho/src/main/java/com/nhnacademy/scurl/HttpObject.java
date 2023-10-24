package com.nhnacademy.scurl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class HttpObject {
    private CommandLine cmd;
    private boolean isVerboseMode = false;
    //    private boolean isConnected = false;
    private String contentType = null;
    private HttpURLConnection conn = null;
    private String responseMethod = "GET";
    private final int maxRedirectCount = 5;
    private int currentRedirectCount = 0;

    public HttpObject(Options options, String[] args) throws ParseException, IOException {
        CommandLineParser parser = new DefaultParser();
        this.cmd = parser.parse(options, args);
        this.conn = createHttpURLConnection(args[args.length - 1]);
        this.conn.setInstanceFollowRedirects(false);
    }

    public void run() {
        processOptionsBeforeConnect();
        printRequestHeader();

        processOptionsWithConnect();

        searchRedirectURL();
        printResponseHeader();
        printDataFromServer(conn);
    }

//    public void connect() {
//        try {
//            this.conn.connect();
//            isConnected = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void makeRequestMethod() {
        if (!this.cmd.hasOption("X")) {
            return;
        }

        String responseMethod = this.cmd.getOptionValue("X");
        if (!responseMethod.equals("GET") && !responseMethod.equals("POST") && !responseMethod.equals("PUT")) {
            throw new IllegalArgumentException("-X 명령어의 인자는 GET, POST, PUT만 가능합니다.");
        }

        this.responseMethod = responseMethod;
    }

    public static HttpURLConnection createHttpURLConnection(String urlPath) throws IOException {
        HttpURLConnection conn = null;
        URL url = new URL(urlPath);

        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new IllegalArgumentException("url의 값이 유효하지 않습니다.");
        }

        return conn;
    }

    public void printRequestHeader() {
        if (!isVerboseMode) {
            return;
        }
        System.out.println("< Request Method : " + this.conn.getRequestMethod());

        Map<String, List<String>> keys = this.conn.getRequestProperties();
        for (String key : keys.keySet()) {
            System.out.println("< " + key + " : " + keys.get(key));
        }
        System.out.println();
    }

    public void printResponseHeader() {
        if (!isVerboseMode) {
            return;
        }
        Map<String, List<String>> keys = this.conn.getHeaderFields();
        for (String key : keys.keySet()) {
            System.out.println("> " + key + " : " + this.conn.getHeaderField(key));
        }
        System.out.println();
    }

    private static void printDataFromServer(HttpURLConnection connection) {
        if (connection.getHeaderField("Content-Type").startsWith("text/") ||
                connection.getHeaderField("Content-Type").startsWith("application/")) {
            System.out.println("서버로부터 데이터 받음");
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.err.println("서버로부터 데이터 받는 중 IOException 발생");
                System.exit(1);
            }
        }
    }

    private void processOptionsBeforeConnect() {
        makeRequestMethod();
        try {
            this.conn.setRequestMethod(this.responseMethod);
        } catch (ProtocolException e) {
            System.err.println("ProtocolException 발생");
            System.exit(1);
        }

        if (cmd.hasOption("v")) {
            this.isVerboseMode = true;
        }

        if (cmd.hasOption("H")) {
            String[] args = cmd.getOptionValue("H").split(":");
            conn.setRequestProperty(args[0].trim(), args[1].trim());
        }
    }

    public void processOptionsWithConnect() {
        if (cmd.hasOption("d")) {
            try {
                conn.setDoOutput(true);
                if (this.contentType == null) {
                    this.contentType = "application/x-www-form-urlencoded";
                    conn.setRequestProperty("Content-Type", this.contentType);
                }
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(cmd.getOptionValue("d"));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("서버로 데이터를 전송하던 중 IOException 발생");
                System.exit(1);
            }
        }

        if (cmd.hasOption("F")) {
            String boundary = "^--^";
            String crlf = "\r\n";
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            String path = cmd.getOptionValue("F");
            path = path.substring(path.indexOf("@") + 1);
            try {
                conn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
                File file = new File(path);
                writer.append("--").append(boundary).append(crlf);
                writer.append("Content-Disposition: form-data; name=\"" + file.getName() + "\"; filename =\"")
                        .append(file.getName()).append("\"").append(crlf);
                writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append(crlf);
                writer.append("Content-Transfer-Encoding: binary").append(crlf);
                writer.append(crlf);
                writer.flush();

                Files.copy(file.toPath(), conn.getOutputStream());
                conn.getOutputStream().flush();
                writer.append(crlf).flush();

                writer.append("--").append(boundary).append("--").append(crlf).flush();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void searchRedirectURL() {
        if (cmd.hasOption("L")) {
            try {
                if (currentRedirectCount >= maxRedirectCount) {
                    throw new IllegalArgumentException("Redirection 횟수 초과");
                }
                if (conn.getResponseCode() >= 300 && conn.getResponseCode() < 400) {
                    String url = "http://".concat(
                            conn.getURL().getAuthority().concat(conn.getHeaderField("Location")));
                    System.out.println("new URL : " + url);
                    URL newURL = new URL(url);
                    this.conn = (HttpURLConnection) newURL.openConnection();
                    this.conn.setInstanceFollowRedirects(false);
                    currentRedirectCount++;
                    this.conn.connect();
                    searchRedirectURL();
                } else {
                    printDataFromServer(conn);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        } else {
            printDataFromServer(conn);
        }
    }
}