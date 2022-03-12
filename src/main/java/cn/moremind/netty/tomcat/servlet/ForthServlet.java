package cn.moremind.netty.tomcat.servlet;


import cn.moremind.netty.tomcat.http.MNRequest;
import cn.moremind.netty.tomcat.http.MNResponse;
import cn.moremind.netty.tomcat.http.MNServlet;

public class ForthServlet extends MNServlet {

	public void doGet(MNRequest request, MNResponse response) throws Exception {
		this.doPost(request, response);
	}

	public void doPost(MNRequest request, MNResponse response) throws Exception {
		response.write("This is Second Serlvet");
	}

}
