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
    private Map<String, String> headers = new HashMap<>();
    private DataOutputStream dos;
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    public HttpResponse(OutputStream dos) {
        this.dos = new DataOutputStream(dos);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void responseHeader(int status, String des) {
        try {
            dos.writeBytes("HTTP/1.1 " + status + " " + des + "\r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processHeaders(){
        headers.keySet().stream().forEach( key -> {
            try {
                dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }

    private void responseBody(byte[] body) throws Exception {
        dos.write(body, 0, body.length);
        dos.writeBytes("\r\n");
        dos.flush();
    }

    public void forward(String url) throws Exception{
        byte[] body = ReadFileToByteFromUrl(url);
        if(url.endsWith(".css")) {
            addHeader("Content-Type", "text/css");
        } else if(url.endsWith(".js")) {
            addHeader("Content-Type", "application/javascript");
        } else {
            addHeader("Content-Type", "text/html;charset=utf-8");
        }

        addHeader("Content-Length", String.valueOf(body.length));
        responseHeader(200, "ok");
        responseBody(body);
    }

    public void forwardBody(byte[] body) throws Exception{
        addHeader("Content-Length", String.valueOf(body.length));
        responseHeader(200, "ok");
        responseBody(body);
    }

    public void sendRedirect(String fileName) throws Exception {
        addHeader("Content-Type", "text/html");
        addHeader("Location", fileName);
        responseHeader(302, "found");
    }
}
