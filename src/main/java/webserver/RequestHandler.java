package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            //1. Socket에서 읽은 데이터를 한 줄씩 파싱한다.
            List<String> eachLine = parseEachLine(in);
            if(!eachLine.isEmpty()) {

                byte[] body = "Hi EveryBody".getBytes();

                //2. 첫번째 줄에서 URL을 파싱한다.
                String url = getURL(eachLine.get(0));
                log.debug("getURL : {}", url);
                if (url != null && url.equals("/index.html")) {

                    //3. 추출한 URL 경로의 파일을 읽는다.
                    body = ReadFileToByteFromUrl(url);
                }

                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 입력 받은 InputStream을 한 줄씩 파싱한다.
     * @param in
     * @return
     */
    private List<String> parseEachLine(InputStream in){
        List<String> httpHeaders = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = bufferedReader.readLine();
            while(!"".equals(line)){
                if ( line == null ){
                    log.debug("read line is null");
                    break;
                }
                log.debug(line);
                httpHeaders.add(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpHeaders;
    }

    /**
     * 첫번째 라인에서 URL을 추출한다.
     * @param line
     * @return
     */
    private String getURL(String line) {
        String[] startLine = line.split(" ");
        if (startLine.length != 3) {
            log.error("Invalid Start Line : {}", line);
            return null;
        }
        return startLine[1];
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] ReadFileToByteFromUrl(String url) throws IOException {
        return Files.readAllBytes(new File("./webapp"+url).toPath());
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
