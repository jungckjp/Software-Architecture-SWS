/*
 * PriorityRequest.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import protocol.HttpRequest;
import protocol.Protocol;
import server.SockPuppet;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class PriorityRequest implements Comparable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8630490911429311051L;
	/**
	 * 
	 */
	private int rank;
	public HttpRequest request;
	public String rootDir;
	public long id;
	public long start;

	public PriorityRequest(HttpRequest request, String rootDir, long id, long start, boolean throttled) {
		this.request = request;
		this.rootDir = rootDir;
		this.id = id;
		this.start = start;

		if (this.request.getMethod().equals(Protocol.GET)) {
			rank = 1;
		} else if (this.request.getMethod().equals(Protocol.DELETE)) {
			rank = 2;
		} else if (this.request.getMethod().equals(Protocol.PUT)) {
			rank = 3;
		} else {
			rank = 4;
		}
		if (throttled) {
			rank += 4;
		}
	}

	public int getRank() {
		return rank;
	}

	@Override
	public int compareTo(Object obj) {
		PriorityRequest otherRequest = (PriorityRequest) obj;
		return this.rank - otherRequest.getRank();
	}

	public byte[] getBytes() {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			try {
				bos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return bytes;
	}
}
