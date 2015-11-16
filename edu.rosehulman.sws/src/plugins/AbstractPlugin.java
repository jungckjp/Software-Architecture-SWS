/*
 * IPlugin.java
 * Oct 25, 2015
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

package plugins;

import java.util.HashMap;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Tai J. Enrico (enricotj@rose-hulman.edu)
 */
public abstract class AbstractPlugin implements IPlugin {
	protected HashMap<String, IPlugin> servlets = new HashMap<String, IPlugin>();

	public HttpResponse process(HttpRequest request, String rootDir) {
		HttpResponse response = null;
		String key = request.getUri().split("/")[2];
		if (!servlets.containsKey(key)) {
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		response = servlets.get(key).process(request, rootDir);
		if (response == null) {
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		return response;
	}
}
