package edu.rosehulman.extensions.httprequests;

import plugins.AbstractPlugin;

public class Plugin extends AbstractPlugin {
	
	public Plugin() {
		this.servlets.put("GetServlet", new GetServlet());
		this.servlets.put("PutServlet", new PutServlet());
		this.servlets.put("PostServlet", new PostServlet());
		this.servlets.put("DeleteServlet", new DeleteServlet());
	}
	
}
