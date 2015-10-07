package ece1779.cloudmsngr.servlets;

import java.io.*;
import java.util.*;

import javax.persistence.Entity;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@Entity(name = "SessionCache")
public class SessionCache implements Serializable {

	private String from_ph_number_;
	private String user_email_;
	private Queue<String> message_queue_;
	
	public SessionCache(String from_ph_number, String user_email) {
		from_ph_number_ = from_ph_number;
		user_email_ = user_email;
		message_queue_ = new LinkedList<String>();
	}
	
	public synchronized void PushNewMessage(String message)
	{
		message_queue_.add(message);
	}
	
	public synchronized String PopMessage()
	{
		return message_queue_.remove();
	}
	
	public synchronized String GetOwnerId() {
		return user_email_;
	}

	public synchronized int NumNewMessages()
	{
		return message_queue_.size();
	}
	
	public synchronized boolean AddSessionToCache()
	{
		try {
			MemcacheService msg_cache = MemcacheServiceFactory.getMemcacheService();
			
			if (!msg_cache.contains(from_ph_number_))
				msg_cache.put(from_ph_number_, this);
		}
		catch (MemcacheServiceException ex) {
			return false;
		}
		return true;
	}
	
	public synchronized boolean DeleteFromCache()
	{
		try {
			MemcacheService msg_cache = MemcacheServiceFactory.getMemcacheService();

			if (msg_cache.contains(from_ph_number_))
				msg_cache.delete(from_ph_number_);
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
			
			if (msg_cache.contains(from_ph_number_)) {
				msg_cache.delete(from_ph_number_);
				msg_cache.put(from_ph_number_, this);
			}
		}
		catch (MemcacheServiceException ex) {
			return false;
		}
		return true;
	}
	
	// static getter of the messages from Cache.
	public synchronized static SessionCache GetContactSessionFromCache(String from_ph_number)
	{
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		SessionCache contact_session = null;
		try {
            if (memcache.contains(from_ph_number)) {
            	contact_session = (SessionCache) memcache.get(from_ph_number);
                return contact_session;
            }
        } catch (MemcacheServiceException e) {
        }
		return contact_session;
	}
}

