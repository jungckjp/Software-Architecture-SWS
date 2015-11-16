/*
 * RequestManger.java
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

package pluginmanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import server.Server;
import server.SockPuppet;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class RequestManager implements Runnable {

	private final static String REQUEST_QUEUE_NAME = "requestQueue";
	private final static String RESPONSE_QUEUE_NAME = "responseQueue";

	private static RequestManager instance;
	private PriorityQueue<PriorityRequest> queue = new PriorityQueue<PriorityRequest>();
	private HashMap<String, Long> throttleMap = new HashMap<String, Long>();
	private long throttleRate = 1000;
	private Connection requestConnection;
	private Channel requestChannel;
	private Connection responseConnection;
	private Channel responseChannel;
	private long currentRequestId = 0;
	
	private HashMap<Long, Socket> sockDrawer = new HashMap<Long, Socket>();

	private RequestManager() {
	}

	public static RequestManager getInstance() {
		if (instance == null) {
			instance = new RequestManager();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		// RESPONSE QUEUE STUFF
		try {
			responseConnection = factory.newConnection();
			responseChannel = responseConnection.createChannel();
			responseChannel.queueDeclare(RESPONSE_QUEUE_NAME, false, false, false, null);
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		} catch (IOException | TimeoutException e2) {
			e2.printStackTrace();
		}
		
		Consumer consumer = new DefaultConsumer(responseChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				HttpResponse response = (HttpResponse) SerializationUtils.deserialize(body);
				try {
					response.write(sockDrawer.get(response.id).getOutputStream());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		try {
			responseChannel.basicConsume(RESPONSE_QUEUE_NAME, true, consumer);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		//REQUEST QUEUE STUFF
		try {
			requestConnection = factory.newConnection();
			requestChannel = requestConnection.createChannel();
			requestChannel.queueDeclare(REQUEST_QUEUE_NAME, false, false, false, null);
		} catch (IOException | TimeoutException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Request Manger Running");
		while (true) {
			if (!queue.isEmpty()) {
				System.out.println("Processing");
				PriorityRequest request = queue.poll();
				byte[] data = SerializationUtils.serialize(request);
				try {
					requestChannel.basicPublish("", REQUEST_QUEUE_NAME, null, data);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				//response.write(socket.getOutputStream());
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void process(HttpRequest request, Server server, Socket socket, long start) {
		boolean throttled = false;
		if (throttleMap.containsKey(socket.getInetAddress().toString())) {
			long last = throttleMap.get(socket.getInetAddress().toString());
			long diff = start - last;
			if (diff < throttleRate) {
				server.incrementConnections(1);
				long end = System.currentTimeMillis();
				server.incrementServiceTime(end - start);
				System.out.println("THROTTLED " + diff);
				throttled = true;
			}
		}
		throttleMap.put(socket.getInetAddress().getHostAddress(), start);

		System.out.println("Adding to queue");
		if (throttled) {
			System.out.println("Throttled request added");
		}
		System.out.println(server.getPort());
		queue.add(new PriorityRequest(request, server.getRootDirectory(), currentRequestId, start, throttled));
		sockDrawer.put(currentRequestId, socket);
		currentRequestId++;
		System.out.println("Queue is this large " + queue.size());
	}

}
