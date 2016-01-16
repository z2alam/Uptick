package com.welcome.uptick;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.http.*;
import javax.servlet.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@SuppressWarnings("serial")
public class UptickServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		// check whether user is already logged in
		HttpSession userSession = request.getSession();
		if (userSession.getAttribute("login") == null) {
			userSession.setAttribute("login", "inv");
		}		
		
		response.setContentType("text/html");
		RequestDispatcher dispatcher = request.getRequestDispatcher("../welcome.jsp");
		dispatcher.forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HttpSession userSession = request.getSession();
		String submit_type = request.getParameter("submit");
		
		if (submit_type != null) {
			if (submit_type.equals("logout")) {
				userSession.setAttribute("login", "inv");
			} else if (submit_type.equals("Get Early Access")) {
				String to_id = request.getParameter("request_email");
				String msg = "";
				
				String email_result = SendEmail(to_id);
				if (email_result.equals("success")) {
					msg = "Thank you for registration. Please check your email for sign in instruction.";
				} else {
					msg = email_result;
				}
				userSession.setAttribute("registered", msg);
			} else if (submit_type.equals("Sign in")) {
				String login_id = request.getParameter("login_id");
				String password = request.getParameter("password");
				
				if (login_id.isEmpty() || password.isEmpty()) {
					userSession.setAttribute("registered", "Invalid login");
				} else {
					userSession.setAttribute("login", login_id);
				}
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("../welcome.jsp");
		dispatcher.forward(request, response);
	}
	
	public String SendEmail(String to) {
		String host = "localhost";
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(props);
        
        String msgBody = "<i>Greetings from UpTick!</i><br><br>";
        msgBody += "Thank you for becoming the member. Your membership password is <b><font color=red>abc123</font></b>.";
        msgBody += "Please use this password with your email address to login.";

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("uptick-1186@appspot.gserviceaccount.com", "uptick org"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to, to));
            msg.setSubject("You are successfully registered to Uptick!");
            msg.setContent(msgBody, "text/html");

            Transport.send(msg);
        } catch (AddressException e) {
        	return e.getMessage();
        } catch (MessagingException e) {
        	return e.getMessage();
        } catch (UnsupportedEncodingException e) {
        	return e.getMessage();
		}
        return "success";
	}
}
