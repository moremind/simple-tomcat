package cn.moremind.netty.tomcat;

import cn.moremind.netty.tomcat.http.MNRequest;
import cn.moremind.netty.tomcat.http.MNResponse;
import cn.moremind.netty.tomcat.http.MNServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author
 * @description
 * @see
 * @since
 */
public class MNettyTomcat {
    private int port = 8080;

    private ServerSocket server;

    private Map<String, MNServlet> servletMapping = new HashMap<String, MNServlet>();

    private Properties webxml = new Properties();


    // 1.配置好启动端口，默认8080，ServerSocket IP:locahost
    // 2.配置web.xml 自己写的servlet继承HttpServlet
    // servlet-name
    // servlet-class
    // url-pattern

    // 3.读取配置，url-pattern 和 Servlet 建立映射关系
    // Map ServletMapping
    private void init(){

        //加载web.xml文件,同时初始化 ServletMapping对象
        try{
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web-2.properties");

            webxml.load(fis);

            for (Object k : webxml.keySet()) {

                String key = k.toString();
                if(key.endsWith(".url")){
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    MNServlet obj = (MNServlet)Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void start() {
        init();

        // Netty 封装 NIO,Reactor Model, Boss Thread, Worker Thread

        EventLoopGroup boosGroup = new NioEventLoopGroup();

        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // Netty 服务
            // ServerBootstrap ServerSocketChannel
            ServerBootstrap server = new ServerBootstrap();

            // 链路式编程
            server.group(boosGroup, workGroup)
                    // 主线程处理类，底层就是用反射
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 初始化客户端处理
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 无锁串行化编程
                            // Netty 对 HTTP的封装
                            // HttpResponseEncoder 编码器
                            socketChannel.pipeline().addLast(new HttpResponseEncoder());
                            // HttpRequestDecoder 解码器
                            socketChannel.pipeline().addLast(new HttpRequestDecoder());
                            // 业务处理逻辑
                            socketChannel.pipeline().addLast(new MTomcatHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 120)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 启动服务
            ChannelFuture future = server.bind(port).sync();
            System.out.println("M Neety Tomcat已启动，监听端口是：" + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MTomcatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest) msg;

                // 转交给我们自己的request实现
                MNRequest request = new MNRequest(ctx,req);
                // 转交给我们自己的response实现
                MNResponse response = new MNResponse(ctx,req);
                // 实际业务处理
                String url = request.getUrl();

                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request, response);
                }else{
                    response.write("404 - Not Found");
                }

            }
        }
    }

    public static void main(String[] args) {
        new MNettyTomcat().start();
    }
}
