package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.*;
import static util.IOUtils.readData;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private Map<String,String> headers = new HashMap<>();
    private Map<String,String> queryString = new HashMap<>();
    private Map<String,String> cookies = new HashMap<>();
    private String body = "";
    private RequestLine requestLine;

    private BufferedReader br;
    public HttpRequest(InputStream in)
    {
        this.br = new BufferedReader(new InputStreamReader(in));
        parse();
    }

    private boolean parse(){
        try {

            String line = br.readLine();
            requestLine = new RequestLine(line);

            log.info(line);
            parseHeaders();

            if(headers.containsKey("Cookie")) {
                cookies = parseCookies(headers.get("Cookie"));
            }

            if(getMethod().isPost()) {
                String body = readData(br, Integer.parseInt(getHeader("Content-Length")));
                queryString = parseQueryString(body);
            } else {
                queryString = requestLine.getParams();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void parseHeaders() throws IOException {
        String line;
        while ((line = br.readLine() ) != null && !"".equals(line)) {
            log.info(line);
            HttpRequestUtils.Pair pair = parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
        }
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(String key) {
        return queryString.get(key);
    }

    public HttpMethod getMethod(){
        return requestLine.getMethod();
    }

    public String getBody() {
        return body;
    }

    public String getCookies(String key) { return cookies.get(key);}
}
