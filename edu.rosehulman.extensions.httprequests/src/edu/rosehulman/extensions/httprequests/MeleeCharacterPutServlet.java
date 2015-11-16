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

public class MeleeCharacterPutServlet implements IPlugin {

	public HttpResponse process(HttpRequest request, String rootDir) {
		HttpResponse response;

		String[] splitString = request.getUri().split("/");
		String fname = "melee" + System.getProperty("file.separator") + splitString[3] + ".json";
		String name = "\"" + splitString[3] + "\"";
		String tier = "\"" + splitString[4] + "\"";
		String rank = splitString[5];
		String ff = splitString[6];
		String tech = "\"" + splitString[7] + "\"";
		tech = tech.replaceAll("%20", " ");
		String body = "{ \"name\":" + name + ", \"tier\":" + tier
				+ ", \"rank\":" + rank + ", \"isFastFaller\":" + ff
				+ ", \"technicalInfo\":" + tech + " }";

		// Combine them together to form absolute file path
		fname = rootDir + System.getProperty("file.separator") + fname;
		File file = new File(fname);

		// Check if the file exists
		if (file.exists()) {
			if (file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = fname + System.getProperty("file.separator")
						+ Protocol.DEFAULT_FILE;
				file = new File(location);
				try (Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file.getPath()), "utf-8"))) {
					writer.write(body);
					writer.close();
				} catch (IOException e1) {
					response = HttpResponseFactory
							.create400BadRequest(Protocol.CLOSE);
				}
				// Create 200 OK response
				response = HttpResponseFactory
						.create200OK(file, Protocol.CLOSE);
			} else { // Its a file
						// Lets create 200 OK response
				try (Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file.getPath()), "utf-8"))) {
					writer.write(body);
					writer.close();
				} catch (IOException e1) {
					response = HttpResponseFactory
							.create400BadRequest(Protocol.CLOSE);
				}
				// Create 200 OK response
				response = HttpResponseFactory
						.create200OK(file, Protocol.CLOSE);
			}
		} else {
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file.getPath()), "utf-8"))) {
				writer.write(body);
				writer.close();
			} catch (IOException e1) {
				response = HttpResponseFactory
						.create400BadRequest(Protocol.CLOSE);
			}
			// Create 200 OK response
			response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
		}
		return response;
	}

}
