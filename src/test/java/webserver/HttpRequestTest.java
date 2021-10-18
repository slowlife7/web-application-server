package webserver;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.*;

public class HttpRequestTest extends TestCase {
    @Test
    public void testParse_header_성공() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("GET /index.html HTTP/1.1\r\n");
        stringBuffer.append("Host: localhost:8080\r\n");
        stringBuffer.append("Connection: keep-alive\r\n");
        stringBuffer.append("Accept: */*\r\n");
        stringBuffer.append("\r\n");

        byte[] bytes = stringBuffer.toString().getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputStreamReader ir = new InputStreamReader(inputStream);

        HttpRequest httpRequest = new HttpRequest(new BufferedReader(ir));
        assertTrue(httpRequest.parse());
        assertEquals(httpRequest.header("Connection"), "keep-alive");
    }
    @Test
    public void testParse_StartLine_성공() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("GET /index.html HTTP/1.1\r\n");

        byte[] bytes = stringBuffer.toString().getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputStreamReader ir = new InputStreamReader(inputStream);

        HttpRequest httpRequest = new HttpRequest(new BufferedReader(ir));

        assertTrue(httpRequest.parse());
    }

    @Test
    public void testParse_StartLine_실패() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("/index.html HTTP/1.1\r\n");

        byte[] bytes = stringBuffer.toString().getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputStreamReader ir = new InputStreamReader(inputStream);

        HttpRequest httpRequest = new HttpRequest(new BufferedReader(ir));

        assertFalse(httpRequest.parse());
    }

}
