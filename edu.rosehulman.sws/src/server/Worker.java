/*
 * Worker.java
 * Nov 11, 2015
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

package server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import pluginmanager.PluginManager;
import pluginmanager.PriorityRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class Worker {

	private final static String REQUEST_QUEUE_NAME = "requestQueue";
	private final static String RESPONSE_QUEUE_NAME = "responseQueue";
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection requestConnection = factory.newConnection();
		Channel requestChannel = requestConnection.createChannel();
		requestChannel.queueDeclare(REQUEST_QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		
		Connection responseConnection = factory.newConnection();
		final Channel responseChannel = responseConnection.createChannel();
		responseChannel.queueDeclare(RESPONSE_QUEUE_NAME, false, false, false, null);

		Consumer consumer = new DefaultConsumer(requestChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				PriorityRequest request = (PriorityRequest) SerializationUtils.deserialize(body);
				System.out.println(" [x] Received");
				System.out.println(request.request.toString());
				System.out.println("*************************************");

				System.out.println(request.request.getMethod());
				HttpResponse response = PluginManager.getInstance().process(request.request, request.rootDir);
				if (response == null) {
					response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
				}
				try {
					System.out.println(request.id);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				response.id = request.id;
				
				byte[] data = SerializationUtils.serialize(response);
				responseChannel.basicPublish("", RESPONSE_QUEUE_NAME, null, data);
			}
		};
		requestChannel.basicConsume(REQUEST_QUEUE_NAME, true, consumer);
	}

}
