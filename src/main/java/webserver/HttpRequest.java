package webserver;

import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.parseHeader;
import static util.HttpRequestUtils.parseQueryString;
import static util.IOUtils.readData;

public class HttpRequest {
    private String method = "";
    private String url = "";
    private Map<String,String> headers = new HashMap<>();
    private Map<String,String> queryString = new HashMap<>();
    private String body = "";

    private BufferedReader br;
    public HttpRequest(BufferedReader br) {
        this.br = br;
    }

    public boolean parse(){
        try {
            String line = br.readLine();
            if (!parseStartLine(line)) {
                return false;
            }

            parseHeaders();

            String value = header("Content-Length");
            if (value != null) {
                int length = Integer.parseInt(value);
                body = readData(br, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void parseHeaders() throws IOException {
        String line;
        while ((line = br.readLine() ) != null && !"".equals(line)) {
            HttpRequestUtils.Pair pair = parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
        }
    }

    private boolean parseStartLine(String line) {

        String[] lines = line.split(" ");
        if (lines.length != 3) {
            return false;
        }

        method = lines[0];

        int index = lines[1].indexOf("?");
        if (index <0){
            url = lines[1];
            return true;
        }
        url = lines[1].substring(0, index);
        queryString = parseQueryString(lines[1].substring(index+1));
        return true;
    }

    public String header(String key) {
        return headers.get(key);
    }

    public String getUrl() {
        return url;
    }

    public String queryString(String key) {
        return queryString.get(key);
    }

    public String getMethod(){
        return method;
    }

    public String getBody() {
        return body;
    }
}
