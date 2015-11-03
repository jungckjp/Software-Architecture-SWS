package edu.rosehulman.watchdog;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import gui.WebServer;

public class WatchDog extends TimerTask {
	   public WatchDog() {
	       Timer timer = new Timer();
	       timer.scheduleAtFixedRate(this, new Date(), 2000);
	   }

	    public void run(){
			String host = "localhost";
			int port = 8080;
			try {
				Socket socket = new Socket(host, port);
			} catch (IOException e) {
				System.out.println("Starting new server!");
				WebServer server = new WebServer();
				server.butStartServer.doClick();
			}
			
	    }

	  public static void main(String[] args) {
	          new WatchDog();
	  }
}
