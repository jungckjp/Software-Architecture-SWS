package edu.rosehulman.extensions.httprequests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import plugins.IPlugin;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import server.Server;

public class PutServlet implements IPlugin {

	public HttpResponse process(HttpRequest request, Server server) {
		HttpResponse response;

		String rootDir = server.getRootDirectory();

		String[] splitString = request.getUri().split("/");
		String fname = "";
		for (int i = 3; i < splitString.length; i++) {
			fname += splitString[i];
			if (i < splitString.length - 1) {
				fname += "/";
			}
		}

		// Combine them together to form absolute file path
		fname = rootDir + System.getProperty("file.separator") + fname;
		File file = new File(fname);
		
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = fname + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				try (Writer writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								file.getPath()), "utf-8"))) {
					writer.write(request.getBody().toString());
					writer.close();
				} catch (IOException e1) {
					response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
				}
				// Create 200 OK response
				response = HttpResponseFactory.create200OK(file,
						Protocol.CLOSE);
			}
			else { // Its a file
				// Lets create 200 OK response
				try (Writer writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								file.getPath()), "utf-8"))) {
					writer.write(request.getBody());
					writer.close();
				} catch (IOException e1) {
					response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
				}
				// Create 200 OK response
				response = HttpResponseFactory.create200OK(file,
						Protocol.CLOSE);
			}
		}
		else {
			try (Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(
							file.getPath()), "utf-8"))) {
				writer.write(request.getBody());
				writer.close();
			} catch (IOException e1) {
				response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			}
			// Create 200 OK response
			response = HttpResponseFactory.create200OK(file,
					Protocol.CLOSE);
		}
		return response;
	}

}
