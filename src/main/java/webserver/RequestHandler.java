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
import sun.nio.ch.IOUtil;
import util.IOUtils;

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

            HttpRequest httpRequest = new HttpRequest();

            //1. HttpRequest 객체에게 InputStream 파싱을 위임한다.
            if (!httpRequest.parseHttpRequest(in)) {
                log.debug("Invalid Http Request");
                return ;
            }

            HttpResponse httpResponse = new HttpResponse();
            DataOutputStream dos = new DataOutputStream(out);

            //2. Http Response를 생성한다.
            switch(httpRequest.getRequestPath()) {
                case "/index.html":
                case "/user/form.html":
                    httpResponse.end(dos, IOUtils.ReadFileToByteFromUrl(httpRequest.getRequestPath()));
                    break;
                case "/user/create":
                    Map<String, String> queryString = httpRequest.getQueryString();
                    User user = new User(queryString.get("userId"), queryString.get("password"), queryString.get("name"), "");

                    log.debug("user : {}", user.toString());
                    DataBase.addUser(user);

                    httpResponse.end(dos, "회원가입 성공".getBytes(StandardCharsets.UTF_8));
                    break;
                default:
                    httpResponse.end(dos, "Hello wolrd".getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
