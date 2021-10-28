package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.RequestHandler;

public abstract class AbstractController implements Controller{
    public void service(HttpRequest request, HttpResponse response) throws Exception{
        if("GET".equals(request.getMethod())) {
            doGet(request, response);
        } else if("POST".equals(request.getMethod())) {
            doPost(request, response);
        }
    }
    public abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;
    public abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;
}
