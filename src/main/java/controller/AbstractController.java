package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpMethod;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.RequestHandler;

public abstract class AbstractController implements Controller{
    public void service(HttpRequest request, HttpResponse response) throws Exception{
        HttpMethod method = request.getMethod();
        if(method.isPost()) {
            doPost(request, response);
        } else {
            doGet(request, response);
        }
    }
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    };
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
