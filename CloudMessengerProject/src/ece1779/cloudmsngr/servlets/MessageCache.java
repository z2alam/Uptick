package ece1779.cloudmsngr.servlets;

import java.io.*;
import java.util.*;

import javax.persistence.Entity;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@Entity(name = "MessageCache")
public class MessageCache implements Serializable {

	private static final int max_entries_ = 1000;
	private Long user_contact_id_;
	private boolean[] from_self_;
	private String[] message_;
	private Date[] time_stamp_;
	private int num_entries_;
	
	public MessageCache(Long user_contact_id) {
		from_self_ = new boolean[max_entries_];
		message_ = new String[max_entries_];
		time_stamp_ = new Date[max_entries_];
		user_contact_id_ = user_contact_id;
		num_entries_ = 0;
	}
	
	public boolean AddNewEntry(boolean from_self, String message, Date timestamp)
	{
		if (num_entries_ >= 1000)
			return false;
		
		from_self_[num_entries_] = from_self;
		message_[num_entries_] = message;
		time_stamp_[num_entries_] = timestamp;
		
		++num_entries_;
		return true;
	}
	
	public int GetNumEntries()
	{
		return num_entries_;
	}
	
	public void ResetCache()
	{
		num_entries_ = 0;
	}
	
	public boolean GetFromSelf(int index)
	{
		return from_self_[index];
	}
	
	public String GetMessage(int index)
	{
		return message_[index];
	}
	
	public Date GetTimestamp(int index)
	{
		return time_stamp_[index];
	}
	
	public boolean AddToCache()
	{
		try {
			MemcacheService msg_cache = MemcacheServiceFactory.getMemcacheService();
			msg_cache.put(user_contact_id_, this);
		}
		catch (MemcacheServiceException ex) {
			return false;
		}
		return true;
	}
	
	public boolean DeleteFromCache()
	{
		try {
			MemcacheService msg_cache = MemcacheServiceFactory.getMemcacheService();
			msg_cache.delete(user_contact_id_);
		}
		catch (MemcacheServiceException ex) {
			return false;
		}
		return true;
	}
	
	public boolean UpdateCache()
	{
		try {
			MemcacheService msg_cache = MemcacheServiceFactory.getMemcacheService();
			msg_cache.delete(user_contact_id_);
			msg_cache.put(user_contact_id_, this);
		}
		catch (MemcacheServiceException ex) {
			return false;
		}
		return true;
	}
	
	// static getter of the messages from Cache.
	public static MessageCache GetMessageHistoryFromCache(Long user_contact_id)
	{
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		MessageCache msgHistory = null;
		try {
            if (memcache.contains(user_contact_id)) {
            	msgHistory = (MessageCache) memcache.get(user_contact_id);
                return msgHistory;
            }
        } catch (MemcacheServiceException e) {
        }
		return msgHistory;
	}
}

