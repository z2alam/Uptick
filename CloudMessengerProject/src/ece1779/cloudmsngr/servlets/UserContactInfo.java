package ece1779.cloudmsngr.servlets;

import java.io.*;
import java.util.*;

import com.google.appengine.api.datastore.Key;

public class UserContactInfo {

	String contact_name_;
	String contact_number_;
	Key    user_contact_id_;
	
	public UserContactInfo (String contact_name, String contact_number, Key user_contact_id) {
		contact_name_ = contact_name;
		contact_number_ = contact_number;
		user_contact_id_ = user_contact_id;
	}

	public String GetContactName()
	{
		return contact_name_;
	}
	
	public Key GetUserContactId()
	{
		return user_contact_id_;
	}

	public String GetContactNumber()
	{
		return contact_number_;
	}
}