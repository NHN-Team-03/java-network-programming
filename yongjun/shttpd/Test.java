package com.nhnacademy.yongjun.shttpd;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author emunhi
 *
 */
public class Test {

    public static void mainxx(String[] args) {
        String boundary = Long.toHexString(System.currentTimeMillis());
        System.out.println(boundary);
    }

    public static void main(String[] args) throws Exception {

        // 업로드할 서버 URL
        String url = "localhost:8000/upload";
        String charset = "UTF-8";

        File textFile = new File("/Users/jun/Documents/Text.txt/");
        File binaryFile = new File("/Users/jun/Documents/Test.apk/");

        // Unique한 값 설정 (boundary를 위한 값)
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //System.out.println("001 :" + sdf.format(new Timestamp(System.currentTimeMillis())));

        /**=======================================================================================
         * URLConnection.openConnection() 실행전에 아래 실행하면
         * SSL인증에대한 ignore  
         *=======================================================================================*/
        trustSSL();
        /**======================================================================================*/

        URLConnection connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("User-Agent", "curl/7.74.0");

        try (OutputStream output = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);) {

            // Send normal parameter.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"param1\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append("sddfasdfasd");
            writer.append(CRLF).flush(); // ※중요※ CRLF 는 boundary의 끝을 의미.

            // Send JSON
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"jsonData\"").append(CRLF);
            writer.append("Content-Type: application/json; charset=" + charset).append(CRLF);
            writer.append(CRLF).append("{\"app_type\":\"001\"}");
            writer.append(CRLF).flush(); // ※중요※ CRLF 는 boundary의 끝을 의미.

            // Send text file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"textFile\"; filename=\"" + textFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(textFile.toPath(), output);
            output.flush(); // ※중요 반드시 flush()
            writer.append(CRLF).flush(); // ※중요※ CRLF 는 boundary의 끝을 의미.

            // Send binary file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: application/octet-stream").append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(binaryFile.toPath(), output);
            output.flush(); // ※중요 반드시 flush()
            writer.append(CRLF).flush(); // ※중요※ CRLF 는 boundary의 끝을 의미.

            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush(); // ※중요※ CRLF 는 boundary의 끝을 의미.
        }

        // 반드시 reponse결과값을 취득해애 연결이 종료된다.( 안하면 파일업로드 실패)
        int responseCode = ((HttpURLConnection) connection).getResponseCode();

        // 종료 후 결과값을 수신처리시 읽어처리
        BufferedReader br = null;
        if (responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(((HttpURLConnection) connection).getInputStream()));
            String strCurrentLine;
            while ((strCurrentLine = br.readLine()) != null) {
                System.out.println(strCurrentLine);
            }
        } else {
            br = new BufferedReader(new InputStreamReader(((HttpURLConnection) connection).getErrorStream()));
            String strCurrentLine;
            while ((strCurrentLine = br.readLine()) != null) {
                System.out.println(strCurrentLine);
            }
        }

    }

    /**
     * SSL 인증서에 대해 모두 신뢰하도록 처리를 한다.
     */
    public static void trustSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                        }
                        @Override
                        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                        }
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                            //return null;
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}