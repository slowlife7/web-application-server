package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {

    private DataOutputStream dos;
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void end(byte[] body) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
