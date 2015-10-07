package ece1779.cloudmsngr.servlets;

import javax.servlet.http.*;
import javax.servlet.ServletConfig;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Initialization extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) {
    }
}
