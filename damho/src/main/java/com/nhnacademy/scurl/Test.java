package com.nhnacademy.scurl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws IOException {
        URL url = new URL("http://httpbin.org/status/302");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setInstanceFollowRedirects(false);

        Map<String, List<String>> keys = conn.getHeaderFields();
        for (String key : keys.keySet()) {
            List<String> values = conn.getHeaderFields().get(key);
            for (String value : values) {
                System.out.println(key + " : " + value);
            }
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

    }
}
