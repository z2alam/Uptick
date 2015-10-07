package ece1779.cloudmsngr.servlets;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;

import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.Message;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
 
@SuppressWarnings("serial")
public class TextReceiver extends HttpServlet {
 
    public void service(HttpServletRequest request, HttpServletResponse response) 
    		throws IOException, ServletException {
    
        String from_number = request.getParameter("From");
        String rec_message = request.getParameter("Body");
        
        from_number = CloudMessenger.ValidateAndReturnFormattedPhoneNumber(from_number);
        SessionCache contact_session = SessionCache.GetContactSessionFromCache(from_number);
        
        if (contact_session == null) {
	        String err_message = "Sorry! Currently no active session in conversation with you";
	 
	        // Create a TwiML response and add our friendly message.
	        TwiMLResponse twiml = new TwiMLResponse();
	        Message sms = new Message(err_message);
	        try {
	            twiml.append(sms);
	        } catch (TwiMLException e) {
	            e.printStackTrace();
	        }
	        response.setContentType("application/xml");
	        response.getWriter().print(twiml.toXML());
        }
        else {
        	if (rec_message != null) {        		
        		if (rec_message.startsWith("xSTOP")) {
        			String receiver_email = contact_session.GetOwnerId();
        			MessageListener active_session_list = MessageListener.GetActiveSessionListFromCache(receiver_email);
        			
        			if (active_session_list != null) {
        				active_session_list.RemoveSession(from_number);
						active_session_list.UpdateCache();
        			}
        			contact_session.DeleteFromCache();
        			
        			String err_message = "*** Session ended with " + receiver_email + ". Hope the experience was great :) ***";
        			 
        	        // Create a TwiML response and add our friendly message.
        	        TwiMLResponse twiml = new TwiMLResponse();
        	        Message sms = new Message(err_message);
        	        try {
        	            twiml.append(sms);
        	        } catch (TwiMLException e) {
        	            e.printStackTrace();
        	        }
        	        response.setContentType("application/xml");
        	        response.getWriter().print(twiml.toXML());
        	        return;
        		}
        		else {
        			contact_session.PushNewMessage(rec_message);
        		}
        	}
        	else {
        		contact_session.PushNewMessage("what's up,,!");
        	}
        	contact_session.UpdateCache();
        }
    }
}
