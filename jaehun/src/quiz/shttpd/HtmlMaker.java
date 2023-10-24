package quiz.shttpd;

public class HtmlMaker {
    private final StringBuilder stringBuilder;

    public HtmlMaker() {
        this.stringBuilder = new StringBuilder();
        stringBuilder.append("<!DOCTYPE html>");
        stringBuilder.append("<html>");
        stringBuilder.append("   <head>");
        stringBuilder.append("       <meta charset=\"UTF-8\">");
        stringBuilder.append("       <meta name=\"author\" content=\"Jaehun\">");
        stringBuilder.append("       <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        stringBuilder.append("       <title>Simple HTTP Directory</title>");
        stringBuilder.append("   </head>");;
    }

    public void writeBody(String message) {
        stringBuilder.append("   <body>");
        stringBuilder.append(message);
        stringBuilder.append("   </body>");
        stringBuilder.append("</html>");
    }

    public StringBuilder getHtml() {
        return stringBuilder;
    }

}
