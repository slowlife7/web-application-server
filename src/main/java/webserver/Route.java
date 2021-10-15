package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Route {
    private static Map<String, BiFunction<HttpRequest, HttpResponse, Integer>> route = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    public static void init() {

        route.put("/index.html", (HttpRequest httpRequest, HttpResponse httpResponse) -> {
            try {
                httpResponse.end(IOUtils.ReadFileToByteFromUrl(httpRequest.getRequestPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        });

        route.put("/user/form.html", (HttpRequest httpRequest, HttpResponse httpResponse) -> {
            try {
                httpResponse.end(IOUtils.ReadFileToByteFromUrl(httpRequest.getRequestPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        });

        route.put("/user/create", (HttpRequest httpRequest, HttpResponse httpResponse) -> {

            Map<String, String> queryString = httpRequest.getQueryString();
            User user = new User(queryString.get("userId"), queryString.get("password"), queryString.get("name"), "");

            DataBase.addUser(user);

            httpResponse.end("회원가입 성공".getBytes(StandardCharsets.UTF_8));

            return 0;
        });

        route.put("/", (HttpRequest httpRequest, HttpResponse httpResponse) -> {
            httpResponse.end("Hello wolrd".getBytes(StandardCharsets.UTF_8));
            return 0;
        });
    }

    public static int get(HttpRequest httpRequest, HttpResponse httpResponse) {
        BiFunction<HttpRequest, HttpResponse, Integer> function = route.get(httpRequest.getRequestPath());
        if (function!=null) {
            return route.get(httpRequest.getRequestPath()).apply(httpRequest, httpResponse);
        }
        return 1;
    }
}
