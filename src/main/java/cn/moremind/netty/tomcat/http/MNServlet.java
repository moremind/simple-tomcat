package cn.moremind.netty.tomcat.http;

public abstract class MNServlet {
	
	public void service(MNRequest request, MNResponse response) throws Exception{
		
		//由service方法来决定，是调用doGet或者调用doPost
		if("GET".equalsIgnoreCase(request.getMethod())){
			doGet(request, response);
		}else{
			doPost(request, response);
		}

	}
	
	public abstract void doGet(MNRequest request, MNResponse response) throws Exception;
	
	public abstract void doPost(MNRequest request, MNResponse response) throws Exception;
}
