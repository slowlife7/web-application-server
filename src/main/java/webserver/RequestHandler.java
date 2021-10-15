package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import static util.HttpRequestUtils.*;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();

            //1. 첫번째 라인에서 Url 추출
            String url = parseUrl(line);
            if (url == null) {
                log.error("fail to parse Url : {}", url);
                return;
            }

            //2. url에 따라 분기 처리
            routePath(url, new DataOutputStream(out));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void routePath(String url, DataOutputStream dos) throws IOException {
        String requestPath = parseRequestPath(url);
        switch (requestPath) {
            case "/index.html":
            case "/user/form.html":
                end(dos, IOUtils.ReadFileToByteFromUrl(requestPath));
                break;
            case "/user/create":
                Map<String, String> params = parseQueryString(url);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), "");
                log.info("success /user/create : {}", user);
                end(dos, "회원가입 성공".getBytes(StandardCharsets.UTF_8));
                break;
            default:
                end(dos,"Hello wolrd".getBytes(StandardCharsets.UTF_8));
        }
    }

    public void end(DataOutputStream dos, byte[] body) throws IOException{
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
