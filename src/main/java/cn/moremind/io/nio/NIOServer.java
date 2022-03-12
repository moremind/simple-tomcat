package cn.moremind.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author
 * @description
 * @see
 * @since
 */
public class NIOServer {

    private int port = 8080;

    // 准备两个东西
    // 轮询器 Selector
    private Selector selector;
    // 缓冲区 Buffer

    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    // 初始化完毕
    public NIOServer(int port) {
        // 初始化
        try {
            this.port = port;
            ServerSocketChannel server = ServerSocketChannel.open();

            // 告诉地址 ip:port
            server.bind(new InetSocketAddress(this.port));

            // BIO升级版本NIO NIO模型默认采用阻塞
            server.configureBlocking(false);

            selector = Selector.open();

            // 开始接受消息
            server.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        System.out.println("listen on:" + this.port);
        try {
            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                // 不断的轮询
                Iterator<SelectionKey> iter = keys.iterator();
                // 同步体现在这里，因为每次只能处理一种状态
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    process(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 每一次轮询就是调用一次process方法，而每一次调用，都只能干一件事
    private void process(SelectionKey key) throws IOException {
        // 针对每种状态给一个反应
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            // 这个方法体现非阻塞，不管你数据有没有准备好，都要给我一个状态和反馈
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            // 当数据准备就绪的时候，将状态改为可读
            key = channel.register(selector, SelectionKey.OP_READ);
        }
        else if (key.isReadable()) {
            // key.channel 多路复用器中拿到客户端的引用
            SocketChannel channel = (SocketChannel) key.channel();
            int len = channel.read(buffer);
            if (len > 0) {
                buffer.flip();
                String content = new String(buffer.array(), 0, len);
                channel.register(selector, SelectionKey.OP_WRITE);

                // 在key上携带一个附件，一会再写出去
                key.attach(content);
                System.out.println("读取内容" + content);
            }
        }
        else if (key.isWritable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            String content = (String) key.attachment();
            channel.write(ByteBuffer.wrap(("输出:" + content).getBytes()));
            channel.close();
        }
    }

    public static void main(String[] args) {
        new NIOServer(8080).listen();
    }
}
