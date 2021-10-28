package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import controller.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private Map<String, Controller> routeMap = new HashMap<>();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            Controller controller = RequestMapping.getController(request.getPath());
            if(controller == null) {
                String path = getDefaultPath(request.getPath());
                log.info("no exist controller");
                log.info("path : {}",path);
                response.forward(path);
            } else {
                controller.service(request, response);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDefaultPath(String path) {
        if(path.equals("/")){
            return "/index.html";
        }
        return path;
    }
}
