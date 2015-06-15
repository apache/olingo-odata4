/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.ext.spring;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.ODataHttpHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class OlingoSpringServlet extends HttpServlet {

	private ApplicationContext context;

	private ODataHttpHandler httpHandler;

	protected ODataHttpHandler getHttpHandler() throws ServletException {
		Map<String, ODataHttpHandler> odatas = context
				.getBeansOfType(ODataHttpHandler.class);
		if (odatas.size() == 1) {
			return odatas.values().iterator().next();
		}

		throw new ServletException(
				"No OData HTTP handler can be found in the Spring container.");
	}

	protected ApplicationContext initializeApplicationContext(ServletConfig config)
			throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(config
				.getServletContext());

		if (context==null) {
			throw new ServletException(
				"No Spring container is configured within the Web application.");
		}
		
		return context;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init();

		context = initializeApplicationContext(config);

		httpHandler = getHttpHandler();
	}

	@Override
	protected void service(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		System.err.println(">> service - begin - req = "+req.getRequestURL());
		try {
			httpHandler.process(req, resp);
		} catch (RuntimeException e) {
			// TODO: to be improved
			throw new ServletException(e);
		}
		System.err.println(">> service - end");
	}

}
