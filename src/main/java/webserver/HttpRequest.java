package webserver;

import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private int result = 0;
    private String httpMethod = "";
    private String requestPath = "";
    private Map<String, String> queryString = new HashMap<>();

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public boolean parseHttpRequest(InputStream in){

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = bufferedReader.readLine();
            if (line == null) {
                return false;
            }

            String url = getURL(line);
            if (url == null) {
                System.out.println("url = " + url);
                return false;
            }

            requestPath = getRequestPath(url);
            queryString = getQueryStringFromURL(url);

            while(!"".equals(line)){
                if ( line == null ){
                    return true;
                }
                line = bufferedReader.readLine();
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("HttpRequest.parseHttpRequest");
        return false;
    }

    private String getURL(String line) {
        String[] startLine = line.split(" ");
        if (startLine.length != 3) {
            //log.error("Invalid Start Line : {}", line);
            return null;
        }
        return startLine[1];
    }

    private  String getRequestPath(String url) {
        int index = url.indexOf("?");
        if (index < 0) {
            return url;
        }
        return url.substring(0, index);
    }

    private Map<String, String> getQueryStringFromURL(String requestURL) {
        int index = requestURL.indexOf("?");
        String queryString = requestURL.substring(index+1);
        return HttpRequestUtils.parseQueryString(queryString);
    }
}
