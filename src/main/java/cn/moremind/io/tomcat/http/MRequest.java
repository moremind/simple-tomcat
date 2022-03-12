package cn.moremind.io.tomcat.http;

import java.io.InputStream;

/**
 * @author
 * @description
 * @see
 * @since
 */
public class MRequest {

    private String method;
    private String url;

    public MRequest(InputStream inputStream) {
        try {
            String content = "";
            byte[] buf = new byte[1024];
            int len = 0;

            if ((len = inputStream.read(buf)) > 0) {
                content = new String(buf, 0, len);
            }
            String line = content.split("\\n")[0];
            String[] arr = line.split("\\s");
            this.url = arr[1].split("\\?")[0];
            this.method = arr[0];
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }
}
