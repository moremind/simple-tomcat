package cn.moremind.io.tomcat.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author
 * @description
 * @see
 * @since
 */
public class MResponse {

    private OutputStream out;
    public MResponse(OutputStream outputStream) {
        this.out = outputStream;
    }

    public void write(String msg) throws IOException {
        // 给一个状态码
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\n")
                .append("Content-Type: text/html;\n")
                .append("\r\n")
                .append(msg);

        out.write(sb.toString().getBytes());
    }
}
