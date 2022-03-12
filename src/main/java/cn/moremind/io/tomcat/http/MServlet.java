package cn.moremind.io.tomcat.http;

/**
 * @author
 * @description
 * @see
 * @since
 */
public abstract class MServlet {
    public void service(MRequest request, MResponse response)  {
        try {
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                doGet(request, response);
            } else {
                doPost(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void doPost(MRequest request, MResponse response) throws Exception;

    protected abstract void doGet(MRequest request, MResponse response) throws Exception;
}
