package cn.moremind.io.tomcat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import cn.moremind.io.tomcat.http.MRequest;
import cn.moremind.io.tomcat.http.MResponse;
import cn.moremind.io.tomcat.http.MServlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author
 * @description
 * @see
 * @since
 */
public class MTomcat {
    // J2EE
    // Servlet
    // Request
    // Response
    private int port = 8080;

    private ServerSocket server;

    private Map<String, MServlet> servletMapping = new HashMap<String,MServlet>();

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
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");

            webxml.load(fis);

            for (Object k : webxml.keySet()) {

                String key = k.toString();
                if(key.endsWith(".url")){
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    MServlet obj = (MServlet)Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // 4.发送http请求，发送的数据就是字符串，有规律的字符串(Http 协议)

    // 5.从协议内容中拿到URL，把相应的servlet用反射进行实例化
    public void start() {
        init();

        try {
            server = new ServerSocket(this.port);
            System.out.println("MM Tomcat 已启动，监听端口：" + this.port);
            while (true) {
                Socket client = server.accept();

                process(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(Socket client) throws IOException {

        InputStream is = client.getInputStream();
        OutputStream os = client.getOutputStream();

        MRequest request = new MRequest(is);

        MResponse response = new MResponse(os);

        String url = request.getUrl();

        if (servletMapping.containsKey(url)) {
            servletMapping.get(url).service(request, response);
        } else {
            response.write("404 - Not Found");
        }

        os.flush();
        os.close();
        client.close();
    }

    // 6.调用实例化对象的service()方法，执行具体的逻辑

    // 7.Request(InputStream)/Response(OutputStream)

    public static void main(String[] args) {
        new MTomcat().start();
    }

}
