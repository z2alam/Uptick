package ece1779.cloudmsngr.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.sdk.verbs.Message;

import java.util.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.xmpp.*;
import com.google.appengine.repackaged.org.joda.time.DateTime;

@SuppressWarnings("serial")
public class CloudMessenger extends HttpServlet {
    
	private static final long serialVersionUID = 1L;

	static String ACCOUNT_SID = "ACae9265e9df7d40fafaf2f57a9c5103a6";
	static String AUTH_TOKEN = "7d474963c0eb5b74d955ab89ed1baacf";

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		HttpSession userSession = request.getSession();
		
		userSession.removeAttribute("ERROR_MSG");

		/* User is not logged in */
		if (userSession.getAttribute("user_name") == null) {
			response.sendRedirect(response.encodeRedirectURL("/servlet/GoogleLogin"));
			return;
		}
		
		String user_email = userSession.getAttribute("user_name").toString();
		MessageListener active_session_list = MessageListener.GetActiveSessionListFromCache(user_email);
		if (active_session_list == null) {
			active_session_list = StartMessageListener(user_email);
			active_session_list.AddActiveSessionListToCache();
		}
		active_session_list.LookupMessageQueue();

		RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
		dispatcher.forward(request, response);
	}

    // Do this because the servlet uses both post and get
    public void doPost(HttpServletRequest request, HttpServletResponse response)
       	throws IOException, ServletException {

    	HttpSession userSession = request.getSession();
    	userSession.removeAttribute("ERROR_MSG");
		
    	/* User is not logged in */
		if (userSession.getAttribute("user_name") == null) {
			response.sendRedirect(response.encodeRedirectURL("/servlet/GoogleLogin"));
			return;
		}
    	
    	String submit_type = request.getParameter("submit");
		if (submit_type != null) {

			if (submit_type.equals("SelectNum")) {
				String phone_number = request.getParameter("phoneNumber");
				
				/*
				 * ..................VALIDATING PHONE NUMBER..................
				 * API calls are made from: "com.google.i18n.phonenumbers"
				 */
				phone_number = ValidateAndReturnFormattedPhoneNumber(phone_number);
				if (phone_number == null) {
					userSession.setAttribute("ERROR_MSG", "Unfortunately, only CANADIAN numbers are supported.");
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
				}
				
				/*
				 * ..................OBTAINING KEY FOR THE GIVEN PH.NUMBER..................
				 * API calls are made from: "com.google.appengine.api.datastore"
				 */
				UserContactInfo info = ObtainUserContactKeyFromNumber(phone_number, userSession.getAttribute("user_name").toString(), null);
				Key key = info.GetUserContactId();
				userSession.setAttribute("UserContactKey", key.getId());
				userSession.setAttribute("contact_name", info.GetContactName());
				
				if (userSession.getAttribute("contact_name").toString().equals("")) {
					userSession.setAttribute("contact_name", phone_number);
				}
				userSession.setAttribute("phone_number", phone_number);
				
				
				/*
				 * ..................PUSH ALL THE MESSAGE HISTORY TO CACHE..................
				 * API calls are made from: "com.google.appengine.api.memcache"
				 */
				if (PushMessageHistoryToCache(key.getId()) == false) {
					// May be throw a warning??
				}
			}

			//Select a different Contact
			else if (submit_type.startsWith("selectContact")) {

				String[] sp = submit_type.split(":");
				String selected_contact = sp[1];
				

				UserContactInfo info = ObtainUserContactKeyFromContact(userSession.getAttribute("user_name").toString(), selected_contact);
				Key key = info.GetUserContactId();
				
				userSession.setAttribute("contact_name", selected_contact);	
				userSession.setAttribute("UserContactKey", key.getId());
				userSession.setAttribute("phone_number", info.GetContactNumber());
				
				
				/*
				 * ..................PUSH ALL THE MESSAGE HISTORY TO CACHE..................
				 * API calls are made from: "com.google.appengine.api.memcache"
				 */
				if (PushMessageHistoryToCache(key.getId()) == false) {
					// May be throw a warning??
				}
				
				userSession.setAttribute("ERROR_MSG", "Contact: "+ userSession.getAttribute("contact_name") + " Number: " 
						+ userSession.getAttribute("phone_number") + " set successfully");
				RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
				dispatcher.forward(request, response);
				return;
			}
			
			else if (submit_type.equals("SendMessage")) {				
				boolean MessageSendSuccess = true;
				String phone_number = (String)userSession.getAttribute("phone_number");
				String user_email = userSession.getAttribute("user_name").toString();
				SessionCache contact_session = SessionCache.GetContactSessionFromCache(phone_number);

				if (contact_session == null || !contact_session.GetOwnerId().equals(user_email)) {
					userSession.setAttribute("ERROR_MSG", "Please first start session before sending message");
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
					return;
				}
				
				/*
				 * ..................SENDING TEXT MESSAGE..................
				 * API calls are made from: "com.twilio.sdk"
				 */
				String message = request.getParameter("messageToSend");
				MessageSendSuccess = SendTextMessage(phone_number, message);
				if (MessageSendSuccess == false) {
					userSession.setAttribute("ERROR_MSG", "Failed to send text to: " + phone_number);
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
					return;
				}
				
				/*
				 * ..................STORING IN DATABASE(DATASTORE)..................
				 * API calls are made from: "GAE datastore and memcache"
				 */
				if (userSession.getAttribute("UserContactKey") != null) {
					Long keyId = (Long) userSession.getAttribute("UserContactKey");
					boolean StoreMessageSuccess = UpdateTextMessageInDbAndCache(message, keyId, true);
					if (StoreMessageSuccess == false) {
						userSession.setAttribute("ERROR_MSG", "Failed to store conversation");
						RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
						dispatcher.forward(request, response);
					}
				}
				else {
					userSession.setAttribute("ERROR_MSG", "Please select a PhoneNumber or a Contact");
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
				}
			}
			
			else if (submit_type.equals("StartSession")) {
				String contact_number = userSession.getAttribute("phone_number").toString();
				String contact_name = userSession.getAttribute("contact_name").toString();
				String user_email = userSession.getAttribute("user_name").toString();
				SessionCache contact_session = SessionCache.GetContactSessionFromCache(contact_number);
				MessageListener active_session_list = MessageListener.GetActiveSessionListFromCache(user_email);
				
				contact_name = contact_name.equals("") ? contact_number : contact_name;

				if (contact_session != null) {
					// meaning there is an already existing session for this contact number
					
					// checking if you are the owner of this session, then no need to re-start session..
					if (contact_session.GetOwnerId().equals(user_email)) {
						userSession.setAttribute("ERROR_MSG", "Session is already running between you and " + contact_name);
					}
					else {
						userSession.setAttribute("ERROR_MSG", "Sorry! contact '" + contact_name + "' is currently busy..");
					}
				}
				else {
					// start session..
					contact_session = new SessionCache(contact_number, user_email);
					contact_session.AddSessionToCache();

					if (active_session_list == null) {
						active_session_list = StartMessageListener(user_email);
						active_session_list.AddActiveSessionListToCache();
					}
					
					active_session_list.AddSession(contact_number);
					active_session_list.UpdateCache();

					userSession.setAttribute("ERROR_MSG", "Session started with " + contact_name);
					
					String session_start_msg = "*** Session started with " + user_email + ". Reply back 'xSTOP' anytime to close the session ***";
					SendTextMessage(contact_number, session_start_msg);
				}
			}
			
			else if (submit_type.equals("StopSession")) {
				String contact_number = userSession.getAttribute("phone_number").toString();
				String contact_name = userSession.getAttribute("contact_name").toString();
				String user_email = userSession.getAttribute("user_name").toString();
				SessionCache contact_session = SessionCache.GetContactSessionFromCache(contact_number);
				MessageListener active_session_list = MessageListener.GetActiveSessionListFromCache(user_email);
				
				contact_name = contact_name.equals("") ? contact_number : contact_name;

				if (contact_session != null) {
					
					// you're supposed to be the owner of this session.
					if (contact_session.GetOwnerId().equals(user_email)) {
						// release ownership of this session. (or simply end session)
						contact_session.DeleteFromCache();
						
						if (active_session_list == null) {
							active_session_list = StartMessageListener(user_email);
							active_session_list.AddActiveSessionListToCache();
						}
						
						active_session_list.RemoveSession(contact_number);
						active_session_list.UpdateCache();
						userSession.setAttribute("ERROR_MSG", "Session ended with " + contact_name);

						String session_stop_msg = "*** Session ended with " + user_email + ". Hope the experience was great :) ***";
						SendTextMessage(contact_number, session_stop_msg);
					}
					else {
						userSession.setAttribute("ERROR_MSG", "Invalid state. You're stopping session with " + contact_name + " without starting.");
					}
				}
				else {
					userSession.setAttribute("ERROR_MSG", "Invalid state. You're stopping session with " + contact_name + " without starting.");
				}
			}

			else if (submit_type.equals("AddNumber")) {
				String new_phone_number = request.getParameter("newPhoneNumber");
				String new_contact = request.getParameter("newContactName");
				
				new_phone_number = ValidateAndReturnFormattedPhoneNumber(new_phone_number);
				if (new_phone_number == null) {
					userSession.setAttribute("ERROR_MSG", "Unfortunately, only CANADIAN numbers are supported.");
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
					return;
				}
				if (new_contact.equals("")) {
					userSession.setAttribute("ERROR_MSG", "Please Enter a valid Contact name");
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
					return;
				}
				
				UserContactInfo info = ObtainUserContactKeyFromNumber(new_phone_number, userSession.getAttribute("user_name").toString(), new_contact);
				
				if (info.GetContactName().equals(new_contact)){
					userSession.setAttribute("ERROR_MSG", new_contact +" has been added to your Contacts");
				}
				else {
					userSession.setAttribute("ERROR_MSG", "We are sorry, a contact under this number already exists!");
				}
			}	

			//Delete Contact History
			else if (submit_type.startsWith("deleteHistory")) {
				
				String[] sp = submit_type.split(":");
				String contact_ = sp[1];
				
				UserContactInfo info = ObtainUserContactKeyFromContact(userSession.getAttribute("user_name").toString(), contact_);
				Key key = info.GetUserContactId();
				
				if (key == null){
					userSession.setAttribute("ERROR_MSG", "Error deleting history - Contact "+ contact_ +" not found!");
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
					return;
				}

		    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		    	
		    	
				@SuppressWarnings("deprecation")
				Query contact_name_query = new Query("MessageHistory")
											.addFilter("user_contact_id", FilterOperator.EQUAL, key.getId());
				PreparedQuery pq2 = ds.prepare(contact_name_query);
				
				Key key2 = null;
				for (Entity en:pq2.asIterable()) {
					key2 = en.getKey();
					if (key2 != null){
						ds.delete(key2);
						MessageCache cache = MessageCache.GetMessageHistoryFromCache(key.getId());
				    	if (cache != null) {
				    		cache.DeleteFromCache();
				    		//cache.ResetCache();
				    	}
					}
				}
			}
			
			//Delete Contact
			else if (submit_type.startsWith("deleteContact")) {
				
				String[] sp = submit_type.split(":");
				String contact_to_delete = sp[1];
				String current_contact_name = userSession.getAttribute("contact_name").toString();
				
		    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		    	
				@SuppressWarnings("deprecation")
				Query phone_num_query = new Query("UserContacts")
											.addFilter("user_email", FilterOperator.EQUAL, userSession.getAttribute("user_name").toString())
											.addFilter("contact_name", FilterOperator.EQUAL, contact_to_delete);
				PreparedQuery pq = ds.prepare(phone_num_query);
				
				Key key = null;
				for (Entity en:pq.asIterable()) {
					key = en.getKey();
				}
				if (key == null) {
					userSession.setAttribute("ERROR_MSG", "Error Deleting - Contact does not exist in Database");
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
					return;
				}
				else if(contact_to_delete.equals(current_contact_name)){
					userSession.setAttribute("ERROR_MSG", "Error Deleting - There is a current session going with this Contact!");
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
					return;
				}
				else {
					ds.delete(key);
					userSession.setAttribute("ERROR_MSG", contact_to_delete +" has been deleted from your Contacts");
					RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}
			
			else if (submit_type.equals("Logout")) {
				userSession.removeAttribute("contact_name");
				userSession.removeAttribute("user_name");
				userSession.removeAttribute("phone_number");
				userSession.removeAttribute("UserContactKey");
				userSession.removeAttribute("ERROR_MSG");

				response.sendRedirect(response.encodeRedirectURL("/servlet/GoogleLogin"));
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("../CloudMessenger.jsp");
		dispatcher.forward(request, response);
    }
    
    public MessageListener StartMessageListener(String user_email) {
    	MessageListener listener_thread = new MessageListener(user_email);
		listener_thread.LookupMessageQueue(); 
		return listener_thread;
    }
    
    public static String ValidateAndReturnFormattedPhoneNumber(String phone_number) {
    	
    	PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    	// Append +1 in the phone number, if not existing.
		// Check validity of the phone number, and check if they are CANADIAN.
		// 2-digit ISO_3166-1 code -- only for Canada "CA"
		PhoneNumber number = null;
		try {
			number = phoneUtil.parseAndKeepRawInput(phone_number, "CA");
		} catch (NumberParseException e) {
			return null;
		}
		
		// Check if the number is Canadian.
		if (number != null && phoneUtil.isValidNumberForRegion(number, "CA")) {
			System.out.println(number + " : " + phone_number);
		}
		else {
			return null;
		}
		
		if (phone_number.length() == 0)
			return null;
		
		// Add +1 if not existing.
		if (phone_number.length() == 10) {
			phone_number = "+1" + phone_number;
		}
		return phone_number;
    }
   
    public static UserContactInfo ObtainUserContactKeyFromNumber(String phone_number, String self_email_add, String contact_name)
    {
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	String saved_contact_name = "";
    	
    	// if the phone number is valid, then check if there is a conversation history with this
		// phone number. If not then create one.
    	
		@SuppressWarnings("deprecation")
		Query phone_num_query = new Query("UserContacts")
									.addFilter("user_email", FilterOperator.EQUAL, self_email_add)
									.addFilter("contact_number", FilterOperator.EQUAL, phone_number);
		PreparedQuery pq = ds.prepare(phone_num_query);
		
		// Obtain key of the corresponding user-contact.. otherwise add a new one.
		Key key = null;
		for (Entity en:pq.asIterable()) {
			key = en.getKey();
			saved_contact_name = en.getProperty("contact_name").toString();
			if (saved_contact_name.equals("") && (contact_name!=null)) {
				en.setProperty("contact_name", contact_name);
				saved_contact_name = contact_name;
				ds.put(en);
			}
		}
		
		if (key == null) {
			// begin a transaction.. 
			Transaction tx = ds.beginTransaction();
			saved_contact_name = (contact_name == null) ? "" : contact_name;
			
			// create a new entry for this contact
			Entity entity = new Entity("UserContacts");
			try {
				entity.setProperty("user_email", self_email_add);
				entity.setProperty("contact_name", saved_contact_name);
				entity.setProperty("contact_number", phone_number);
				
				// push the entity to datastore.
				ds.put(entity);
				tx.commit();
			}
			finally {
				if (tx.isActive()) {
					tx.rollback();
				}
			}
			
			// get the newly generated key
			key = entity.getKey();
		}
		
		UserContactInfo info = new UserContactInfo(saved_contact_name, phone_number, key);
		return info;
    }
    
    //Get Key from Contact Name
    public static UserContactInfo ObtainUserContactKeyFromContact(String self_email_add, String contact_name)
    {
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	//String saved_contact_name = "";
    	String phone_number = "";
    	
    	// if the phone number is valid, then check if there is a conversation history with this
		// phone number. If not then create one.
    	
		@SuppressWarnings("deprecation")
		Query contact_name_query = new Query("UserContacts")
									.addFilter("user_email", FilterOperator.EQUAL, self_email_add)
									.addFilter("contact_name", FilterOperator.EQUAL, contact_name);
		PreparedQuery pq = ds.prepare(contact_name_query);
		
		// Obtain key of the corresponding user-contact.. otherwise add a new one.
		Key key = null;
		for (Entity en:pq.asIterable()) {
			key = en.getKey();
			phone_number = en.getProperty("contact_number").toString();
		}
				
		UserContactInfo info = new UserContactInfo(contact_name, phone_number, key);
		return info;
    }
    
    static boolean PushMessageHistoryToCache(Long keyId)
    {
    	// Check if the message history is already in cache. Clear it.
    	MessageCache cache = MessageCache.GetMessageHistoryFromCache(keyId);
    	if (cache == null) {
    		cache = new MessageCache(keyId);
    	}
    	else {
    		cache.ResetCache();
    		cache.DeleteFromCache();
    	}
    	
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		@SuppressWarnings("deprecation")
		Query query = new Query("MessageHistory")
									.addFilter("user_contact_id", Query.FilterOperator.EQUAL, keyId)
									.addSort("timestamp", Query.SortDirection.ASCENDING);
		PreparedQuery pq = ds.prepare(query);
		
		for (Entity en:pq.asIterable()) {
			String message = en.getProperty("message").toString();
			boolean fromSelf = Boolean.parseBoolean(en.getProperty("from_self").toString());
			Date dateTime = (Date) en.getProperty("timestamp");
			
			cache.AddNewEntry(fromSelf, message, dateTime);
		}
		cache.AddToCache();
    	return true;
    }
    
    public static boolean UpdateTextMessageInDbAndCache(String message, Long keyId, boolean fromSelf)
    {
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	// begin a transaction.. 
		Transaction tx = ds.beginTransaction();
		java.util.Date date;
		try {
	    	Entity entity = new Entity("MessageHistory");
	    	entity.setProperty("user_contact_id", keyId);
			entity.setProperty("from_self", fromSelf);
			entity.setProperty("message", message);
	
			DateTime now = DateTime.now();
			date = now.toDate();
			entity.setProperty("timestamp", date);
			
			ds.put(entity);
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		
		MessageCache cache = MessageCache.GetMessageHistoryFromCache(keyId);
    	if (cache != null) {
    		cache.AddNewEntry(fromSelf, message, date);
    		cache.UpdateCache();
    	}
    	else {
    		PushMessageHistoryToCache(keyId);
    	}
			
    	return true;
    }
    
    public static boolean SendTextMessage(String phone_number, String message)
    {
    	// Create a rest client
		final TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

		// Get the main account (The one we used to authenticate the client)
		final Account mainAccount = client.getAccount();

		// Send a text message
		final SmsFactory smsFactory = mainAccount.getSmsFactory();
		final Map<String, String> smsParams = new HashMap<String, String>();
		
		smsParams.put("To", phone_number); // The number to send the text to
		smsParams.put("From", "+16475593754"); // A Twilio number
		smsParams.put("Body", message);

		try {
			smsFactory.create(smsParams);
		} catch (TwilioRestException e) {
			return false;
		}
		
		return true;
    }
}