package com.nhnacadmemy.shttpd;

import static com.nhnacadmemy.shttpd.FileList.findFile;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class GetHandler implements HttpHandler {

    /**
     * GET 요청을 처리한다.
     */

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String fileName = exchange.getRequestURI().getPath();

        // TODO: /file-path인 경우
        if (!(fileName == null || fileName.equals("/") || fileName.equals("/favicon.ico"))) {
            fileName = fileName.substring(1);
            File file = findFile(fileName);
            if (file == null || !file.exists()) {
                // TODO: 파일이 존재하지 않는 경우 -> 404 HTTP_Not_found
                Response.send(exchange, HttpURLConnection.HTTP_NOT_FOUND, fileNotFound());

            } else if (!file.canRead()) {
                file.canRead();
                // TODO: 파일이 존재하지만 읽을 수 없는 경우 -> 403 HTTP_Forbidden
                Response.send(exchange, HttpURLConnection.HTTP_FORBIDDEN, fileCantRead());
            } else {
                // TODO: 파일이 존재하는 경우 -> 200 HTTP_OK
                Response.send(exchange, HttpURLConnection.HTTP_OK, fileFound(file));
            }
        } else {

            // TODO: 경로를 지정하지 않은 경우 파일 리스트를 출력 -> 200 HTTP_OK
            Response.send(exchange, HttpURLConnection.HTTP_OK, getFileList());
        }
    }


    public static void writeFileList(StringBuilder sb) {
        List<String> fileList = FileList.getFileLists();

        sb.append("         <span> ======== FILE LIST ========</span>");
        for (String file : fileList) {
            sb.append("         <ul>").append(file).append("</ul>");
        }
        sb.append("         <span> ===========================</span>");
    }

    private static StringBuilder getFileList() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"seungjo\">");
        sb.append("       <title>Seungjo's shttpd</title>");
        sb.append("   </head>");

        sb.append("   <body>");
        sb.append("       <h3>File List </h3>");
        writeFileList(sb);
        sb.append("   </body>");
        sb.append("</html>");
        return sb;
    }

    private StringBuilder fileFound(File file) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"seungjo\">");
        sb.append("       <title>Seungjo's shttpd</title>");
        sb.append("   </head>");

        sb.append("   <body>");
        sb.append("       <h3> 파일의 내용 </h3>");
        readFile(file, sb);
        sb.append("   </body>");
        sb.append("</html>");

        return sb;
    }

    private void readFile(File file, StringBuilder sb) {

        try {
            FileReader fr = new FileReader(file);

            String line;
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                sb.append("     <ul>").append(line).append("</ul>");
            }

            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder fileNotFound() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"seungjo\">");
        sb.append("       <title>Seungjo's shttpd</title>");
        sb.append("   </head>");
        sb.append("   <body>");
        sb.append("       <h3> 해당 파일을 찾을 수 없습니다! </h3>");
        sb.append("   </body>");
        sb.append("</html>");

        return sb;
    }

    private StringBuilder fileCantRead() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"seungjo\">");
        sb.append("       <title>Seungjo's shttpd</title>");
        sb.append("   </head>");
        sb.append("   <body>");
        sb.append("       <h3> 해당 파일을 읽을 수 없습니다! </h3>");
        sb.append("   </body>");
        sb.append("</html>");

        return sb;
    }




}
