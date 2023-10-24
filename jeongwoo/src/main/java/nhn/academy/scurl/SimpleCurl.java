package nhn.academy.scurl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

public class SimpleCurl {

    static StringBuilder stringBuilder = new StringBuilder();
    static URL url = null;
    static HttpURLConnection http = null;
    static String method = "GET";
    static String userAgent = "curl/8.1.2";
    static String accept = "*/*";
    static Options options = addOption();
    static CommandLineParser parser = new DefaultParser();
    static CommandLine cmd;
    static int requestCount = 0;


    public static void main(String[] args) {
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("X")) {
                method = cmd.getOptionValue("X");
            }

            String urlString = cmd.getArgs()[0];

            /*
            URL 생성자
            URL(String protocol, String host, int port, String file)
            URL(String protocol, String host, String file)
            URL(String urlString)
             */
            url = new URL(urlString);

            setHttp();
            InetAddress inetAddress = InetAddress.getByName(url.getHost());

            stringBuilder.append(
                    "*\tTrying " + inetAddress.getHostAddress() + ":" + url.getDefaultPort() + "...\n");
            stringBuilder.append(
                    "* Connected to " + url.getHost() + " port " + url.getDefaultPort() + " (#" + (requestCount) +
                            ")");

            if (cmd.hasOption("H")) {
                String requestHeader = cmd.getOptionValue("H");
                System.out.println(requestHeader);
                String[] requestHeaderList = requestHeader.split(" ");
                int length = requestHeaderList[0].length();
                requestHeaderList[0] = requestHeaderList[0].substring(0, length - 1);
                http.addRequestProperty(requestHeaderList[0], requestHeaderList[1]);
            }

            if (cmd.hasOption("d")) {
                http.setDoOutput(true); // 출력을 가능하게 하는 메서드
                OutputStream writer = http.getOutputStream();
                writer.write(new JSONObject(cmd.getOptionValue("d")).toString().getBytes());
            }

            //~$ scurl -F "upload=@file_path" http://httpbin.org/post
            if (cmd.hasOption("F")) {
                String boundary = "------------------------" + System.currentTimeMillis();
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                http.setDoOutput(true);
                http.setDoInput(true);
                http.setUseCaches(false);

                String filePath = cmd.getOptionValue("F");
                filePath = filePath.split("=")[1];

                System.out.println(filePath);

                DataOutputStream outputStream = new DataOutputStream(http.getOutputStream());
                outputStream.writeBytes("--" + boundary + "\r\n");
                outputStream.writeBytes(
                        "Content-Disposition: form-data; name=\"upload\"; filename=\"" + filePath + "\"\r\n");
                outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

                // 파일 내용을 전송
                FileInputStream fileInputStream = new FileInputStream(new File(filePath));
                int bytesRead;
                byte[] buffer = new byte[1024];
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                outputStream.writeBytes("\r\n");
                outputStream.writeBytes("--" + boundary + "--\r\n");
                outputStream.flush();
                outputStream.close();
            }

            if (cmd.hasOption("L")) {

                int maxCount = 5;
                int redirectCount = 0;
                while (redirectCount < maxCount) {
                    setHttp();
                    http.setInstanceFollowRedirects(false); // Disable automatic redirection
                    http.connect();
                    int responseCode = http.getResponseCode();
                    // System.out.println(responseCode);
                    storeRequestResponse();

                    if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                            responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                            responseCode == 307 || responseCode == 308) {

                        String location = http.getHeaderField("Location");
                        urlString = url.getProtocol() + "://" + url.getHost() + location;
                        // System.out.println(urlString);
                        url = new URL(urlString);

                        if (redirectCount > 0) {
                            stringBuilder.append("\n* Ignoring the response-body");
                        }
                        stringBuilder.append(
                                        "\n* Connection #" + requestCount + " to host " + url.getHost() + "left intact")
                                .append("\n* Issue another request to this URL: \'" + urlString + "\'")
                                .append("\n* Found bundle for host: " + InetAddress.getByName(url.getHost()) +
                                        " [serially]")
                                .append("\n* Can not multiplex, even if we wanted to")
                                .append("\n* Re-using exisiting connection #" + requestCount + " with host " +
                                        url.getHost());
                    }
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        break;
                    }
                    http.disconnect();
                    redirectCount++;
                }

                if (redirectCount == 5) {
                    throw new IOException("ERROR : MAX redirect값을 초과");
                }
            } else {
                storeRequestResponse();
            }


            if (cmd.hasOption("v")) {
                System.out.println(stringBuilder);

            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String message;
            while ((message = bufferedReader.readLine()) != null) {
                System.out.println(message);
            }

        } catch (ParseException e) {
            System.out.println("잘못된 인수");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setHttp() throws IOException {
        http = (HttpURLConnection) url.openConnection();

        http.setRequestProperty("User-Agent", userAgent);
        http.setRequestProperty("accept", accept);
        http.setRequestMethod(method);
    }

    static void storeRequestResponse() throws IOException {

        stringBuilder.append("\n> " + http.getRequestMethod() + " " + http.getURL().getPath() + " " +
                        http.getHeaderField(0).split(" ")[0])
                .append("\n> Host: " + url.getHost())
                .append("\n> User-Agent: " + http.getRequestProperty("User-Agent"))
                .append("\n> Accept: " + http.getRequestProperty("Accept"))
                .append("\n>");

        stringBuilder.append("\n< " + http.getHeaderField(0));

        for (int i = 1; i < http.getHeaderFields().size(); i++) {
            String key = http.getHeaderFieldKey(i);
            String value = http.getHeaderField(i);
            stringBuilder.append("\n< " + key + ": " + value);
        }

        stringBuilder.append("\n<");

    }


    static Options addOption() {
        Options options = new Options();

        Option verbose = new Option("v", "verbose, 요청, 응답 헤더를 출력합니다.");

        Option header = Option.builder("H")
                .argName("line")
                .hasArg()
                .desc("임의의 헤더를 서버로 전송합니다.")
                .build();

        Option data = Option.builder("d")
                .argName("data")
                .hasArg()
                .desc("POST, PUT 등에 데이타를 전송합니다.")
                .build();

        Option method = Option.builder("X")
                .argName("command")
                .hasArg()
                .desc("사용할 method 를 지정합니다. 지정되지 않은 경우 기본값은 GET 입니다.")
                .build();

        Option response = new Option("L", "서버의 응답이 30x 계열이면 다음 응답을 따라 갑니다.");

        Option fileName = Option.builder("F")
                .argName("name")
                .hasArg()
                .desc("multipart/form-data 를 구성하여 전송합니다. content 부분에 @filename 을 사용할 수 있습니다.")
                .build();

        options.addOption(verbose);
        options.addOption(header);
        options.addOption(data);
        options.addOption(method);
        options.addOption(response);
        options.addOption(fileName);

        return options;
    }
}
