package webserver;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.*;

import static junit.framework.TestCase.assertEquals;

public class HttpRequestTest {

    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_POST() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory +
                "Http_POST.http"));

        HttpRequest request = new HttpRequest(in);

        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("javajigi", request.getParameter("userId"));
    }

    @Test
    public void request_GET() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory +
                "Http_GET.http"));

        HttpRequest request = new HttpRequest(in);

        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("javajigi", request.getParameter("userId"));
    }

    @Test
    public void request_GET_Cookie() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory +
                "Http_GET_Cookie.http"));

        HttpRequest request = new HttpRequest(in);

        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/index.html", request.getPath());

        assertEquals(true, Boolean.parseBoolean(request.getCookies("logined")));
        assertEquals("/",request.getCookies("path"));
    }


}
