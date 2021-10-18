package webserver;

import junit.framework.TestCase;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class HttpResponseTest extends TestCase {


    @Test
    public void testSetStatusLine(){
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusLine(200, "OK");

        String request = httpResponse.write();

        assertEquals("HTTP/1.1 200 OK\r\n", request);
    }

    @Test
    public void testSetHeader() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHeader("Content-Length", "1");
        httpResponse.setHeader("Location", "http://localhost/indx.html");

        String request = httpResponse.write();
        assertEquals("Content-Length: 1\r\nLocation: http://localhost/indx.html\r\n", request);
    }

    @Test
    public void testSetBody() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHeader("Location", "http://localhost/indx.html");

        httpResponse.setBody("hello!!".getBytes(StandardCharsets.UTF_8));

        String request = httpResponse.write();
        assertEquals("Content-Length: 7\r\nLocation: http://localhost/indx.html\r\n\r\nhello!!", request);
    }

}