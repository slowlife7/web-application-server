package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class IOUtils {
    /**
     * @param BufferedReader는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    /**
     * Url을 입력받아 지정된 경로의 파일을 Byte형태로 읽는다.
     * @param url
     * @return
     * @throws IOException
     */
    public static byte[] ReadFileToByteFromUrl(String url) throws IOException {
        return Files.readAllBytes(new File("./webapp"+url).toPath());
    }
}
