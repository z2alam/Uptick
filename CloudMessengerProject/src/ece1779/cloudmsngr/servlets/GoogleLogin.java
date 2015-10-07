package ece1779.cloudmsngr.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.sql.*;


public class GoogleLogin extends HttpServlet {
    
	private static final long serialVersionUID = 1L;	
	
	public void doGet(HttpServletRequest request,
	              HttpServletResponse response)
		throws IOException, ServletException {
		
		UserService userService = UserServiceFactory.getUserService();
		HttpSession userSession = request.getSession();
		userSession.setAttribute("login_url", userService.createLoginURL(request.getRequestURI()));
		userSession.setAttribute("logout_url", userService.createLogoutURL(request.getRequestURI()));
		User user = userService.getCurrentUser();

		response.setContentType("text/html");

		if (user != null) {
			userSession.setAttribute("user_name", user.getEmail());
			response.sendRedirect(response.encodeRedirectURL("/servlet/CloudMessenger"));
		} else {
			userSession.removeAttribute("contact_name");
			userSession.removeAttribute("user_name");
			userSession.removeAttribute("phone_number");
			userSession.removeAttribute("UserContactKey");
			userSession.removeAttribute("ERROR_MSG");

			RequestDispatcher dispatcher = request.getRequestDispatcher("../userLogin.jsp");
			dispatcher.forward(request, response);
		}
    }

    // Do this because the servlet uses both post and get
    public void doPost(HttpServletRequest request, HttpServletResponse response)
       	throws IOException, ServletException {
    }
}
