package webserver;

import junit.framework.TestCase;
import model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.HttpRequestUtils;

import static org.junit.Assert.*;


import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 요구사항 1. index.html 응답하기
 * - 입력 Http Header
 * GET /index.html HTTP/1.1
 * Host: localhost:8080
 * Connection: keep-alive
 * Accept:
 *
 * 1. InputStream을 읽어서 요청 정보를 파싱한다.
 * 1.1 헤더 마지막은 while (!"".equals(line)) {} 로 확인 가능하다.
 * 1.2 line이 null 값인 경우에 대한 예외 처리도 해야 한다. 그렇지 않을 경우 무한 루프에 빠진다. ( if (line == null){ return ;}
 * 1.3 Http 요청 정보 첫 번째 라인에서 요청 URL을 추출한다.
 * 2. 추출한 요청 URL에 해당 하는 파일을 webapp 디렉토리에서 읽어 응답으로 전달한다.
 * 2.1 byte[] body = Files.readAllBytes(new File("./webapp"+ url).toPath());
 */

public class RequestHandlerTest extends TestCase {

    /**
     *
     * @param in Socket으로부터 읽어들인 InputStream 값
     * @return 각 라인별로 파싱한 결과
     */
    private List<String> parseEachLine(InputStream in){
        List<String> httpHeaders = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = bufferedReader.readLine();
            while(!"".equals(line)){
                if ( line == null ){
                    System.out.println("line value is null");
                    break;
                }
                System.out.println(line);
                httpHeaders.add(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpHeaders;
    }

    private String getURL(String line) {
        String[] startLine = line.split(" ");
        if (startLine.length != 3) {
            return null;
        }

        return startLine[1];
    }

    @Test
    public void testGetURL() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("GET /index.html HTTP/1.1\r\n");
        stringBuffer.append("Host: localhost:8080\r\n");
        stringBuffer.append("Connection: keep-alive\r\n");
        stringBuffer.append("Accept: */*\r\n");
        stringBuffer.append("\r\n");

        byte[] bytes = stringBuffer.toString().getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);

        List<String> headers = parseEachLine(inputStream);
        String url = getURL(headers.get(0));

        assertEquals("/index.html", url);
    }

    @Test
    public void testReadFileToByteFromUrl() {

        String url = "./webapp/index.html";

        Path path = Paths.get(url);
        try {
            long bytes = Files.size(path);
            byte[] readBytes = ReadFileToByteFromUrl("/index.html");
            assertNotEquals(0, bytes);
            assertEquals(bytes, readBytes.length);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] ReadFileToByteFromUrl(String url) throws IOException {
        return Files.readAllBytes(new File("./webapp"+url).toPath());
    }

    /**
     * GET /index.html HTTP/1.1
     *  * Host: localhost:8080
     *  * Connection: keep-alive
     *  * Accept:
     */
    @Test
    public void testParseHttpRequest_Success() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("GET /index.html HTTP/1.1\r\n");
        stringBuffer.append("Host: localhost:8080\r\n");
        stringBuffer.append("Connection: keep-alive\r\n");
        stringBuffer.append("Accept: */*\r\n");
        stringBuffer.append("\r\n");

        byte[] bytes = stringBuffer.toString().getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);

        List<String> headers = parseEachLine(inputStream);
        Assert.assertEquals(4, headers.size());
    }

    @Test
    public void testGetQueryStringFromURL() {
        String requestURL = "/user/create?userId=billy&password=234&name=herry&email=test%40test.com";

        Map<String, String> params = getQueryStringFromURL(requestURL);

        assertEquals("billy", params.get("userId"));
        assertEquals("234", params.get("password"));
        assertEquals("herry", params.get("name"));

        String requestPath = getRequestPath(requestURL);
        assertEquals("/user/create", requestPath);
    }

    @Test
    public void testGetRequestPath(){
        String requestPath = getRequestPath("/index.html");
        System.out.println("requestPath = " + requestPath);
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