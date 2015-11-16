package edu.rosehulman.extensions.httprequests;

import java.io.File;

import plugins.IPlugin;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

public class MeleeCharacterGetServlet implements IPlugin {

	//private HashMap<String, MeleeCharacter> characters = new HashMap<String, MeleeCharacter>();
	
	public MeleeCharacterGetServlet() {
		/*
		characters.put("Fox", new MeleeCharacter("Fox", true, 1, "Waveshine, Multishine, Firestall, Shine-Spike"));
		characters.put("Falco", new MeleeCharacter("Falco", true, 2, "Waveshine, Multishine, Firestall, Pillar Combo"));
		characters.put("Shiek", new MeleeCharacter("Shiek", false, 3, "Fast-Fallers Chain Grab"));
		characters.put("Marth", new MeleeCharacter("Marth", false, 4, "Ken Combo, Spacies Chain Grab"));
		characters.put("Jigglypuff", new MeleeCharacter("Jigglypuff", false, 5, "Up Throw --> Rest"));
		characters.put("Peach", new MeleeCharacter("Peach", false, 6, "Float Cancel"));
		characters.put("Captain Falcon", new MeleeCharacter("Captain Falcon", true, 7, "Hax Dash, Sacred Combo (Up-Air --> \"The Knee\" --> Falcon Punch)"));
		characters.put("Ice Climbers", new MeleeCharacter("Ice Climbers", false, 8, "Desync, Wobbling, Hand-Off"));
		*/
	}
	
	public HttpResponse process(HttpRequest request, String rootDir) {
		String[] splitString = request.getUri().split("/");
		String fname = splitString[3] + ".json";
		
		HttpResponse response;
		// Combine them together to form absolute file path
		fname = rootDir + System.getProperty("file.separator") + "melee" + System.getProperty("file.separator") + fname;
		System.out.println(fname);
		File file = new File(fname);
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = fname + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					// Lets create 200 OK response
					response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
				}
				else {
					// File does not exist so lets create 404 file not found code
					response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
				}
			}
			else { // Its a file
				// Lets create 200 OK response
				response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
			}
		}
		else {
			// File does not exist so lets create 404 file not found code
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
		return response;
	}
	
}
