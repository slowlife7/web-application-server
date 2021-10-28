package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class CreateUserConroller extends AbstractController{

    @Override
    public void doPost(HttpRequest request, HttpResponse response)  throws Exception{
        User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), "");
        DataBase.addUser(user);
        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
