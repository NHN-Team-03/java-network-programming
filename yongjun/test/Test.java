package com.nhnacademy.yongjun.test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {
    public static void main(String[] args) {
        int maxRedirects = 5; // 최대 리다이렉션 횟수 설정
        String baseUrl = "http://httpbin.org/status/302";
        String url = baseUrl;
        int redirects = 0;

        try {
            while (redirects <= maxRedirects) {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setInstanceFollowRedirects(false);

                int responseCode = connection.getResponseCode();
                System.out.println(responseCode + "@@@@@@@@@@@@@");

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 200 OK 상태 코드인 경우 데이터 읽기
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    System.out.println("Response Data:");
                    System.out.println(response.toString());
                    break; // 리다이렉션 종료

                } else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                        responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                        responseCode == HttpURLConnection.HTTP_SEE_OTHER ||
                        responseCode == 307 || responseCode == 308) {
                    // 리다이렉션 발생
                    String newUrl = connection.getHeaderField("Location");
                    System.out.println("Redirected to: " + newUrl);

                    if (newUrl == null) {
                        System.out.println("Redirect URL not found.");
                        break;
                    }

                    // 상대 경로를 절대 경로로 변환
                    url = new URL(new URL(baseUrl), newUrl).toString();
                    redirects++;
                } else {
                    // 다른 상태 코드 처리
                    System.out.println("HTTP Request failed. Response Code: " + responseCode);
                    break;
                }
            }

            if (redirects > maxRedirects) {
                System.out.println("Maximum redirections reached.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
