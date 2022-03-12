package cn.moremind.io.tomcat.servlet;


import cn.moremind.io.tomcat.http.MRequest;
import cn.moremind.io.tomcat.http.MResponse;
import cn.moremind.io.tomcat.http.MServlet;

public class SecondServlet extends MServlet {

	public void doGet(MRequest request, MResponse response) throws Exception {
		this.doPost(request, response);
	}

	public void doPost(MRequest request, MResponse response) throws Exception {
		response.write("This is Second Serlvet");
	}

}
