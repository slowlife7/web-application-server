package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginController extends AbstractController{

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {

        User userId = DataBase.findUserById(request.getParameter("userId"));
        if (userId == null || !userId
                .getPassword().
                equals(request.getParameter("password"))) {
            response.addHeader("Set-Cookie", "logined=false; Path=/");
            response.sendRedirect("/user/login_failed.html");
            //dos.end();
            return;
        }

        response.addHeader("Set-Cookie", "logined=true; Path=/");
        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
