package edu.rosehulman.extensions.httprequests;

import java.io.File;

import plugins.IPlugin;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

public class DeleteServlet implements IPlugin {

	public HttpResponse process(HttpRequest request, String rootDir) {
		String[] splitString = request.getUri().split("/");
		String fname = "";
		for(int i = 3; i<splitString.length; i++){
			fname+=splitString[i];
			if(i < splitString.length-1){
				fname += "/";
			}
		}
		HttpResponse response;
		// Combine them together to form absolute file path
		fname = rootDir + System.getProperty("file.separator") + fname;
		File file = new File(fname);
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = fname + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					if (file.delete()) {
						response = HttpResponseFactory.create200OK((File)null, Protocol.CLOSE);
					}
					else {
						response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
					}
				}
				else {
					// File does not exist so lets create 404 file not found code
					response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
				}
			}
			else { // Its a file
				if (file.delete()) {
					response = HttpResponseFactory.create200OK((File)null, Protocol.CLOSE);
				}
				else {
					response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
				}
			}
		}
		else {
			// File does not exist so lets create 404 file not found code
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
		return response;
	}

}
