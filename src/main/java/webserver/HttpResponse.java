package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {
    private StringBuilder sb = new StringBuilder();
    private String statusLine = "";
    private Map<String, String> headers = new HashMap<>();
    private byte[] body = null;
    private DataOutputStream dos;

    public HttpResponse() {

    }

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void setStatusLine(int status, String description) {
        statusLine = "HTTP/1.1 " + status + " " + description + "\r\n";
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(byte[] body) {
        setHeader("Content-Length", String.valueOf(body.length));
        this.body=body;
    }

    public void end(){
        try {
            dos.write(write().getBytes(StandardCharsets.UTF_8));
            if (body != null) {
                dos.write(body);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String write() {
        sb.append(statusLine);
        sb.append(headers.keySet().stream()
                .map(s -> s + ": " + headers.get(s) + "\r\n")
                .collect(Collectors.joining()));
        sb.append("\r\n");
        return sb.substring(0);
    }
}
