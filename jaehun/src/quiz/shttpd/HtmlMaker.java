package quiz.shttpd;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HtmlMaker {
    private StringBuilder sb;

    public HtmlMaker() {
        this.sb = new StringBuilder();

        writeHeader();
    }

    public void writeHeader() {
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("   <head>");
        sb.append("       <meta charset=\"UTF-8\">");
        sb.append("       <meta name=\"author\" content=\"Jaehun\">");
        sb.append("       <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        sb.append("       <title>Simple HTTP Directory</title>");
        sb.append("   </head>");
    }

    public void writeBody(String message) {
        sb.append("   <body>");
        sb.append(message);
        sb.append("   </body>");
        sb.append("</html>");
    }

    public StringBuilder getHtml() {
        return sb;
    }

    public void clear() {
        sb.delete(0, sb.length()-1);

        writeHeader();
    }

}
