package cn.moremind.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author
 * @description
 * @see
 * @since
 */
public class BIOServer {
    ServerSocket serverSocket;

    public BIOServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("BIO服务已启动，监听端口是：" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() throws IOException {
        while (true) {
            // 等待客户端连接，阻塞方法
            // Socket数据发送者在服务端的引用
            Socket client = serverSocket.accept();

            InputStream is = client.getInputStream();

            // 网络的客户端把数据发送到网卡，机器所得到的数据读到JVM中
            byte[] buff = new byte[1024];

            int len = is.read(buff);

            if (len > 0) {
                String msg = new String(buff, 0, len);
                System.out.println("收到" + msg);
            }
        }
    }

    public static void main(String[] args) {
        try {
            new BIOServer(8080).listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
