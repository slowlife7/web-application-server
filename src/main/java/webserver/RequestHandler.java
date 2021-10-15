package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        Route.init();
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

            HttpResponse httpResponse = new HttpResponse(new DataOutputStream(out));

            //2. Http Response를 생성한다.
            Route.get(httpRequest, httpResponse);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
