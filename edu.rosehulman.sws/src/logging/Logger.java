/*
 * Logger.java
 * Nov 2, 2015
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */

package logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import protocol.HttpRequest;
import protocol.HttpResponse;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class Logger {
	private static Logger inst;
	private String filename;
	private File file;

	private Logger() {
		createLogFile();
	}

	public static Logger getInstance() {
		if (inst == null) {
			inst = new Logger();
		}
		return inst;
	}
	
	private void append(String log) {
		try {
			Files.write(file.toPath(),
					new String((new Date()).toString() + "\t" + log).getBytes(),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	@SuppressWarnings("deprecation")
	private void createLogFile() {
		Date date = new Date();
		int day = date.getDay() + 1;
		int year = date.getYear() + 1900;
		this.filename = date.getMonth() + "-" + day + "-"
				+ year + " @ " + date.getHours() + "-" + date.getMinutes()
				+ "-" + date.getSeconds();
		String slash = System.getProperty("file.separator");
		file = new File("logs" + slash + this.filename + ".txt");
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file.getPath()), "utf-8"))) {
			writer.write(date.toString() + "\t" + "FILE CREATED\n");
			writer.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	public void printLog(String log) {
		if (file != null && file.exists()) {
			append(log);
		}
		else {
			createLogFile();
			append(log);
		}
	}
	
	public void printRequest(HttpRequest request) {
		printLog("REQUEST\t" + request.getMethod() + request.getUri() + "\n");
	}
	
	public void printRequest(HttpRequest request, Socket socket) {
		printLog("REQUEST\t" + request.getMethod() + request.getUri() + "\t" + socket.getInetAddress() + "\n");
	}

	public void printResponse(HttpResponse response) {
		printLog("RESPONSE\t" + response.getPhrase() + " " + response.getStatus() + ":" + response.getFile() + "\n");
	}
	
	public void printResponse(HttpResponse response, Socket socket) {
		printLog("RESPONSE\t" + response.getPhrase() + " " + response.getStatus() + ":" + response.getFile() + "\t" + socket.getInetAddress() + "\n");
	}
}