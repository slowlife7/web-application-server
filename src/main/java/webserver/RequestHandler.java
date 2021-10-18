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

            HttpRequest httpRequest = new HttpRequest(new BufferedReader(new InputStreamReader(in)));

            if (!httpRequest.parse()) {
                log.error("fail to parse");
                return;
            }

            HttpResponse httpResponse = new HttpResponse(new DataOutputStream(out));

            routeMethod(httpRequest, httpResponse);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void routeMethod(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        switch (httpRequest.getMethod()) {
            case "GET":
                getRoute(httpRequest, httpResponse);
                break;
            case "POST":
                postRoute(httpRequest, httpResponse);
        }
    }

    private void getRoute(HttpRequest httpRequest, HttpResponse response) throws IOException {

        switch (httpRequest.getUrl()) {
            case "/index.html":
            case "/user/form.html":
            case "/user/login.html":
            case "/user/login_failed.html":
                response.setStatusLine(200, "Ok");
                response.setHeader("Content-Type", "text/html;charset=utf-8");
                response.setBody(IOUtils.ReadFileToByteFromUrl(httpRequest.getUrl()));
                response.end();
                break;

            case "/user/create":
                User user = new User(httpRequest.queryString("userId"), httpRequest.queryString("password"), httpRequest.queryString("name"), "");
                log.info("success /user/create : {}", user);
                response.setStatusLine(200, "Ok");
                response.setHeader("Content-Type", "text/html;charset=utf-8");
                response.end();
                break;
            default:
                response.setStatusLine(200, "Ok");
                response.setHeader("Content-Type", "text/html;charset=utf-8");
                response.setBody("Hello wolrd".getBytes(StandardCharsets.UTF_8));
                response.end();
        }
    }

    private void postRoute(HttpRequest httpRequest, HttpResponse dos) throws IOException {
        String body = "";
        Map<String, String> bodyData;
        switch (httpRequest.getUrl()) {
            case "/user/create":
                body = httpRequest.getBody();
                bodyData = parseQueryString(body);

                User user = new User(bodyData.get("userId"), bodyData.get("password"), bodyData.get("name"), "");
                log.info("success /user/create : {}", user);
                DataBase.addUser(user);
                dos.setStatusLine(302, "Found");
                dos.setHeader("Content-Type", "text/html;charset=utf-8");
                dos.setHeader("Location", "/index.html");
                dos.end();
                break;

            case "/user/login":
                body = httpRequest.getBody();
                bodyData = parseQueryString(body);

                User userId = DataBase.findUserById(bodyData.get("userId"));
                log.info("/user/login : {}", userId);
                if (userId == null || !userId
                        .getPassword().
                        equals(bodyData.get("password"))) {
                    dos.setStatusLine(302, "Found");
                    dos.setHeader("Content-Type", "text/html;charset=utf-8");
                    dos.setHeader("Location", "/user/login_failed.html");
                    dos.setHeader("Set-Cookie", "logined=false; Path=/");
                    dos.end();
                    break;
                }

                dos.setStatusLine(302, "Found");
                dos.setHeader("Content-Type", "text/html;charset=utf-8");
                dos.setHeader("Location", "/index.html");
                dos.setHeader("Set-Cookie", "logined=true; Path=/");
                dos.end();
                break;
            default:
                dos.setBody("Hello World".getBytes(StandardCharsets.UTF_8));
        }
    }
}
