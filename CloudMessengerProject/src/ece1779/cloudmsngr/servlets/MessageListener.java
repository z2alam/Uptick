package ece1779.cloudmsngr.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.persistence.Entity;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@Entity(name = "MessageListener")
public class MessageListener implements Serializable {

	private ArrayList<String> sessions_;
	private String user_email_;
	private boolean init_ = false;

	public MessageListener(String user_email) {
		init_ = false;
		user_email_ = user_email;
		sessions_ = new ArrayList<String>();
	}
	
	public synchronized void AddSession(String session_info)
	{
		if (sessions_ == null)
			return;

		if (sessions_.indexOf(session_info) == -1)
			sessions_.add(session_info);
	}
	
	public synchronized void RemoveSession(String session_info)
	{
		if (sessions_ == null)
			return;

		int idx = sessions_.indexOf(session_info);
		if (idx != -1)  {
			sessions_.remove(idx);
		}
	}
	
	public boolean LookupMessageQueue() {
				
		/*
		Thread thread = ThreadManager.createBackgroundThread(new Runnable() {
			public void run() {
			    try {
			    	while (true) {
			    		boolean new_messages = CheckAndUpdateNewMessagesInDbAndCache();
			    		
			    		if (new_messages) {
			    			// ideally refresh here ..
			    		}
			    		
						Thread.sleep(2000);
					}
			    } catch (InterruptedException ex) {
			    	Thread.currentThread().interrupt();
			    }
			}
		});
		thread.start();
		*/
		boolean new_messages = CheckAndUpdateNewMessagesInDbAndCache();
		
		if (new_messages) {
			// ideally refresh here ..
		}
		
		return true;
	}

	public synchronized boolean CheckAndUpdateNewMessagesInDbAndCache()
	{
		SessionCache cache;
		String message;
		String contact_number;
		String self_email_address;
		
		boolean new_messages = false;

		if (sessions_ == null) {
			return false;
		}
		
		for (int i = 0 ; i < sessions_.size() ; ++i) {
			
			cache = SessionCache.GetContactSessionFromCache(sessions_.get(i));
			if (cache != null) {
				int num = cache.NumNewMessages();
				self_email_address = cache.GetOwnerId();
				
				if (num == 0) {
					// NO NEW MESSAGES
				}
				else {
					for (int j = 0; j < num ; ++j) {
						// push the new messages
						message = cache.PopMessage();
						contact_number = sessions_.get(i);
						
						UserContactInfo info = CloudMessenger.ObtainUserContactKeyFromNumber(contact_number, self_email_address, null);
						CloudMessenger.UpdateTextMessageInDbAndCache(message, info.GetUserContactId().getId(), false);
						
						new_messages = true;
					}
				}
			}
			cache.UpdateCache();
		}	
		return new_messages;
	}
	
	public synchronized boolean AddActiveSessionListToCache()
	{
		try {
			MemcacheService msg_cache = MemcacheServiceFactory.getMemcacheService();
			
			if (!msg_cache.contains(user_email_))
				msg_cache.put(user_email_, this);
		}
		catch (MemcacheServiceException ex) {
			return false;
		}
		return true;
	}
	
	public synchronized boolean DeleteActiveSessionListFromCache()
	{
		try {
			MemcacheService msg_cache = MemcacheServiceFactory.getMemcacheService();

			if (msg_cache.contains(user_email_))
				msg_cache.delete(user_email_);
		}
		catch (MemcacheServiceException ex) {
			return false;
		}
		return true;
	}
	
	public synchronized boolean UpdateCache()
	{
		try {
			MemcacheService msg_cache = MemcacheServiceFactory.getMemcacheService();
			
			if (msg_cache.contains(user_email_)) {
				msg_cache.delete(user_email_);
				msg_cache.put(user_email_, this);
			}
		}
		catch (MemcacheServiceException ex) {
			return false;
		}
		return true;
	}
	
	// static getter of the messages from Cache.
	public synchronized static MessageListener GetActiveSessionListFromCache(String user_email)
	{
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		MessageListener active_session_list = null;
		try {
            if (memcache.contains(user_email)) {
            	active_session_list = (MessageListener) memcache.get(user_email);
                return active_session_list;
            }
        } catch (MemcacheServiceException e) {
        }
		return active_session_list;
	}
}
