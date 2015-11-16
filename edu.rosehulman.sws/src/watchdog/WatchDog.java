/*
 * WatchDog.java
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
 
package watchdog;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import gui.WebServer;
import pluginmanager.PluginManager;
import pluginmanager.RequestManager;

public class WatchDog extends TimerTask {
	
	public WatchDog() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, new Date(), 2000);
	}

	public void run() {
		String host = "localhost";
		int port = 8080;
		try {
			//System.out.println("Testing Connection...");
			@SuppressWarnings({ "unused", "resource" })
			Socket socket = new Socket(host, port);
		} catch (IOException e) {
			System.out.println("Starting new server!");
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					WebServer server = new WebServer();
					server.setVisible(true);
					server.butStartServer.doClick();
				}
			});
		}

	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new WatchDog();
				new Thread(PluginManager.getInstance()).start();
				new Thread(RequestManager.getInstance()).start();
			}
		});
	}
}
