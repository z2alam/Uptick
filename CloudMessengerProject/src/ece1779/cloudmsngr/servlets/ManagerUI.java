package ece1779.cloudmsngr.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class ManagerUI extends HttpServlet {
    
	private static final long serialVersionUID = 1L;	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("../ManagerUI.jsp");
		dispatcher.forward(request, response);
    }

    // Do this because the servlet uses both post and get
    public void doPost(HttpServletRequest request, HttpServletResponse response)
       	throws IOException, ServletException {
    }
}
