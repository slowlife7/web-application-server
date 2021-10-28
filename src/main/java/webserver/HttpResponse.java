package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static util.IOUtils.ReadFileToByteFromUrl;

public class HttpResponse {
    private StringBuilder sb = new StringBuilder();
    private String statusLine = "";
    private Map<String, String> headers = new HashMap<>();
    private OutputStream dos;
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    public HttpResponse(OutputStream dos) {
        this.dos = dos;
    }

    public void setStatusLine(int status, String description) {
        statusLine = "HTTP/1.1 " + status + " " + description + "\r\n";
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private byte[] processHeaders() {
        sb.append(statusLine);
        sb.append(headers.keySet().stream()
                .map(s -> s + ": " + headers.get(s) + "\r\n")
                .collect(Collectors.joining()));
        sb.append("\r\n");
        return sb.substring(0).getBytes(StandardCharsets.UTF_8);
    }

    public void responseBody(byte[] body) throws Exception {
        setStatusLine(200, "ok");
        addHeader("Content-Type", "text/html");
        addHeader("Content-Length", String.valueOf(body.length));

        dos.write(processHeaders());
        dos.write(body);
    }

    public void forward(String fileName) throws Exception{
        setStatusLine(200, "ok");
        //addHeader("Content-Type", "text/html");
        byte[] bytes = ReadFileToByteFromUrl(fileName);
        log.info("length: {}", bytes.length);
        addHeader("Content-Length", String.valueOf(bytes.length));

        dos.write(processHeaders());
        dos.write(bytes);
    }

    public void sendRedirect(String fileName) throws Exception {
        setStatusLine(302, "found");
        addHeader("Content-Type", "text/html");
        addHeader("Location", fileName);
        dos.write(processHeaders());
    }
}
