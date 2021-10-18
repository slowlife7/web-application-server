package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import javax.xml.crypto.Data;

import static util.HttpRequestUtils.*;
import static util.IOUtils.readData;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            log.info(line);

            //1. 첫번째 라인에서 Url 추출
            String url = parseUrl(line);
            if (url == null) {
                log.error("fail to parse Url : {}", url);
                return;
            }

            //2. 헤더 파싱
            parseHeaders(br);

            //3. Content Length 체크
            String s = headers.get("Content-Length");
            if ( s==null) {
                getRoute(url, new HttpResponse(new DataOutputStream(out)));
                return;
            }

            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            postRoute(url, new HttpResponse(new DataOutputStream(out)), readData(br, contentLength));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseHeaders(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine() ) != null && !"".equals(line)) {
            log.info(line);
            Pair pair = parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
        }
    }

    private void getRoute(String url, HttpResponse response) throws IOException {
        String requestPath = parseRequestPath(url);
        switch (requestPath) {
            case "/index.html":
            case "/user/form.html":
            case "/user/login.html":
            case "/user/login_failed.html":
                response.setStatusLine(200, "Ok");
                response.setHeader("Content-Type", "text/html;charset=utf-8");
                response.setBody(IOUtils.ReadFileToByteFromUrl(requestPath));
                response.end();
                break;

            case "/user/create":
                Map<String, String> params = parseQueryString(url);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), "");
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

    private void postRoute(String url, HttpResponse dos, String body) throws IOException {
        String requestPath = parseRequestPath(url);
        Map<String, String> params = parseQueryString(body);

        switch (requestPath) {
            case "/user/create":
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), "");
                log.info("success /user/create : {}", user);
                DataBase.addUser(user);
                dos.setStatusLine(302, "Found");
                dos.setHeader("Content-Type", "text/html;charset=utf-8");
                dos.setHeader("Location", "/index.html");
                dos.end();
                break;

            case "/user/login":

                User userId = DataBase.findUserById(params.get("userId"));
                log.info("/user/login : {}", userId);
                if (userId == null || !userId
                        .getPassword().
                        equals(params.get("password"))) {
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
