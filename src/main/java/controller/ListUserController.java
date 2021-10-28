package controller;

import db.DataBase;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static util.HttpRequestUtils.parseCookies;

public class ListUserController extends AbstractController{
    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String cookieHeader = request.getHeader("Cookie");
        if(!isLogined(cookieHeader)) {
            response.sendRedirect("/user/login.html");
        }
        response.responseBody(getUserList().getBytes(StandardCharsets.UTF_8));
    }

    private boolean isLogined(String line) {
        Map<String, String> cookie = parseCookies(line);
        String value = cookie.get("logined");
        if(value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    private String getUserList() {
        StringBuilder ul = new StringBuilder();
        ul.append("<ul>");

        DataBase.findAll().forEach(u -> {
            StringBuilder li = new StringBuilder("<li></li>");
            li.insert(4, u.getUserId());
            ul.append(li);
        });
        ul.append("</ul>");
        return ul.toString();
    }
}
