package cn.moremind.io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * @author
 * @description
 * @see
 * @since
 */
public class BIOClient {

    public static void main(String[] args) throws IOException {

        // 要和谁进行通信，IP:PORT
        Socket client = new Socket("localhost", 8080);

        // 不管是客户端还是服务端，都有可能write和read
        OutputStream os = client.getOutputStream();

        String name = UUID.randomUUID().toString();

        System.out.println("客户端发送数据" + name);

        os.write(name.getBytes());

        os.close();

        client.close();
    }
}
