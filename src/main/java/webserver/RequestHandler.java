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
                getRoute(url, new DataOutputStream(out));
                return;
            }

            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            postRoute(url, new DataOutputStream(out), readData(br, contentLength));
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

    private void getRoute(String url, DataOutputStream dos) throws IOException {
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
                end(dos, "".getBytes(StandardCharsets.UTF_8));
                break;
            default:
                end(dos,"Hello wolrd".getBytes(StandardCharsets.UTF_8));
        }
    }

    private void postRoute(String url, DataOutputStream dos, String body) throws IOException {
        String requestPath = parseRequestPath(url);
        switch (requestPath) {
            case "/user/create":
                Map<String, String> params = parseQueryString(body);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), "");
                log.info("success /user/create : {}", user);
                response302Header(dos, "/index.html");
                break;
            default:
                end(dos,"Hello wolrd".getBytes(StandardCharsets.UTF_8));
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
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
