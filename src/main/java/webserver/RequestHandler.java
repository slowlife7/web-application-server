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

            routeMap.put("/user/login", new LoginController());
            routeMap.put("/user/list", new ListUserController());
            routeMap.put("/user/create", new CreateUserConroller());

            Controller controller = routeMap.get(request.getPath());
            if(controller == null) {
                String path = request.getPath();
                log.info("no exist controller");
                log.info("path : {}",path);

                response.addHeader("Accept", "text/css, */*;q=0.1");
                response.forward(path);

                return;
            }
            controller.service(request,response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
